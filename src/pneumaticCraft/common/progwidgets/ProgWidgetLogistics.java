package pneumaticCraft.common.progwidgets;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.ResourceLocation;
import pneumaticCraft.common.ai.DroneAILogistics;
import pneumaticCraft.common.ai.IDroneBase;
import pneumaticCraft.common.item.ItemPlastic;
import pneumaticCraft.lib.Textures;

public class ProgWidgetLogistics extends ProgWidgetAreaItemBase{

    @Override
    public String getWidgetString(){
        return "logistics";
    }

    @Override
    public int getCraftingColorIndex(){
        return ItemPlastic.ENDER_PLANT_DAMAGE;
    }

    @Override
    public ResourceLocation getTexture(){
        return Textures.PROG_WIDGET_LOGISTICS;
    }

    @Override
    public Class<? extends IProgWidget>[] getParameters(){
        return new Class[]{ProgWidgetArea.class};
    }

    @Override
    public EntityAIBase getWidgetAI(IDroneBase drone, IProgWidget widget){
        return new DroneAILogistics(drone, (ProgWidgetAreaItemBase)widget);
    }

}
