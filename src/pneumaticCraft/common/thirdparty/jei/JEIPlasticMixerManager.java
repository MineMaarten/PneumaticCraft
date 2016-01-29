package pneumaticCraft.common.thirdparty.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.fluid.Fluids;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.thirdparty.jei.JEIPlasticMixerManager.PlasticMixerNEIRecipe;
import pneumaticCraft.lib.PneumaticValues;
import pneumaticCraft.lib.Textures;

public class JEIPlasticMixerManager extends PneumaticCraftPlugins<PlasticMixerNEIRecipe>{

    public JEIPlasticMixerManager(IJeiHelpers jeiHelpers){
        super(jeiHelpers);
    }

    @Override
    public String getTitle(){
        return StatCollector.translateToLocal(Blockss.plasticMixer.getUnlocalizedName() + ".name");
    }

    @Override
    public ResourceDrawable getGuiTexture(){
        return new ResourceDrawable(Textures.GUI_PLASTIC_MIXER, 0, 0, 6, 3, 166, 79);
    }

    public static class PlasticMixerNEIRecipe extends
            pneumaticCraft.common.thirdparty.jei.PneumaticCraftPlugins.MultipleInputOutputRecipe{

        private PlasticMixerNEIRecipe(ItemStack input, FluidStack output){

            addOutputLiquid(output, 146, 11);
            addIngredient(new PositionedStack(input, 92, 23));
            setUsedTemperature(76, 22, PneumaticValues.PLASTIC_MIXER_MELTING_TEMP);
        }

        private PlasticMixerNEIRecipe(FluidStack input, ItemStack output){
            addInputLiquid(input, 146, 11);
            addIngredient(new PositionedStack(new ItemStack(Items.dye, 1, 1), 121, 19));
            addIngredient(new PositionedStack(new ItemStack(Items.dye, 1, 2), 121, 37));
            addIngredient(new PositionedStack(new ItemStack(Items.dye, 1, 4), 121, 55));
            addOutput(new PositionedStack(output, 92, 55));
            setUsedTemperature(76, 22, PneumaticValues.PLASTIC_MIXER_MELTING_TEMP);
        }
    }

    public List<MultipleInputOutputRecipe> getAllRecipes(){
        List<MultipleInputOutputRecipe> recipes = new ArrayList<MultipleInputOutputRecipe>();
        for(int i = 0; i < 16; i++)
            recipes.add(new PlasticMixerNEIRecipe(new ItemStack(Itemss.plastic, 1, i), new FluidStack(Fluids.plastic, 1000)));
        for(int i = 0; i < 16; i++)
            recipes.add(new PlasticMixerNEIRecipe(new FluidStack(Fluids.plastic, 1000), new ItemStack(Itemss.plastic, 1, i)));
        return recipes;
    }

    @Override
    public Class<PlasticMixerNEIRecipe> getRecipeClass(){
        return PlasticMixerNEIRecipe.class;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(PlasticMixerNEIRecipe recipe){
        return recipe;
    }

    @Override
    public boolean isRecipeValid(PlasticMixerNEIRecipe recipe){
        return true;
    }
}
