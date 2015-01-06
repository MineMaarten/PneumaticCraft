package pneumaticCraft.common.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import pneumaticCraft.api.tileentity.IAirHandler;
import pneumaticCraft.api.tileentity.IManoMeasurable;
import pneumaticCraft.api.tileentity.IPneumaticMachine;
import pneumaticCraft.common.DateEventHandler;
import pneumaticCraft.common.block.tubes.IPneumaticPosProvider;
import pneumaticCraft.common.item.ItemMachineUpgrade;
import pneumaticCraft.common.network.GuiSynced;
import pneumaticCraft.common.network.NetworkHandler;
import pneumaticCraft.common.network.PacketPlaySound;
import pneumaticCraft.common.network.PacketSpawnParticle;
import pneumaticCraft.common.thirdparty.ModInteractionUtils;
import pneumaticCraft.common.thirdparty.computercraft.ILuaMethod;
import pneumaticCraft.common.thirdparty.computercraft.LuaConstant;
import pneumaticCraft.common.thirdparty.computercraft.LuaMethod;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.common.util.TileEntityCache;
import pneumaticCraft.lib.Log;
import pneumaticCraft.lib.ModIds;
import pneumaticCraft.lib.PneumaticValues;
import pneumaticCraft.lib.Sounds;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = ModIds.COMPUTERCRAFT)
public class TileEntityPneumaticBase extends TileEntityBase implements IManoMeasurable, IAirHandler,
        IPneumaticPosProvider, IPeripheral{
    public float maxPressure;
    @GuiSynced
    public int volume;
    public final int DEFAULT_VOLUME;
    public final float DANGER_PRESSURE;
    public final float CRITICAL_PRESSURE;
    @GuiSynced
    public int currentAir;
    public int soundCounter;
    public TileEntity parentTile;
    private TileEntityCache[] tileCache;
    protected List<ILuaMethod> luaMethods = new ArrayList<ILuaMethod>();

    public TileEntityPneumaticBase(float dangerPressure, float criticalPressure, int volume){
        if(volume <= 0) throw new IllegalArgumentException("Volume can't be lower than or equal to 0!");
        DANGER_PRESSURE = dangerPressure;
        CRITICAL_PRESSURE = criticalPressure;
        maxPressure = DANGER_PRESSURE + (CRITICAL_PRESSURE - DANGER_PRESSURE) * (float)Math.random();
        this.volume = volume;
        DEFAULT_VOLUME = volume;
        currentAir = 0;
        addLuaMethods();
    }

    @Override
    public void updateEntity(){
        // volume calculations
        if(!worldObj.isRemote && getUpgradeSlots() != null) {
            int upgradeVolume = getVolumeFromUpgrades(getUpgradeSlots());
            setVolume(DEFAULT_VOLUME + upgradeVolume);

            if(getUpgrades(ItemMachineUpgrade.UPGRADE_SECURITY, getUpgradeSlots()) > 0) {
                int loopCount = 0;
                while(getPressure(ForgeDirection.UNKNOWN) >= DANGER_PRESSURE - 0.1) {
                    airLeak(ForgeDirection.DOWN);
                    if(++loopCount > 1000) break; // if we're still not finished (creative compressor?) its better to explode than bring down the server due to lag
                }
            }
        }

        super.updateEntity();
        // if(!worldObj.isRemote ) System.out.println("currentPressure: " +
        // getPressure());
        for(ForgeDirection pneumaticSide : ForgeDirection.values()) {
            if(!worldObj.isRemote && getPressure(pneumaticSide) > maxPressure) {
                worldObj.createExplosion(null, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, 1.0F, true);
                worldObj.setBlockToAir(xCoord, yCoord, zCoord);
            }
        }
        if(!worldObj.isRemote) disperseAir();
        if(soundCounter > 0) soundCounter--;
    }

    /**
     * Method invoked every update tick which is used to handle air dispersion. It retrieves the pneumatics connecting
     * with this TE, and sends air to it when it has a lower pressure than this TE.
     */
    protected void disperseAir(){
        if(worldObj.isRemote) return;
        disperseAir(getConnectedPneumatics());
    }

    private void disperseAir(List<Pair<ForgeDirection, IPneumaticMachine>> teList){

        boolean shouldRepeat = false;
        List<Pair<Integer, Integer>> dispersion = new ArrayList<Pair<Integer, Integer>>();
        do {
            shouldRepeat = false;
            //Add up every volume and air.
            int totalVolume = getVolume();
            int totalAir = currentAir;
            for(Pair<ForgeDirection, IPneumaticMachine> entry : teList) {
                IAirHandler airHandler = entry.getValue().getAirHandler();
                totalVolume += airHandler.getVolume();
                totalAir += airHandler.getCurrentAir(entry.getKey().getOpposite());
            }
            //Only go push based, ignore any machines that have a higher pressure than this block.
            Iterator<Pair<ForgeDirection, IPneumaticMachine>> iterator = teList.iterator();
            while(iterator.hasNext()) {
                Pair<ForgeDirection, IPneumaticMachine> entry = iterator.next();
                IAirHandler airHandler = entry.getValue().getAirHandler();
                int totalMachineAir = (int)((long)totalAir * airHandler.getVolume() / totalVolume);//Calculate the total air the machine is going to get.
                int airDispersed = totalMachineAir - airHandler.getCurrentAir(entry.getKey().getOpposite());
                if(airDispersed < 0) {
                    iterator.remove();
                    shouldRepeat = true;
                    dispersion.clear();
                    break;
                } else {
                    dispersion.add(new MutablePair(getMaxDispersion(entry.getKey()), airDispersed));
                }
            }
        } while(shouldRepeat);

        int toBeDivided = 0;
        int receivers = dispersion.size();
        for(Pair<Integer, Integer> disp : dispersion) {
            if(disp.getValue() > disp.getKey()) {
                toBeDivided += disp.getValue() - disp.getKey();//Any air that wants to go to a neighbor, but can't (because of regulator module) gives back its air.
                disp.setValue(disp.getKey());
                receivers--;
            }
        }

        while(toBeDivided >= receivers && receivers > 0) {
            int dividedValue = toBeDivided / receivers; //try to give every receiver an equal part of the to be divided air.
            for(Pair<Integer, Integer> disp : dispersion) {
                int maxTransfer = disp.getKey() - disp.getValue();
                if(maxTransfer > 0) {
                    if(maxTransfer <= dividedValue) {
                        receivers--;//next step this receiver won't be able to receive any air.
                    }
                    int transfered = Math.min(dividedValue, maxTransfer);//cap it at the max it can have.
                    disp.setValue(disp.getValue() + transfered);
                    toBeDivided -= transfered;
                }
            }
        }

        for(int i = 0; i < teList.size(); i++) {
            IPneumaticMachine neighbor = teList.get(i).getValue();
            int transferedAir = dispersion.get(i).getValue();

            onAirDispersion(transferedAir, teList.get(i).getKey());
            neighbor.getAirHandler().addAir(transferedAir, teList.get(i).getKey().getOpposite());
            addAir(-transferedAir, teList.get(i).getKey());
        }
    }

    /**
     * Method fired to get the maximum air allowed to disperse to this side. Used in the regulator tube to prevent air from travelling.
     * @param key
     * @return
     */
    protected int getMaxDispersion(ForgeDirection side){
        return Integer.MAX_VALUE;
    }

    /**
     * Method fired when air disperses. Overriden in the Flow Detection Tube to calculate the air passed through.
     * return amount that is allowed to be dispersed.
     * @param amount of air being dispersed.
     * @param side
     * @return
     */
    protected void onAirDispersion(int amount, ForgeDirection side){}

    private boolean shouldThrottleEffects(){
        return(soundCounter > 0);
    }

    /**
     * Method to release air in the air. It takes air from a specific side, plays a sound effect, and spawns smoke particles.
     * @param side
     */
    @Override
    public void airLeak(ForgeDirection side){
        if(worldObj.isRemote || Math.abs(getPressure(side)) < 0.01F) return;
        double motionX = side.offsetX;
        double motionY = side.offsetY;
        double motionZ = side.offsetZ;
        if(!shouldThrottleEffects()) {
            soundCounter = 20;
            NetworkHandler.sendToAllAround(new PacketPlaySound(Sounds.LEAKING_GAS_SOUND, xCoord, yCoord, zCoord, 0.1F, 1.0F, true), worldObj);
        }

        if(getPressure(side) < 0) {
            double speed = getPressure(side) * 0.1F - 0.1F;
            if(!shouldThrottleEffects()) NetworkHandler.sendToAllAround(new PacketSpawnParticle("smoke", xCoord + 0.5D + motionX / 2D, yCoord + 0.5D + motionY / 2D, zCoord + 0.5D + motionZ / 2D, motionX * speed, motionY * speed, motionZ * speed), worldObj);

            int dispersedAmount = -(int)(getPressure(side) * PneumaticValues.AIR_LEAK_FACTOR) + 20;
            if(getCurrentAir(side) > dispersedAmount) dispersedAmount = -getCurrentAir(side);
            onAirDispersion(dispersedAmount, side);
            addAir(dispersedAmount, side);
        } else {
            double speed = getPressure(side) * 0.1F + 0.1F;
            if(!shouldThrottleEffects()) {
                if(DateEventHandler.isEvent()) {
                    DateEventHandler.spawnFirework(worldObj, xCoord + 0.5D + motionX / 2D, yCoord + 0.5D + motionY / 2D, zCoord + 0.5D + motionZ / 2D);
                } else {
                    NetworkHandler.sendToAllAround(new PacketSpawnParticle("smoke", xCoord + 0.5D + motionX / 2D, yCoord + 0.5D + motionY / 2D, zCoord + 0.5D + motionZ / 2D, motionX * speed, motionY * speed, motionZ * speed), worldObj);
                }
            }

            int dispersedAmount = (int)(getPressure(side) * PneumaticValues.AIR_LEAK_FACTOR) + 20;
            if(dispersedAmount > getCurrentAir(side)) dispersedAmount = getCurrentAir(side);
            onAirDispersion(-dispersedAmount, side);
            addAir(-dispersedAmount, side);
        }
    }

    /**
        * Retrieves a list of all the connecting pneumatics. It takes sides in account.
        * @return
        */
    @Override
    public List<Pair<ForgeDirection, IPneumaticMachine>> getConnectedPneumatics(){
        List<Pair<ForgeDirection, IPneumaticMachine>> teList = new ArrayList<Pair<ForgeDirection, IPneumaticMachine>>();
        for(ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity te = getTileCache()[direction.ordinal()].getTileEntity();
            IPneumaticMachine machine = ModInteractionUtils.getInstance().getMachine(te);
            if(machine != null && isConnectedTo(direction) && machine.isConnectedTo(direction.getOpposite())) {
                teList.add(new ImmutablePair(direction, machine));
            }
        }
        return teList;
    }

    public TileEntityCache[] getTileCache(){
        if(tileCache == null) tileCache = TileEntityCache.getDefaultCache(worldObj, xCoord, yCoord, zCoord);
        return tileCache;
    }

    /**
     * Returns if TE's is connected to the given side of this TE.
     * @param side
     * @return
     */
    @Override
    public boolean isConnectedTo(ForgeDirection side){
        if(parentTile == null) {
            return true;
        } else if(ModInteractionUtils.getInstance().isMultipart(parentTile)) {
            return ModInteractionUtils.getInstance().isMultipartWiseConnected(parentTile, side);
        } else {
            return ((IPneumaticMachine)parentTile).isConnectedTo(side);
        }
    }

    /**
     * Adds air to the tank of the given side of this TE. It also updates clients where needed.
     * @param amount
     * @param side
     */
    @Override
    @Deprecated
    public void addAir(float amount, ForgeDirection side){
        addAir((int)amount, side);
    }

    @Override
    public void addAir(int amount, ForgeDirection side){
        currentAir += amount;
    }

    @Override
    @Deprecated
    public void setVolume(float newVolume){
        setVolume((int)newVolume);
    }

    /**
     * Sets the volume of this TE's air tank. When the volume decreases the pressure will remain the same, meaning air will
     * be lost. When the volume increases, the air remains the same meaning the pressure will drop.
     * Used in the Volume Upgrade calculations.
     * @param newVolume
     */
    @Override
    public void setVolume(int newVolume){
        if(newVolume <= 0) throw new IllegalArgumentException("Volume can't be lower or equal than 0!");
        if(newVolume < volume) currentAir = (int)(currentAir * (float)newVolume / volume); // lose air when we decrease in volume.
        volume = newVolume;
    }

    @Override
    public float getPressure(ForgeDirection sideRequested){
        return (float)currentAir / volume;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        if(getClass() != TileEntityPneumaticBase.class && saveTeInternals()) {
            super.readFromNBT(nbt);
        }
        if(nbt.hasKey("pneumatic")) nbt = nbt.getCompoundTag("pneumatic");
        currentAir = nbt.getInteger("currentAir");
        maxPressure = nbt.getFloat("maxPressure");
        volume = nbt.getInteger("volume");
        if(volume == 0) {
            Log.error("Volume was 0! Assigning default");
            volume = DEFAULT_VOLUME;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt){
        if(getClass() != TileEntityPneumaticBase.class && saveTeInternals()) {
            super.writeToNBT(nbt);
        }
        nbt.setInteger("currentAir", currentAir);
        nbt.setInteger("volume", volume);
        nbt.setFloat("maxPressure", maxPressure);

        NBTTagCompound tag = new NBTTagCompound();
        nbt.setTag("pneumatic", tag);
        nbt = tag;
        nbt.setInteger("currentAir", currentAir);
        nbt.setInteger("volume", volume);
        nbt.setFloat("maxPressure", maxPressure);
    }

    protected boolean saveTeInternals(){
        return true;
    }

    protected int getVolumeFromUpgrades(int[] upgradeSlots){
        return getUpgrades(ItemMachineUpgrade.UPGRADE_VOLUME_DAMAGE, upgradeSlots) * PneumaticValues.VOLUME_VOLUME_UPGRADE;
    }

    @Override
    public void printManometerMessage(EntityPlayer player, List<String> curInfo){
        curInfo.add(EnumChatFormatting.GREEN + "Current pressure: " + PneumaticCraftUtils.roundNumberTo(getPressure(ForgeDirection.UNKNOWN), 1) + " bar.");
    }

    @Override
    public int getVolume(){
        return volume;
    }

    @Override
    public float getMaxPressure(){
        return maxPressure;
    }

    @Override
    public int getCurrentAir(ForgeDirection sideRequested){
        return currentAir;
    }

    @Override
    public IAirHandler getAirHandler(){
        return this;
    }

    @Override
    public int getXCoord(){
        return xCoord;
    }

    @Override
    public int getYCoord(){
        return yCoord;
    }

    @Override
    public int getZCoord(){
        return zCoord;
    }

    @Override
    public void onNeighborTileUpdate(){
        super.onNeighborTileUpdate();
        for(TileEntityCache cache : getTileCache()) {
            cache.update();
        }
    }

    /*
     * COMPUTERCRAFT API 
     */

    @Override
    @Optional.Method(modid = ModIds.COMPUTERCRAFT)
    public String getType(){
        return "pneumaticMachine";
    }

    @Override
    @Optional.Method(modid = ModIds.COMPUTERCRAFT)
    public String[] getMethodNames(){
        String[] methodNames = new String[luaMethods.size()];
        for(int i = 0; i < methodNames.length; i++) {
            methodNames[i] = luaMethods.get(i).getMethodName();
        }
        return methodNames;
    }

    @Override
    @Optional.Method(modid = ModIds.COMPUTERCRAFT)
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException{
        return luaMethods.get(method).call(computer, context, arguments);
    }

    @Override
    @Optional.Method(modid = ModIds.COMPUTERCRAFT)
    public void attach(IComputerAccess computer){}

    @Override
    @Optional.Method(modid = ModIds.COMPUTERCRAFT)
    public void detach(IComputerAccess computer){}

    @Override
    @Optional.Method(modid = ModIds.COMPUTERCRAFT)
    public boolean equals(IPeripheral other){//TODO await documention on the method, so it can be correctly implemented.
        return this.equals((Object)other);
    }

    @Override
    @Optional.Method(modid = ModIds.COMPUTERCRAFT)
    protected void addLuaMethods(){
        luaMethods.add(new LuaMethod("getPressure"){
            @Override
            public Object[] call(IComputerAccess computer, ILuaContext context, Object[] args) throws LuaException, InterruptedException{
                if(args.length == 0) {
                    return new Object[]{getPressure(ForgeDirection.UNKNOWN)};
                } else if(args.length == 1) {
                    return new Object[]{getPressure(getDirForString((String)args[0]))};
                } else {
                    throw new LuaException("getPressure method requires 0 or 1 argument (direction: up, down, east, west, north, south!");
                }
            }
        });

        luaMethods.add(new LuaConstant("getDangerPressure", DANGER_PRESSURE));
        luaMethods.add(new LuaConstant("getCriticalPressure", CRITICAL_PRESSURE));
        luaMethods.add(new LuaConstant("getDefaultVolume", DEFAULT_VOLUME));
    }

    /*
     * End ComputerCraft API 
     */

    @Override
    public void updateEntityI(){
        updateEntity();
    }

    @Override
    public void readFromNBTI(NBTTagCompound nbt){
        readFromNBT(nbt);
    }

    @Override
    public void writeToNBTI(NBTTagCompound nbt){
        writeToNBT(nbt);
    }

    @Override
    public void validateI(TileEntity parent){
        parentTile = parent;
        worldObj = parent.getWorldObj();
        xCoord = parent.xCoord;
        yCoord = parent.yCoord;
        zCoord = parent.zCoord;
    }

    @Override
    public void onNeighborChange(){
        onNeighborTileUpdate();
    }

    @Override
    public World world(){
        return worldObj;
    }

    @Override
    public int x(){
        return xCoord;
    }

    @Override
    public int y(){
        return yCoord;
    }

    @Override
    public int z(){
        return zCoord;
    }
}
