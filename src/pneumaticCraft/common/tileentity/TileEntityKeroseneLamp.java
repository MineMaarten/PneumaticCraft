package pneumaticCraft.common.tileentity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
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
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.fluid.Fluids;
import pneumaticCraft.common.network.DescSynced;
import pneumaticCraft.common.network.GuiSynced;
import pneumaticCraft.common.util.PneumaticCraftUtils;

public class TileEntityKeroseneLamp extends TileEntityBase implements IFluidHandler, IRedstoneControlled,
        ISidedInventory{
    private final Set<BlockPos> managingLights = new HashSet<BlockPos>();
    @DescSynced
    private boolean isOn;
    @GuiSynced
    private int range;
    @GuiSynced
    private int targetRange = 10;
    @GuiSynced
    private int redstoneMode;
    @GuiSynced
    private int fuel;
    private static final int LIGHT_SPACING = 3;
    public static final int FUEL_PER_MB = 10000;
    private int checkingX, checkingY, checkingZ;
    @DescSynced
    private EnumFacing sideConnected = EnumFacing.DOWN;

    @DescSynced
    private final FluidTank tank = new FluidTank(1000);

    private final ItemStack[] inventory = new ItemStack[2];

    @Override
    public void update(){
        super.update();
        if(!worldObj.isRemote) {
            processFluidItem(0, 1);
            if(worldObj.getTotalWorldTime() % 5 == 0) {
                int realTargetRange = redstoneAllows() ? targetRange : 0;
                if(redstoneMode == 3) realTargetRange = (int)(poweredRedstone / 15D * targetRange);
                updateRange(Math.min(realTargetRange, tank.getFluidAmount())); //Fade out the lamp when almost empty.
                updateLights();
                useFuel();
            }
        } else {
            if(isOn && worldObj.getTotalWorldTime() % 5 == 0) {
                worldObj.spawnParticle(EnumParticleTypes.FLAME, getPos().getX() + 0.4 + 0.2 * worldObj.rand.nextDouble(), getPos().getY() + 0.2 + tank.getFluidAmount() / 1000D * 3 / 16D, getPos().getZ() + 0.4 + 0.2 * worldObj.rand.nextDouble(), 0, 0, 0);
            }
        }
    }

    private void useFuel(){
        fuel -= Math.pow(range, 3);
        if(fuel < 0 && tank.drain(1, true) != null) {
            fuel += FUEL_PER_MB;
        }
        if(fuel < 0) fuel = 0;
    }

    @Override
    public void validate(){
        super.validate();
        checkingX = getPos().getX();
        checkingY = getPos().getY();
        checkingZ = getPos().getZ();
    }

    @Override
    public void invalidate(){
        super.invalidate();
        for(BlockPos pos : managingLights) {
            if(isLampLight(pos)) {
                worldObj.setBlockToAir(pos);
            }
        }
    }

    private boolean isLampLight(BlockPos pos){
        return worldObj.getBlockState(pos).getBlock() == Blockss.keroseneLampLight;
    }

    private void updateLights(){
        int roundedRange = range / LIGHT_SPACING * LIGHT_SPACING;
        checkingX += LIGHT_SPACING;
        if(checkingX > getPos().getX() + roundedRange) {
            checkingX = getPos().getX() - roundedRange;
            checkingY += LIGHT_SPACING;
            if(checkingY > getPos().getY() + roundedRange) {
                checkingY = getPos().getY() - roundedRange;
                checkingZ += LIGHT_SPACING;
                if(checkingZ > getPos().getZ() + roundedRange) checkingZ = getPos().getZ() - roundedRange;
            }
        }
        BlockPos pos = new BlockPos(checkingX, checkingY, checkingZ);
        BlockPos lampPos = new BlockPos(getPos().getX(), getPos().getY(), getPos().getZ());
        if(managingLights.contains(pos)) {
            if(isLampLight(pos)) {
                if(!passesRaytraceTest(pos, lampPos)) {
                    worldObj.setBlockToAir(pos);
                    managingLights.remove(pos);
                }
            } else {
                managingLights.remove(pos);
            }
        } else {
            tryAddLight(pos, lampPos);
        }
    }

    private void updateRange(int targetRange){
        if(targetRange > range) {
            range++;
            BlockPos lampPos = new BlockPos(getPos().getX(), getPos().getY(), getPos().getZ());
            int roundedRange = range / LIGHT_SPACING * LIGHT_SPACING;
            for(int x = -roundedRange; x <= roundedRange; x += LIGHT_SPACING) {
                for(int y = -roundedRange; y <= roundedRange; y += LIGHT_SPACING) {
                    for(int z = -roundedRange; z <= roundedRange; z += LIGHT_SPACING) {
                        BlockPos pos = new BlockPos(x + getPos().getX(), y + getPos().getY(), z + getPos().getZ());
                        if(!managingLights.contains(pos)) {
                            tryAddLight(pos, lampPos);
                        }
                    }
                }
            }
        } else if(targetRange < range) {
            range--;
            Iterator<BlockPos> iterator = managingLights.iterator();
            BlockPos lampPos = new BlockPos(getPos().getX(), getPos().getY(), getPos().getZ());
            while(iterator.hasNext()) {
                BlockPos pos = iterator.next();
                if(!isLampLight(pos)) {
                    iterator.remove();
                } else if(PneumaticCraftUtils.distBetween(pos, lampPos) > range) {
                    worldObj.setBlockToAir(pos);
                    iterator.remove();
                }
            }
        }
        isOn = range > 0;
    }

    private boolean passesRaytraceTest(BlockPos pos, BlockPos lampPos){
        MovingObjectPosition mop = worldObj.rayTraceBlocks(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), new Vec3(lampPos.getX() + 0.5, lampPos.getY() + 0.5, lampPos.getZ() + 0.5));
        return mop != null && lampPos.equals(mop.getBlockPos());
    }

    private boolean tryAddLight(BlockPos pos, BlockPos lampPos){
        if(PneumaticCraftUtils.distBetween(pos, lampPos) <= range) {
            if(worldObj.isAirBlock(pos) && !isLampLight(pos)) {
                if(passesRaytraceTest(pos, lampPos)) {
                    worldObj.setBlockState(pos, Blockss.keroseneLampLight.getDefaultState());
                    managingLights.add(pos);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onNeighborBlockUpdate(){
        super.onNeighborBlockUpdate();
        sideConnected = EnumFacing.DOWN;
        for(EnumFacing d : EnumFacing.VALUES) {
            BlockPos neighborPos = getPos().offset(d);
            Block block = worldObj.getBlockState(neighborPos).getBlock();
            if(block.isSideSolid(worldObj, neighborPos, d.getOpposite())) {
                sideConnected = d;
                break;
            }
        }
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill){
        return canFill(from, resource.getFluid()) ? tank.fill(resource, doFill) : 0;
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain){
        return tank.getFluid() != null && tank.getFluid().isFluidEqual(resource) ? drain(null, resource.amount, doDrain) : null;
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain){
        return tank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid){
        return Fluids.areFluidsEqual(fluid, Fluids.kerosene);
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid){
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from){
        return new FluidTankInfo[]{new FluidTankInfo(tank)};
    }

    @Override
    public void writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        NBTTagList lights = new NBTTagList();
        for(BlockPos pos : managingLights) {
            NBTTagCompound t = new NBTTagCompound();
            t.setInteger("x", pos.getX());
            t.setInteger("y", pos.getY());
            t.setInteger("z", pos.getZ());
            lights.appendTag(t);
        }
        tag.setTag("lights", lights);

        NBTTagCompound tankTag = new NBTTagCompound();
        tank.writeToNBT(tankTag);
        tag.setTag("tank", tankTag);
        tag.setByte("redstoneMode", (byte)redstoneMode);
        tag.setByte("targetRange", (byte)targetRange);
        tag.setByte("range", (byte)range);
        tag.setByte("sideConnected", (byte)sideConnected.ordinal());
        writeInventoryToNBT(tag, inventory);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        managingLights.clear();
        NBTTagList lights = tag.getTagList("lights", 10);
        for(int i = 0; i < lights.tagCount(); i++) {
            NBTTagCompound t = lights.getCompoundTagAt(i);
            managingLights.add(new BlockPos(t.getInteger("x"), t.getInteger("y"), t.getInteger("z")));
        }
        tank.readFromNBT(tag.getCompoundTag("tank"));
        redstoneMode = tag.getByte("redstoneMode");
        targetRange = tag.getByte("targetRange");
        range = tag.getByte("range");
        sideConnected = EnumFacing.getFront(tag.getByte("sideConnected"));
        readInventoryFromNBT(tag, inventory);
    }

    @Override
    public boolean redstoneAllows(){
        if(redstoneMode == 3) return true;
        return super.redstoneAllows();
    }

    @Override
    public int getRedstoneMode(){
        return redstoneMode;
    }

    @Override
    public void handleGUIButtonPress(int buttonID, EntityPlayer player){
        if(buttonID == 0) {
            redstoneMode++;
            if(redstoneMode > 3) redstoneMode = 0;
        } else if(buttonID > 0 && buttonID <= 30) {
            targetRange = buttonID;
        }
    }

    @SideOnly(Side.CLIENT)
    public IFluidTank getTank(){
        return tank;
    }

    public int getRange(){
        return range;
    }

    public int getTargetRange(){
        return targetRange;
    }

    public int getFuel(){
        return fuel;
    }

    public EnumFacing getSideConnected(){
        return sideConnected;
    }

    /*
     * ---------------IInventory---------------------
     */

    /**
     * Returns the name of the inventory.
     */
    @Override
    public String getName(){
        return Blockss.keroseneLamp.getUnlocalizedName();
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
        return slot == 1 ? false : stack != null && (FluidContainerRegistry.getFluidForFilledItem(stack) != null || stack.getItem() instanceof IFluidContainerItem && ((IFluidContainerItem)stack.getItem()).getFluid(stack) != null);
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
    public int[] getSlotsForFace(EnumFacing p_94128_1_){
        return new int[]{0, 1};
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side){
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side){
        return slot == 1;
    }

    @Override
    public void clear(){
        Arrays.fill(inventory, null);
    }
}
