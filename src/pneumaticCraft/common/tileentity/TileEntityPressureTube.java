package pneumaticCraft.common.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

import pneumaticCraft.api.tileentity.IAirHandler;
import pneumaticCraft.api.tileentity.IPneumaticMachine;
import pneumaticCraft.api.tileentity.ISidedPneumaticMachine;
import pneumaticCraft.common.block.tubes.IInfluenceDispersing;
import pneumaticCraft.common.block.tubes.ModuleRegistrator;
import pneumaticCraft.common.block.tubes.TubeModule;
import pneumaticCraft.common.network.DescSynced;
import pneumaticCraft.common.thirdparty.ModInteractionUtils;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.lib.PneumaticValues;

public class TileEntityPressureTube extends TileEntityPneumaticBase{
    @DescSynced
    public boolean[] sidesConnected = new boolean[6];
    public TubeModule[] modules = new TubeModule[6];

    private Object part;

    public TileEntityPressureTube(){
        super(PneumaticValues.DANGER_PRESSURE_PRESSURE_TUBE, PneumaticValues.MAX_PRESSURE_PRESSURE_TUBE, PneumaticValues.VOLUME_PRESSURE_TUBE);
    }

    public TileEntityPressureTube(float dangerPressurePressureTube, float maxPressurePressureTube,
            int volumePressureTube){
        super(dangerPressurePressureTube, maxPressurePressureTube, volumePressureTube);
    }

    public TileEntityPressureTube setPart(Object part){
        this.part = part;
        for(TubeModule module : modules) {
            if(module != null) module.shouldDrop = false;
        }
        return this;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);
        for(int i = 0; i < 6; i++) {
            sidesConnected[i] = nbt.getBoolean("sideConnected" + i);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt){
        super.writeToNBT(nbt);
        for(int i = 0; i < 6; i++) {
            nbt.setBoolean("sideConnected" + i, sidesConnected[i]);
        }
    }

    @Override
    public void writeToPacket(NBTTagCompound tag){
        super.writeToPacket(tag);
        writeModulesToNBT(tag);
    }

    public void writeModulesToNBT(NBTTagCompound tag){
        NBTTagList moduleList = new NBTTagList();
        for(int i = 0; i < modules.length; i++) {
            if(modules[i] != null) {
                NBTTagCompound moduleTag = new NBTTagCompound();
                moduleTag.setString("type", modules[i].getType());
                modules[i].writeToNBT(moduleTag);
                moduleTag.setInteger("side", i);
                moduleList.appendTag(moduleTag);
            }
        }
        tag.setTag("modules", moduleList);
    }

    @Override
    public void readFromPacket(NBTTagCompound tag){
        super.readFromPacket(tag);
        modules = new TubeModule[6];
        NBTTagList moduleList = tag.getTagList("modules", 10);
        for(int i = 0; i < moduleList.tagCount(); i++) {
            NBTTagCompound moduleTag = moduleList.getCompoundTagAt(i);
            TubeModule module = ModuleRegistrator.getModule(moduleTag.getString("type"));
            module.readFromNBT(moduleTag);
            setModule(module, EnumFacing.getFront(moduleTag.getInteger("side")));
        }
        if(worldObj != null && worldObj.isRemote) {
            rerenderChunk();
        }
    }

    @Override
    public void update(){
        super.update();

        for(TubeModule module : modules) {
            if(module != null) {
                module.shouldDrop = true;
                module.update();
            }
        }

        List<Pair<EnumFacing, IAirHandler>> teList = getConnectedPneumatics();

        boolean hasModules = false;
        for(TubeModule module : modules) {
            if(module != null) {
                hasModules = true;
                break;
            }
        }
        if(!hasModules && teList.size() - specialConnectedHandlers.size() == 1 && !worldObj.isRemote) {
            for(Pair<EnumFacing, IAirHandler> entry : teList) {
                if(entry.getKey() != null && modules[entry.getKey().getOpposite().ordinal()] == null && isConnectedTo(entry.getKey().getOpposite())) airLeak(entry.getKey().getOpposite());
            }
        }
    }

