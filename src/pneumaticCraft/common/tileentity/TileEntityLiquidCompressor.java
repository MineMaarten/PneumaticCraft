package pneumaticCraft.common.tileentity;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.common.PneumaticCraftAPIHandler;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.network.GuiSynced;
import pneumaticCraft.lib.PneumaticValues;

public class TileEntityLiquidCompressor extends TileEntityPneumaticBase implements IInventory, IRedstoneControlled,
        IFluidHandler{

    @GuiSynced
    private final FluidTank tank = new FluidTank(PneumaticValues.NORMAL_TANK_CAPACITY);
    private final ItemStack[] inventory = new ItemStack[6];
    @GuiSynced
    public int redstoneMode;
    private double internalFuelBuffer;
    @GuiSynced
    public boolean isProducing;

    public TileEntityLiquidCompressor(){
        this(5, 7, 5000);
    }

    public TileEntityLiquidCompressor(float dangerPressure, float criticalPressure, int volume){
        super(dangerPressure, criticalPressure, volume);
        setUpgradeSlots(0, 1, 2, 3);
    }

    private int getFuelValue(FluidStack fluid){
        return fluid == null ? 0 : getFuelValue(fluid.getFluid());
    }

    private int getFuelValue(Fluid fluid){
        Integer value = PneumaticCraftAPIHandler.getInstance().liquidFuels.get(fluid.getName());
        return value == null ? 0 : value;
    }

    @Override
    public void update(){
        super.update();

        if(!worldObj.isRemote) {
            processFluidItem(4, 5);

            isProducing = false;
            if(redstoneAllows()) {
                int usageRate = (int)(getBaseProduction() * this.getSpeedUsageMultiplierFromUpgrades());
                if(internalFuelBuffer < usageRate) {
                    double fuelValue = getFuelValue(tank.getFluid()) / 1000D;
                    if(fuelValue > 0) {
                        int usedFuel = Math.min(tank.getFluidAmount(), (int)(usageRate / fuelValue) + 1);
                        tank.drain(usedFuel, true);
                        internalFuelBuffer += usedFuel * fuelValue;
                    }
                }
                if(internalFuelBuffer >= usageRate) {
                    isProducing = true;
                    internalFuelBuffer -= usageRate;
                    onFuelBurn(usageRate);
                    addAir((int)(getBaseProduction() * this.getSpeedMultiplierFromUpgrades() * getEfficiency() / 100), null);
                }
            }
        }
    }

    protected void onFuelBurn(int burnedFuel){}

    public int getEfficiency(){
        return 100;
    }

    public int getBaseProduction(){
        return 10;
    }

    @Override
    public boolean isConnectedTo(EnumFacing dir){
        EnumFacing orientation = getRotation();
        return orientation == dir || orientation == dir.getOpposite() || dir == EnumFacing.UP;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        writeInventoryToNBT(tag, inventory);
        tag.setByte("redstoneMode", (byte)redstoneMode);

        NBTTagCompound tankTag = new NBTTagCompound();
        tank.writeToNBT(tankTag);
        tag.setTag("tank", tankTag);

        tag.setDouble("internalFuelBuffer", internalFuelBuffer);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        readInventoryFromNBT(tag, inventory);
        redstoneMode = tag.getByte("redstoneMode");
        tank.readFromNBT(tag.getCompoundTag("tank"));

        internalFuelBuffer = tag.getDouble("internalFuelBuffer");
    }

    /*
     * ---------------IInventory---------------------
     */

    /**
     * Returns the name of the inventory.
     */
    @Override
    public String getName(){
        return Blockss.liquidCompressor.getUnlocalizedName();
    }

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
    public ItemStack getStackInSlot(int par1){
        return inventory[par1];
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
        inventory[slot] = itemStack;
        if(itemStack != null && itemStack.stackSize > getInventoryStackLimit()) {
            itemStack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack){
        return slot == 5 ? false : stack != null && (FluidContainerRegistry.getFluidForFilledItem(stack) != null || stack.getItem() instanceof IFluidContainerItem && ((IFluidContainerItem)stack.getItem()).getFluid(stack) != null);
    }

    @Override
    public boolean hasCustomName(){
        return false;
    }

    @Override
    public int getInventoryStackLimit(){
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_){
        return isGuiUseableByPlayer(p_70300_1_);
    }

    @Override
    public void openInventory(EntityPlayer player){}

    @Override
    public void closeInventory(EntityPlayer player){}

    @Override
    public void clear(){
        Arrays.fill(inventory, null);
    }

    /*
     * --------------Redstone modes-------------------
     */

    @Override
    public void handleGUIButtonPress(int buttonID, EntityPlayer player){
        if(buttonID == 0) {
            redstoneMode++;
            if(redstoneMode > 2) redstoneMode = 0;
        }
    }

    @Override
    public int getRedstoneMode(){
        return redstoneMode;
    }

    /*
     * --------------- IFluidHandler
     */

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill){
        if(getFuelValue(resource) == 0) return 0;
        return tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain){
        return drain(from, PneumaticValues.MAX_DRAIN, doDrain);
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain){
        return tank.drain(Math.min(PneumaticValues.MAX_DRAIN, maxDrain), doDrain);
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid){
        return getFuelValue(fluid) > 0;
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid){
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from){
        return new FluidTankInfo[]{tank.getInfo()};
    }

    @SideOnly(Side.CLIENT)
    public IFluidTank getFluidTank(){
        return tank;
    }
}
