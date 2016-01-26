package pneumaticCraft.common.thirdparty.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.fluid.Fluids;
import pneumaticCraft.common.thirdparty.jei.JEIRefineryManager.RefineryNEIRecipe;
import pneumaticCraft.common.tileentity.TileEntityRefinery;
import pneumaticCraft.lib.Textures;

public class JEIRefineryManager extends PneumaticCraftPlugins<RefineryNEIRecipe>{

    public JEIRefineryManager(IJeiHelpers jeiHelpers){
        super(jeiHelpers);
    }

    @Override
    public String getTitle(){
        return StatCollector.translateToLocal(Blockss.refinery.getUnlocalizedName() + ".name");
    }

    @Override
    public ResourceDrawable getGuiTexture(){
        return new ResourceDrawable(Textures.GUI_REFINERY, 0, 0, 6, 3, 166, 79);
    }

    /*   
       private boolean tankClick(GuiRecipe gui, int recipe, boolean usage){
           Point pos = getMousePosition();
           Point offset = gui.getRecipePosition(recipe);
           Point relMouse = new Point(pos.x - gui.guiLeft - offsetx, pos.y - gui.guiTop - offsety);
       }*/

    public static class RefineryNEIRecipe extends
            pneumaticCraft.common.thirdparty.jei.PneumaticCraftPlugins.MultipleInputOutputRecipe{
        public final int refineries;

        private RefineryNEIRecipe(int refineries, int[] outputs){
            this.refineries = refineries;
            addInputLiquid(new FluidStack(Fluids.oil, 10), 2, 10);
            int x = 69;
            int y = 18;
            for(int i = 0; i < outputs.length; i++) {
                if(outputs[i] == 0) continue;
                x += 20;
                y -= 4;
                addOutputLiquid(new FluidStack(TileEntityRefinery.getRefiningFluids()[i], outputs[i]), x, y);
            }
            setUsedTemperature(26, 18, 373);
        }

    }

    public List<MultipleInputOutputRecipe> getAllRecipes(){
        List<MultipleInputOutputRecipe> recipes = new ArrayList<MultipleInputOutputRecipe>();
        for(int i = 0; i < TileEntityRefinery.REFINING_TABLE.length; i++) {
            recipes.add(new RefineryNEIRecipe(2 + i, TileEntityRefinery.REFINING_TABLE[i]));
        }
        return recipes;
    }

    @Override
    public Class<RefineryNEIRecipe> getRecipeClass(){
        return RefineryNEIRecipe.class;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(RefineryNEIRecipe recipe){
        return recipe;
    }

    @Override
    public boolean isRecipeValid(RefineryNEIRecipe recipe){
        return true;
    }
}