    @Override
    protected void onAirDispersion(int amount, EnumFacing side){
        if(side != null) {
            int intSide = side/*.getOpposite()*/.ordinal();
            if(modules[intSide] instanceof IInfluenceDispersing) {
                ((IInfluenceDispersing)modules[intSide]).onAirDispersion(amount);
            }
        }
    }

    @Override
    protected int getMaxDispersion(EnumFacing side){
        if(side != null) {
            int intSide = side/*.getOpposite()*/.ordinal();
            if(modules[intSide] instanceof IInfluenceDispersing) {
                return ((IInfluenceDispersing)modules[intSide]).getMaxDispersion();
            }
        }
        return Integer.MAX_VALUE;
    }

    public void setModule(TubeModule module, EnumFacing side){
        if(module != null) {
            module.setDirection(side);
            module.setTube(this);
        }
        modules[side.ordinal()] = module;
        if(worldObj != null && !worldObj.isRemote) {
            //TODO FMP depif(part != null) updatePart();
            sendDescriptionPacket();
        }
    }

    @Override
    public boolean isConnectedTo(EnumFacing side){
        return (modules[side.ordinal()] == null || modules[side.ordinal()].isInline()) && (part == null || ModInteractionUtils.getInstance().isMultipartWiseConnected(part, side));
    }

    @Override
    public void onNeighborTileUpdate(){
        super.onNeighborTileUpdate();
        updateConnections(worldObj, getPos().getX(), getPos().getY(), getPos().getZ());
        for(TubeModule module : modules) {
            if(module != null) module.onNeighborTileUpdate();
        }
    }

    @Override
    public void onNeighborBlockUpdate(){
        super.onNeighborBlockUpdate();
        updateConnections(worldObj, getPos().getX(), getPos().getY(), getPos().getZ());
        for(TubeModule module : modules) {
            if(module != null) module.onNeighborBlockUpdate();
        }
    }

    public void updateConnections(World world, int x, int y, int z){
        sidesConnected = new boolean[6];
        boolean hasModule = false;
        for(EnumFacing direction : EnumFacing.VALUES) {
            TileEntity te = getTileCache()[direction.ordinal()].getTileEntity();
            IPneumaticMachine machine = ModInteractionUtils.getInstance().getMachine(te);
            if(machine != null) {
                sidesConnected[direction.ordinal()] = isConnectedTo(direction) && machine.isConnectedTo(direction.getOpposite());
            } else if(te instanceof ISidedPneumaticMachine) {
                sidesConnected[direction.ordinal()] = ((ISidedPneumaticMachine)te).getAirHandler(direction.getOpposite()) != null;
            }
            if(modules[direction.ordinal()] != null) {
                hasModule = true;
            }
        }
        int sidesCount = 0;
        for(boolean bool : sidesConnected) {
            if(bool) sidesCount++;
        }
        if(sidesCount == 1 && !hasModule) {
            for(int i = 0; i < 6; i++) {
                if(sidesConnected[i]) {
                    if(isConnectedTo(EnumFacing.getFront(i).getOpposite())) sidesConnected[i ^ 1] = true;
                    break;
                }
            }
        }
        for(int i = 0; i < 6; i++) {
            if(modules[i] != null && modules[i].isInline()) sidesConnected[i] = false;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox(){
        return new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1);
    }

    @Override
    public void printManometerMessage(EntityPlayer player, List<String> text){
        super.printManometerMessage(player, text);
        MovingObjectPosition mop = PneumaticCraftUtils.getEntityLookedObject(player);
        if(mop != null && mop.hitInfo instanceof EnumFacing) {
            EnumFacing dir = (EnumFacing)mop.hitInfo;
            if(dir != null && modules[dir.ordinal()] != null) {
                modules[dir.ordinal()].addInfo(text);
            }
        }
    }

    /*TODO FMP dep  @Override
      @Optional.Method(modid = ModIds.FMP)
      public void sendDescriptionPacket(){
          if(part != null && !worldObj.isRemote) {
              ((PartPressureTube)part).sendDescUpdate();
          }
          super.sendDescriptionPacket();
      }

      @Optional.Method(modid = ModIds.FMP)
      public void updatePart(){
          ((PartPressureTube)part).onNeighborChanged();
      }*/
}
