package pneumaticCraft.common.thirdparty.jei;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.util.StatCollector;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.recipes.BasicThermopneumaticProcessingPlantRecipe;
import pneumaticCraft.lib.Textures;

public class JEIThermopneumaticProcessingPlantManager extends
        PneumaticCraftPlugins<BasicThermopneumaticProcessingPlantRecipe>{

    public JEIThermopneumaticProcessingPlantManager(IJeiHelpers jeiHelpers){
        super(jeiHelpers);
    }

    @Override
    public String getTitle(){
        return StatCollector.translateToLocal(Blockss.thermopneumaticProcessingPlant.getUnlocalizedName() + ".name");
    }

    @Override
    public ResourceDrawable getGuiTexture(){
        return new ResourceDrawable(Textures.GUI_THERMOPNEUMATIC_PROCESSING_PLANT, 0, 0, 5, 11, 166, 70);
    }

    private class ThermoNEIRecipe extends MultipleInputOutputRecipe{
        private ThermoNEIRecipe(BasicThermopneumaticProcessingPlantRecipe recipe){
            addInputLiquid(recipe.getInputLiquid(), 8, 4);
            addOutputLiquid(recipe.getOutputLiquid(), 74, 3);
            if(recipe.getInputItem() != null) this.addIngredient(new PositionedStack(recipe.getInputItem(), 41, 3));
            setUsedPressure(136, 42, recipe.getRequiredPressure(null, null));
            setUsedTemperature(92, 12, recipe.getRequiredTemperature(null, null));
        }
    }

    /*  @Override
      public void drawExtras(int recipe){
          this.drawProgressBar(25, 20, 176, 0, 48, 22, cycleticks % 48 / 48F, 0);
          super.drawExtras(recipe);
      }*/

    @Override
    public Class<BasicThermopneumaticProcessingPlantRecipe> getRecipeClass(){
        return BasicThermopneumaticProcessingPlantRecipe.class;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(BasicThermopneumaticProcessingPlantRecipe recipe){
        return new ThermoNEIRecipe(recipe);
    }

    @Override
    public boolean isRecipeValid(BasicThermopneumaticProcessingPlantRecipe recipe){
        return true;
    }

}
