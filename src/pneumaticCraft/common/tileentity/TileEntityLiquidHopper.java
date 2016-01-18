package pneumaticCraft.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
import pneumaticCraft.api.item.IItemRegistry.EnumUpgrade;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.network.DescSynced;
import pneumaticCraft.common.util.FluidUtils;
import pneumaticCraft.common.util.IOHelper;
import pneumaticCraft.lib.PneumaticValues;

public class TileEntityLiquidHopper extends TileEntityOmnidirectionalHopper implements IFluidHandler{

    @DescSynced
    private final FluidTank tank = new FluidTank(PneumaticValues.NORMAL_TANK_CAPACITY);

    public TileEntityLiquidHopper(){
        super(0, 1, 2, 3);
        addApplicableUpgrade(EnumUpgrade.DISPENSER);
    }

    @Override
    protected int getInvSize(){
        return 4;
    }

    @Override
    public String getName(){
        return Blockss.liquidHopper.getUnlocalizedName();
    }

    @Override
    public int[] getSlotsForFace(EnumFacing var1){
        return new int[]{0, 1, 2, 3};
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack){
        return canInsertUpgrade(slot, stack);
    }

    @Override
    protected boolean exportItem(int maxItems){
        EnumFacing dir = getRotation();
        if(tank.getFluid() != null) {
            TileEntity neighbor = IOHelper.getNeighbor(this, dir);
            if(neighbor instanceof IFluidHandler) {
                IFluidHandler fluidHandler = (IFluidHandler)neighbor;
                if(fluidHandler.canFill(dir.getOpposite(), tank.getFluid().getFluid())) {
                    FluidStack fluid = tank.getFluid().copy();
                    fluid.amount = Math.min(maxItems * 100, tank.getFluid().amount - (leaveMaterial ? 1000 : 0));
                    if(fluid.amount > 0) {
                        tank.getFluid().amount -= fluidHandler.fill(dir.getOpposite(), fluid, true);
                        if(tank.getFluidAmount() <= 0) tank.setFluid(null);
                        return true;
                    }
                }
            }
        }

        if(worldObj.isAirBlock(getPos().offset(dir))) {
            for(EntityItem entity : getNeighborItems(this, dir)) {
                if(!entity.isDead) {
                    List<ItemStack> returnedItems = new ArrayList<ItemStack>();
                    if(FluidUtils.tryExtractingLiquid(this, entity.getEntityItem(), returnedItems)) {
                        if(entity.getEntityItem().stackSize <= 0) entity.setDead();
                        for(ItemStack stack : returnedItems) {
                            EntityItem item = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, stack);
                            item.motionX = entity.motionX;
                            item.motionY = entity.motionY;
                            item.motionZ = entity.motionZ;
                            worldObj.spawnEntityInWorld(item);
                        }
                        return true;
                    }
                }
            }
        }

        if(getUpgrades(EnumUpgrade.DISPENSER) > 0) {
            if(worldObj.isAirBlock(getPos().offset(dir))) {
                FluidStack extractedFluid = drain(null, 1000, false);
                if(extractedFluid != null && extractedFluid.amount == 1000) {
                    Block fluidBlock = extractedFluid.getFluid().getBlock();
                    if(fluidBlock != null) {
                        drain(null, 1000, true);
                        worldObj.setBlockState(getPos().offset(dir), fluidBlock.getDefaultState());
                    }
                }
            }
        }

        return false;
    }

    @Override
    protected boolean suckInItem(int maxItems){
        TileEntity inputInv = IOHelper.getNeighbor(this, inputDir);
        if(inputInv instanceof IFluidHandler) {
            IFluidHandler fluidHandler = (IFluidHandler)inputInv;

            FluidStack fluid = fluidHandler.drain(inputDir.getOpposite(), maxItems * 100, false);
            if(fluid != null && fluidHandler.canDrain(inputDir.getOpposite(), fluid.getFluid())) {
                int filledFluid = fill(inputDir, fluid, true);
                if(filledFluid > 0) {
                    fluidHandler.drain(inputDir.getOpposite(), filledFluid, true);
                    return true;
                }
            }
        }

        if(worldObj.isAirBlock(getPos().offset(inputDir))) {
            for(EntityItem entity : getNeighborItems(this, inputDir)) {
                if(!entity.isDead) {
                    List<ItemStack> returnedItems = new ArrayList<ItemStack>();
                    if(FluidUtils.tryInsertingLiquid(this, entity.getEntityItem(), false, returnedItems)) {
                        if(entity.getEntityItem().stackSize <= 0) entity.setDead();
                        for(ItemStack stack : returnedItems) {
                            EntityItem item = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, stack);
                            item.motionX = entity.motionX;
                            item.motionY = entity.motionY;
                            item.motionZ = entity.motionZ;
                            worldObj.spawnEntityInWorld(item);
                        }
                        return true;
                    }
                }
            }
        }

        if(getUpgrades(EnumUpgrade.DISPENSER) > 0) {
            BlockPos neighborPos = getPos().offset(inputDir);
            Fluid fluid = FluidRegistry.lookupFluidForBlock(worldObj.getBlockState(neighborPos).getBlock());
            if(fluid != null && FluidUtils.isSourceBlock(worldObj, neighborPos)) {
                if(fill(null, new FluidStack(fluid, 1000), false) == 1000) {
                    fill(null, new FluidStack(fluid, 1000), true);
                    worldObj.setBlockToAir(neighborPos);
                    return true;
                }
            }
        }

        return false;
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
        return tank.drain(leaveMaterial ? Math.min(maxDrain, tank.getFluidAmount() - 1000) : maxDrain, doDrain);
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid){
        return true;
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

    @Override
    public void writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);

        NBTTagCompound tankTag = new NBTTagCompound();
        tank.writeToNBT(tankTag);
        tag.setTag("tank", tankTag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        tank.readFromNBT(tag.getCompoundTag("tank"));
    }
}
