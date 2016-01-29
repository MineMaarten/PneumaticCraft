package pneumaticCraft.common.thirdparty.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import pneumaticCraft.common.recipes.PressureChamberRecipe;
import pneumaticCraft.common.util.OreDictionaryHelper;
import pneumaticCraft.lib.PneumaticValues;
import pneumaticCraft.lib.Textures;

public class JEIPressureChamberRecipeManager extends PneumaticCraftPlugins<PressureChamberRecipe>{
    public JEIPressureChamberRecipeManager(IJeiHelpers jeiHelpers){
        super(jeiHelpers);
    }

    public class ChamberRecipe extends MultipleInputOutputRecipe{
        public float recipePressure;

        @Override
        public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight){
            drawAnimatedPressureGauge(120, 27, -1, recipePressure, PneumaticValues.DANGER_PRESSURE_PRESSURE_CHAMBER, PneumaticValues.MAX_PRESSURE_PRESSURE_CHAMBER);
        }
    }

    @Override
    public String getTitle(){
        return "Pressure Chamber";
    }

    @Override
    public ResourceDrawable getGuiTexture(){
        return new ResourceDrawable(Textures.GUI_NEI_PRESSURE_CHAMBER_LOCATION, 0, 0, 5, 11, 166, 130);
    }

    protected ChamberRecipe getShape(PressureChamberRecipe recipe){
        ChamberRecipe shape = new ChamberRecipe();
        for(int i = 0; i < recipe.input.length; i++) {
            PositionedStack stack;
            int posX = 19 + i % 3 * 17;
            int posY = 93 - i / 3 * 17;

            if(recipe.input[i] instanceof Pair) {
                List<ItemStack> oreInputs = new ArrayList<ItemStack>();

                Pair<String, Integer> oreDictEntry = (Pair<String, Integer>)recipe.input[i];
                for(ItemStack s : OreDictionaryHelper.getOreDictEntries(oreDictEntry.getKey())) {
                    s = s.copy();
                    s.stackSize = oreDictEntry.getValue();
                    oreInputs.add(s);
                }
                stack = new PositionedStack(oreInputs, posX, posY);
            } else {
                stack = new PositionedStack((ItemStack)recipe.input[i], posX, posY);
            }
            shape.addIngredient(stack);
        }

        for(int i = 0; i < recipe.output.length; i++) {
            PositionedStack stack = new PositionedStack(recipe.output[i], 101 + i % 3 * 18, 59 + i / 3 * 18);
            shape.addOutput(stack);
        }
        shape.recipePressure = recipe.pressure;
        return shape;
    }

    @Override
    public Class<PressureChamberRecipe> getRecipeClass(){
        return PressureChamberRecipe.class;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(PressureChamberRecipe recipe){
        return getShape(recipe);
    }

    @Override
    public boolean isRecipeValid(PressureChamberRecipe recipe){
        return true;
    }

}
