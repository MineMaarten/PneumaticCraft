package pneumaticCraft.common.progwidgets;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import pneumaticCraft.api.tileentity.IAirHandler;
import pneumaticCraft.api.tileentity.IPneumaticMachine;
import pneumaticCraft.common.ai.DroneAIBlockCondition;
import pneumaticCraft.common.ai.IDroneBase;
import pneumaticCraft.lib.Textures;

public class ProgWidgetPressureCondition extends ProgWidgetCondition{

    @Override
    public String getWidgetString(){
        return "conditionPressure";
    }

    @Override
    public Class<? extends IProgWidget>[] getParameters(){
        return new Class[]{ProgWidgetArea.class, ProgWidgetString.class};
    }

    @Override
    protected DroneAIBlockCondition getEvaluator(IDroneBase drone, IProgWidget widget){
        return new DroneAIBlockCondition(drone, (ProgWidgetAreaItemBase)widget){

            @Override
            protected boolean evaluate(BlockPos pos){
                TileEntity te = drone.getWorld().getTileEntity(pos);
                if(te instanceof IPneumaticMachine) {
                    float pressure = Float.MIN_VALUE;
                    for(EnumFacing d : EnumFacing.VALUES) {
                        if(getSides()[d.ordinal()]) {
                            IAirHandler airHandler = ((IPneumaticMachine)te).getAirHandler(d);
                            if(airHandler != null) pressure = Math.max(airHandler.getPressure(), pressure);
                        }
                    }
                    return ((ICondition)widget).getOperator() == ICondition.Operator.EQUALS ? pressure == ((ICondition)widget).getRequiredCount() : pressure >= ((ICondition)widget).getRequiredCount();
                }
                return false;
            }

        };
    }

    @Override
    public ResourceLocation getTexture(){
        return Textures.PROG_WIDGET_CONDITION_PRESSURE;
    }

}
