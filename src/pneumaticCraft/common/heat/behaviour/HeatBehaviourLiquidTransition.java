package pneumaticCraft.common.heat.behaviour;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fluids.Fluid;
import pneumaticCraft.common.heat.HeatExchangerManager;
import pneumaticCraft.common.network.NetworkHandler;
import pneumaticCraft.common.network.PacketPlaySound;
import pneumaticCraft.common.network.PacketSpawnParticle;
import pneumaticCraft.common.util.FluidUtils;

public abstract class HeatBehaviourLiquidTransition extends HeatBehaviourLiquid{
    private double extractedHeat;
    private double maxExchangedHeat;
    private int fluidTemp = -1;

    @Override
    public boolean isApplicable(){
        Fluid fluid = getFluid();
        return fluid != null && fluid.getTemperature() >= getMinFluidTemp() && fluid.getTemperature() <= getMaxFluidTemp();
    }

    protected abstract int getMinFluidTemp();

    protected abstract int getMaxFluidTemp();

    protected abstract int getMaxExchangedHeat();

    protected abstract Block getTransitionedSourceBlock();

    protected abstract Block getTransitionedFlowingBlock();

    protected abstract boolean transitionOnTooMuchExtraction();

    @Override
    public void update(){
        if(fluidTemp == -1) {
            fluidTemp = getFluid().getTemperature();
            maxExchangedHeat = getMaxExchangedHeat() * (HeatExchangerManager.FLUID_RESISTANCE + getHeatExchanger().getThermalResistance());
        }
        extractedHeat += fluidTemp - getHeatExchanger().getTemperature();
        if(transitionOnTooMuchExtraction() ? extractedHeat > maxExchangedHeat : extractedHeat < -maxExchangedHeat) {
            transformSourceBlock(getTransitionedSourceBlock(), getTransitionedFlowingBlock());
            extractedHeat -= maxExchangedHeat;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        tag.setDouble("extractedHeat", extractedHeat);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        extractedHeat = tag.getDouble("extractedHeat");
    }

    protected void transformSourceBlock(Block turningBlockSource, Block turningBlockFlowing){
        if(FluidUtils.isSourceBlock(getWorld(), getPos())) {
            getWorld().setBlockState(getPos(), turningBlockSource.getDefaultState());
            onLiquidTransition(getPos());
        } else {
            Set<BlockPos> traversed = new HashSet<BlockPos>();
            Stack<BlockPos> pending = new Stack<BlockPos>();
            pending.push(getPos());
            while(!pending.isEmpty()) {
                BlockPos pos = pending.pop();
                for(EnumFacing d : EnumFacing.VALUES) {
                    BlockPos newPos = pos.offset(d);
                    Block checkingBlock = getWorld().getBlockState(newPos).getBlock();
                    if((checkingBlock == getBlockState().getBlock() || getBlockState().getBlock() == Blocks.flowing_water && checkingBlock == Blocks.water || getBlockState().getBlock() == Blocks.flowing_lava && checkingBlock == Blocks.lava) && traversed.add(newPos)) {
                        if(FluidUtils.isSourceBlock(getWorld(), newPos)) {
                            getWorld().setBlockState(newPos, turningBlockSource.getDefaultState());
                            onLiquidTransition(newPos);
                            return;
                        } else {
                            getWorld().setBlockState(newPos, turningBlockFlowing.getDefaultState());
                            onLiquidTransition(newPos);
                            pending.push(newPos);
                        }
                    }
                }
            }
        }
    }

    protected void onLiquidTransition(BlockPos pos){
        NetworkHandler.sendToAllAround(new PacketPlaySound("random.fizz", pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.5F, 2.6F + (getWorld().rand.nextFloat() - getWorld().rand.nextFloat()) * 0.8F, true), getWorld());
        for(int i = 0; i < 8; i++) {
            double randX = pos.getX() + getWorld().rand.nextDouble();
            double randZ = pos.getZ() + getWorld().rand.nextDouble();
            NetworkHandler.sendToAllAround(new PacketSpawnParticle(EnumParticleTypes.SMOKE_LARGE, randX, pos.getY() + 1, randZ, 0, 0, 0), getWorld());
        }
    }
}
