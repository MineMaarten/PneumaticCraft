package pneumaticCraft.common.progwidgets;

import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import pneumaticCraft.common.ai.DroneAIBlockCondition;
import pneumaticCraft.common.ai.IDroneBase;
import pneumaticCraft.lib.Textures;

public class ProgWidgetLightCondition extends ProgWidgetCondition{

    @Override
    public String getWidgetString(){
        return "conditionLight";
    }

    @Override
    public Class<? extends IProgWidget>[] getParameters(){
        return new Class[]{ProgWidgetArea.class, ProgWidgetString.class};
    }

    @Override
    public ResourceLocation getTexture(){
        return Textures.PROG_WIDGET_CONDITION_LIGHT;
    }

    @Override
    protected DroneAIBlockCondition getEvaluator(IDroneBase drone, IProgWidget widget){
        return new DroneAIBlockCondition(drone, (ProgWidgetAreaItemBase)widget){

            @Override
            protected boolean evaluate(BlockPos pos){
                int lightLevel = drone.world().getLight(pos);
                int requiredLight = ((ICondition)widget).getRequiredCount();
                return ((ICondition)widget).getOperator() == ICondition.Operator.EQUALS ? requiredLight == lightLevel : lightLevel >= requiredLight;
            }

        };
    }
}
