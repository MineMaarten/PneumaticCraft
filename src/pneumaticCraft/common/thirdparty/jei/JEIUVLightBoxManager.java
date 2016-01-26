package pneumaticCraft.common.thirdparty.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.thirdparty.jei.JEIUVLightBoxManager.UVLightBoxRecipe;
import pneumaticCraft.common.thirdparty.jei.PneumaticCraftPlugins.MultipleInputOutputRecipe;

public class JEIUVLightBoxManager extends JEISpecialCraftingManager<UVLightBoxRecipe>{

    public JEIUVLightBoxManager(IModRegistry registry, IJeiHelpers jeiHelpers){
        super(registry, jeiHelpers);
        setText("gui.nei.recipe.uvLightBox");
    }

    @Override
    public String getTitle(){
        return StatCollector.translateToLocal(Blockss.uvLightBox.getUnlocalizedName() + ".name");
    }

    @Override
    protected List<MultipleInputOutputRecipe> getAllRecipes(){
        List<MultipleInputOutputRecipe> recipes = new ArrayList<MultipleInputOutputRecipe>();
        MultipleInputOutputRecipe recipe = new UVLightBoxRecipe();
        recipe.addIngredient(new PositionedStack(new ItemStack(Itemss.emptyPCB, 1, Itemss.emptyPCB.getMaxDamage()), 41, 80));
        recipe.addIngredient(new PositionedStack(new ItemStack(Blockss.uvLightBox), 73, 80));
        recipe.addOutput(new PositionedStack(new ItemStack(Itemss.emptyPCB), 105, 80));
        recipes.add(recipe);
        return recipes;
    }

    public static class UVLightBoxRecipe extends MultipleInputOutputRecipe{

    }

    @Override
    public Class<UVLightBoxRecipe> getRecipeClass(){
        return UVLightBoxRecipe.class;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(UVLightBoxRecipe recipe){
        return recipe;
    }

    @Override
    public boolean isRecipeValid(UVLightBoxRecipe recipe){
        return true;
    }
}
