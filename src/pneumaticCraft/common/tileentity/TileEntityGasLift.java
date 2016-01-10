package pneumaticCraft.common.tileentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

import pneumaticCraft.api.tileentity.IAirHandler;
import pneumaticCraft.common.ai.ChunkPositionSorter;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.item.ItemMachineUpgrade;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.network.DescSynced;
import pneumaticCraft.common.network.GuiSynced;
import pneumaticCraft.common.util.FluidUtils;
import pneumaticCraft.common.util.IOHelper;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.lib.PneumaticValues;

public class TileEntityGasLift extends TileEntityPneumaticBase implements IMinWorkingPressure, IRedstoneControlled,
        IFluidHandler, IInventory{
    @GuiSynced
    private final FluidTank tank = new FluidTank(PneumaticValues.NORMAL_TANK_CAPACITY);
    private final ItemStack[] inventory = new ItemStack[5];
    @GuiSynced
    public int currentDepth;
    @GuiSynced
    public int redstoneMode, status, mode;
    @DescSynced
    public boolean[] sidesConnected = new boolean[6];
    private int workTimer;
    private int ticker;
    private List<BlockPos> pumpingLake;
    private static final int MAX_PUMP_RANGE = 15;

    public TileEntityGasLift(){
        super(5, 7, 3000);
        setUpgradeSlots(0, 1, 2, 3);
    }

    @Override
    public boolean isConnectedTo(EnumFacing d){
        return d != EnumFacing.DOWN;
    }

    @Override
    public void onNeighborTileUpdate(){
        super.onNeighborTileUpdate();
        List<Pair<EnumFacing, IAirHandler>> connections = getAirHandler(null).getConnectedPneumatics();
        Arrays.fill(sidesConnected, false);
        for(Pair<EnumFacing, IAirHandler> entry : connections) {
            sidesConnected[entry.getKey().ordinal()] = true;
        }
    }

    @Override
    public void update(){
        super.update();
        if(!worldObj.isRemote) {
            ticker++;
            if(currentDepth > 0) {
                int curCheckingPipe = ticker % currentDepth;
                if(curCheckingPipe > 0 && !isPipe(getPos().add(0, -curCheckingPipe, 0))) currentDepth = curCheckingPipe - 1;
            }
            if(ticker % 400 == 0) pumpingLake = null;

            if(redstoneAllows() && getPressure() >= getMinWorkingPressure()) {
                workTimer += this.getSpeedMultiplierFromUpgrades();
                while(workTimer > 20) {
                    workTimer -= 20;
                    status = 0;
                    if(mode == 2) {
                        if(currentDepth > 0) {
                            status = 3;
                            if(isPipe(getPos().add(0, -currentDepth, 0))) {
                                if(IOHelper.insert((IInventory)this, new ItemStack(Blockss.pressureTube), null, false) == null) {
                                    worldObj.destroyBlock(getPos().add(0, -currentDepth, 0), false);
                                    addAir(-100);
                                    currentDepth--;
                                } else {
                                    status = 0;
                                }
                            } else {
                                currentDepth--;
                            }
                        }
                    } else {
                        if(!suckLiquid()) {
                            if(getPos().getY() - currentDepth >= 0 && !isUnbreakable(getPos().add(0, -currentDepth - 1, 0))) {
                                status = 2;
                                currentDepth++;
                                if(!isPipe(getPos().add(0, -currentDepth, 0))) {
                                    if(inventory[4] != null) {
                                        decrStackSize(4, 1);
                                        worldObj.destroyBlock(getPos().add(0, -currentDepth, 0), false);
                                        worldObj.setBlockState(getPos().add(0, -currentDepth, 0), Blockss.pressureTube.getDefaultState());
                                        addAir(-100);
                                    } else {
                                        status = 0;
                                        currentDepth--;
                                        break;
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            } else {
                status = 0;
            }
            if(getUpgrades(ItemMachineUpgrade.UPGRADE_DISPENSER_DAMAGE) > 0) {
                autoExportLiquid();
            }
        }
    }

    private boolean isPipe(BlockPos pos){
        return worldObj.getBlockState(pos).getBlock() == Blockss.pressureTube;
    }

    private boolean isUnbreakable(BlockPos pos){
        return worldObj.getBlockState(pos).getBlock().getBlockHardness(worldObj, pos) < 0;
    }

    public boolean suckLiquid(){
        Block block = worldObj.getBlockState(getPos().add(0, -currentDepth - 1, 0)).getBlock();
        Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
        if(fluid != null) {
            if(fill(null, new FluidStack(fluid, 1000), false) == 1000) {
                if(pumpingLake == null) {
                    pumpingLake = new ArrayList<BlockPos>();
                    Stack<BlockPos> pendingPositions = new Stack<BlockPos>();
                    BlockPos thisPos = new BlockPos(getPos().getX(), getPos().getY() - currentDepth - 1, getPos().getZ());
                    pendingPositions.add(thisPos);
                    pumpingLake.add(thisPos);
                    while(!pendingPositions.empty()) {
                        BlockPos checkingPos = pendingPositions.pop();
                        for(EnumFacing d : EnumFacing.VALUES) {
                            if(d == EnumFacing.DOWN) continue;
                            BlockPos newPos = new BlockPos(checkingPos.getX() + d.getFrontOffsetX(), checkingPos.getY() + d.getFrontOffsetY(), checkingPos.getZ() + d.getFrontOffsetZ());
                            if(PneumaticCraftUtils.distBetween(newPos, getPos().getX() + 0.5, getPos().getY() - currentDepth - 1, getPos().getZ() + 0.5) <= MAX_PUMP_RANGE && worldObj.getBlockState(newPos).getBlock() == block && !pumpingLake.contains(newPos)) {
                                pendingPositions.add(newPos);
                                pumpingLake.add(newPos);
                            }
                        }
                    }
                    Collections.sort(pumpingLake, new ChunkPositionSorter(getPos().getX() + 0.5, getPos().getY() - currentDepth - 1, getPos().getZ() + 0.5));
                    Collections.reverse(pumpingLake);
                }
                BlockPos curPos = null;
                boolean foundSource = false;
                while(pumpingLake.size() > 0) {
                    curPos = pumpingLake.get(0);
                    if(worldObj.getBlockState(curPos).getBlock() == block && FluidUtils.isSourceBlock(worldObj, curPos)) {
                        foundSource = true;
                        break;
                    }
                    pumpingLake.remove(0);
                }
                if(pumpingLake.size() == 0) {
                    pumpingLake = null;
                } else if(foundSource) {
                    worldObj.setBlockToAir(curPos);
                    fill(null, new FluidStack(fluid, 1000), true);
                    addAir(-100);
                    status = 1;
                }
            }
            return true;
        } else {
            pumpingLake = null;
            return false;
        }
    }

    @Override
    public void handleGUIButtonPress(int buttonID, EntityPlayer player){
        if(buttonID == 0) {
            redstoneMode++;
            if(redstoneMode > 2) redstoneMode = 0;
        } else if(buttonID > 0 && buttonID < 4) {
            mode = buttonID - 1;
        }
    }

    @Override
    public int getRedstoneMode(){
        return redstoneMode;
    }

    @Override
    public float getMinWorkingPressure(){
        return 0.5F + currentDepth * 0.05F;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        writeInventoryToNBT(tag, inventory);
        tag.setByte("redstoneMode", (byte)redstoneMode);
        tag.setByte("mode", (byte)mode);

        NBTTagCompound tankTag = new NBTTagCompound();
        tank.writeToNBT(tankTag);
        tag.setTag("tank", tankTag);
        tag.setInteger("currentDepth", currentDepth);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        readInventoryFromNBT(tag, inventory);
        redstoneMode = tag.getByte("redstoneMode");
        mode = tag.getByte("mode");
        tank.readFromNBT(tag.getCompoundTag("tank"));
        currentDepth = tag.getInteger("currentDepth");
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill){
        return tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain){
        return tank.getFluid() != null && tank.getFluid().isFluidEqual(resource) ? drain(null, resource.amount, doDrain) : null;
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain){
        return mode == 0 ? tank.drain(maxDrain, doDrain) : tank.drain(Math.min(tank.getFluidAmount() - 1000, maxDrain), doDrain);
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid){
        return false;
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid){
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from){
        return new FluidTankInfo[]{new FluidTankInfo(tank)};
    }

    @SideOnly(Side.CLIENT)
    public FluidTank getTank(){
        return tank;
    }

    /**
     * Returns the name of the inventory.
     */
    @Override
    public String getName(){
        return Blockss.gasLift.getUnlocalizedName();
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
    public boolean isItemValidForSlot(int par1, ItemStack stack){
        return stack != null && (par1 < 4 && stack.getItem() == Itemss.machineUpgrade || par1 == 4 && stack.isItemEqual(new ItemStack(Blockss.pressureTube)));
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

}
