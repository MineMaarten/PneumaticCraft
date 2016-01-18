package pneumaticCraft.common.tileentity;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

import pneumaticCraft.api.PneumaticRegistry;
import pneumaticCraft.api.item.IItemRegistry.EnumUpgrade;
import pneumaticCraft.api.tileentity.IAirHandler;
import pneumaticCraft.api.tileentity.IManoMeasurable;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.network.DescSynced;
import pneumaticCraft.common.network.GuiSynced;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.lib.PneumaticValues;

public class TileEntityVacuumPump extends TileEntityPneumaticBase implements IInventory, IRedstoneControlled,
        IManoMeasurable{
    @GuiSynced
    private final IAirHandler vacuumHandler = PneumaticRegistry.getInstance().getAirHandlerSupplier().createTierOneAirHandler(PneumaticValues.VOLUME_VACUUM_PUMP);
    public int rotation;
    public int oldRotation;
    public int turnTimer = -1;
    @DescSynced
    public boolean turning = false;
    public int rotationSpeed;
    @GuiSynced
    public int redstoneMode;

    private ItemStack[] inventory = new ItemStack[4];
    public static final int UPGRADE_SLOT_1 = 0;
    public static final int UPGRADE_SLOT_4 = 3;

    public static final int INVENTORY_SIZE = 4;

    public TileEntityVacuumPump(){
        super(PneumaticValues.DANGER_PRESSURE_VACUUM_PUMP, PneumaticValues.MAX_PRESSURE_VACUUM_PUMP, PneumaticValues.VOLUME_VACUUM_PUMP, UPGRADE_SLOT_1, 1, 2, UPGRADE_SLOT_4);
        vacuumHandler.setUpgradeSlots(UPGRADE_SLOT_1, 1, 2, UPGRADE_SLOT_4);
        addApplicableUpgrade(EnumUpgrade.SPEED);
    }

    @Override
    public IAirHandler getAirHandler(EnumFacing side){
        if(side == null || side == getInputSide()) { //TODO 1.8 test
            return super.getAirHandler(side);
        } else if(side == getVacuumSide()) {
            return vacuumHandler;
        } else {
            return null;
        }
    }

    @Override
    public void validate(){
        super.validate();
        vacuumHandler.validate(this);
    }

    @Override
    public void onNeighborTileUpdate(){
        super.onNeighborTileUpdate();
        vacuumHandler.onNeighborChange();
    }

    public EnumFacing getInputSide(){
        return getVacuumSide().getOpposite();
    }

    public EnumFacing getVacuumSide(){
        return getRotation();
    }

    @Override
    public void update(){
        if(!worldObj.isRemote && turnTimer >= 0) turnTimer--;
        if(!worldObj.isRemote && getAirHandler(getInputSide()).getPressure() > PneumaticValues.MIN_PRESSURE_VACUUM_PUMP && getAirHandler(getVacuumSide()).getPressure() > -1F && redstoneAllows()) {
            if(!worldObj.isRemote && turnTimer == -1) {
                turning = true;
            }
            getAirHandler(getVacuumSide()).addAir((int)(-PneumaticValues.PRODUCTION_VACUUM_PUMP * getSpeedMultiplierFromUpgrades())); // negative because it's pulling a vacuum.
            getAirHandler(getInputSide()).addAir((int)(-PneumaticValues.USAGE_VACUUM_PUMP * getSpeedUsageMultiplierFromUpgrades()));
            turnTimer = 40;
        }
        if(turnTimer == 0) {
            turning = false;
        }
        oldRotation = rotation;
        if(worldObj.isRemote) {
            if(turning) {
                rotationSpeed = Math.min(rotationSpeed + 1, 20);
            } else {
                rotationSpeed = Math.max(rotationSpeed - 1, 0);
            }
            rotation += rotationSpeed;
        }

        super.update();
        vacuumHandler.update();

        IAirHandler inputHandler = getAirHandler(getInputSide());
        List<Pair<EnumFacing, IAirHandler>> teList = inputHandler.getConnectedPneumatics();
        if(teList.size() == 0) inputHandler.airLeak(getInputSide());
        teList = vacuumHandler.getConnectedPneumatics();
        if(teList.size() == 0) vacuumHandler.airLeak(getVacuumSide());

    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox(){
        return new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        NBTTagCompound vacuum = new NBTTagCompound();
        vacuumHandler.writeToNBT(vacuum);
        tag.setTag("vacuum", vacuum);
        tag.setBoolean("turning", turning);
        tag.setInteger("redstoneMode", redstoneMode);
        // Write the ItemStacks in the inventory to NBT
        NBTTagList tagList = new NBTTagList();
        for(int currentIndex = 0; currentIndex < inventory.length; ++currentIndex) {
            if(inventory[currentIndex] != null) {
                NBTTagCompound tagCompound = new NBTTagCompound();
                tagCompound.setByte("Slot", (byte)currentIndex);
                inventory[currentIndex].writeToNBT(tagCompound);
                tagList.appendTag(tagCompound);
            }
        }
        tag.setTag("Items", tagList);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        vacuumHandler.readFromNBT(tag.getCompoundTag("vacuum"));
        turning = tag.getBoolean("turning");
        redstoneMode = tag.getInteger("redstoneMode");
        // Read in the ItemStacks in the inventory from NBT
        NBTTagList tagList = tag.getTagList("Items", 10);
        inventory = new ItemStack[getSizeInventory()];
        for(int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
            byte slot = tagCompound.getByte("Slot");
            if(slot >= 0 && slot < inventory.length) {
                inventory[slot] = ItemStack.loadItemStackFromNBT(tagCompound);
            }
        }
    }

    @Override
    public void handleGUIButtonPress(int buttonID, EntityPlayer player){
        if(buttonID == 0) {
            redstoneMode++;
            if(redstoneMode > 2) redstoneMode = 0;
        }
    }

    @Override
    public void printManometerMessage(EntityPlayer player, List<String> curInfo){
        curInfo.add(EnumChatFormatting.GREEN + "Input pressure: " + PneumaticCraftUtils.roundNumberTo(getAirHandler(getInputSide()).getPressure(), 1) + " bar. Vacuum pressure: " + PneumaticCraftUtils.roundNumberTo(getAirHandler(getVacuumSide()).getPressure(), 1) + " bar.");
    }

    // INVENTORY METHODS- && NBT
    // ------------------------------------------------------------

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory(){

        return inventory.length;
    }

    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int slot){

        return inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount){

        ItemStack itemStack = getStackInSlot(slot);
        if(itemStack != null) {
            if(itemStack.stackSize <= amount) {
                setInventorySlotContents(slot, null);
            } else {
                itemStack = itemStack.splitStack(amount);
                if(itemStack.stackSize == 0) {
                    setInventorySlotContents(slot, null);
                }
            }
        }

        return itemStack;
    }

    @Override
    public ItemStack removeStackFromSlot(int slot){

        ItemStack itemStack = getStackInSlot(slot);
        if(itemStack != null) {
            setInventorySlotContents(slot, null);
        }
        return itemStack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack){
        // super.setInventorySlotContents(slot, itemStack);
        inventory[slot] = itemStack;
        if(itemStack != null && itemStack.stackSize > getInventoryStackLimit()) {
            itemStack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public int getInventoryStackLimit(){

        return 64;
    }

    @Override
    public String getName(){

        return Blockss.vacuumPump.getUnlocalizedName();
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack){
        return canInsertUpgrade(i, itemstack);
    }

    @Override
    public boolean hasCustomName(){
        return false;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer var1){
        return isGuiUseableByPlayer(var1);
    }

    @Override
    public void openInventory(EntityPlayer player){}

    @Override
    public void closeInventory(EntityPlayer player){}

    @Override
    public void clear(){
        Arrays.fill(inventory, null);
    }

    @Override
    public int getRedstoneMode(){
        return redstoneMode;
    }

}
