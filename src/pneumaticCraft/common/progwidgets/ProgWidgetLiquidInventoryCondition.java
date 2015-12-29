package pneumaticCraft.common.progwidgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import pneumaticCraft.common.ai.DroneAIBlockCondition;
import pneumaticCraft.common.ai.IDroneBase;
import pneumaticCraft.common.util.FluidUtils;
import pneumaticCraft.lib.Textures;

public class ProgWidgetLiquidInventoryCondition extends ProgWidgetCondition{

    @Override
    public String getWidgetString(){
        return "conditionLiquidInventory";
    }

    @Override
    public Class<? extends IProgWidget>[] getParameters(){
        return new Class[]{ProgWidgetArea.class, ProgWidgetLiquidFilter.class, ProgWidgetString.class};
    }

    @Override
    protected DroneAIBlockCondition getEvaluator(IDroneBase drone, IProgWidget widget){
        return new DroneAIBlockCondition(drone, (ProgWidgetAreaItemBase)widget){

            @Override
            protected boolean evaluate(BlockPos pos){
                TileEntity te = drone.getWorld().getTileEntity(pos);
                int count = 0;
                if(te instanceof IFluidHandler) {
                    List<FluidStack> visitedStacks = new ArrayList<FluidStack>();
                    IFluidHandler inv = (IFluidHandler)te;
                    for(int i = 0; i < 6; i++) {
                        if(((ISidedWidget)widget).getSides()[i]) {
                            FluidTankInfo[] info = inv.getTankInfo(EnumFacing.getFront(i));
                            if(info != null) {
                                for(FluidTankInfo inf : info) {
                                    if(inf.fluid != null && !visitedStacks.contains(visitedStacks) && ProgWidgetLiquidFilter.isLiquidValid(inf.fluid.getFluid(), widget, 1)) {
                                        visitedStacks.add(inf.fluid);
                                        count += inf.fluid.amount;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Fluid fluid = FluidRegistry.lookupFluidForBlock(drone.getWorld().getBlockState(pos).getBlock());
                    if(fluid != null && ProgWidgetLiquidFilter.isLiquidValid(fluid, widget, 1) && FluidUtils.isSourceBlock(drone.getWorld(), pos)) {
                        count += 1000;
                    }
                }
                return ((ICondition)widget).getOperator() == ICondition.Operator.EQUALS ? count == ((ICondition)widget).getRequiredCount() : count >= ((ICondition)widget).getRequiredCount();
            }

        };
    }

    @Override
    protected ResourceLocation getTexture(){
        return Textures.PROG_WIDGET_CONDITION_LIQUID_INVENTORY;
    }

}
