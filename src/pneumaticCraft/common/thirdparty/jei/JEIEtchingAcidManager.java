package pneumaticCraft.common.thirdparty.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import pneumaticCraft.common.fluid.Fluids;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.thirdparty.jei.JEIEtchingAcidManager.EtchingAcidRecipe;
import pneumaticCraft.common.thirdparty.jei.PneumaticCraftPlugins.MultipleInputOutputRecipe;

public class JEIEtchingAcidManager extends JEISpecialCraftingManager<EtchingAcidRecipe>{

    public JEIEtchingAcidManager(IModRegistry registry, IJeiHelpers jeiHelpers){
        super(registry, jeiHelpers);
        setText("gui.nei.recipe.etchingAcid");
    }

    @Override
    public String getTitle(){
        return StatCollector.translateToLocal(Fluids.getBlock(Fluids.etchingAcid).getUnlocalizedName() + ".name");
    }

    @Override
    protected List<MultipleInputOutputRecipe> getAllRecipes(){
        List<MultipleInputOutputRecipe> recipes = new ArrayList<MultipleInputOutputRecipe>();
        MultipleInputOutputRecipe recipe = new EtchingAcidRecipe();
        recipe.addIngredient(new PositionedStack(new ItemStack(Itemss.emptyPCB), 41, 80));
        recipe.addIngredient(new PositionedStack(new ItemStack(Fluids.getBucket(Fluids.etchingAcid)), 73, 80));
        recipe.addOutput(new PositionedStack(new ItemStack(Itemss.unassembledPCB), 105, 80));
        recipes.add(recipe);
        return recipes;
    }

    public static class EtchingAcidRecipe extends MultipleInputOutputRecipe{

    }

    @Override
    public Class<EtchingAcidRecipe> getRecipeClass(){
        return EtchingAcidRecipe.class;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(EtchingAcidRecipe recipe){
        return recipe;
    }

    @Override
    public boolean isRecipeValid(EtchingAcidRecipe recipe){
        return true;
    }
}
