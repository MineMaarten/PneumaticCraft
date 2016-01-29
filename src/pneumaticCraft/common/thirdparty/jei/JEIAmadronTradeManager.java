package pneumaticCraft.common.thirdparty.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import pneumaticCraft.client.gui.widget.WidgetAmadronOffer;
import pneumaticCraft.client.gui.widget.WidgetTank;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.recipes.AmadronOffer;
import pneumaticCraft.common.recipes.AmadronOfferManager;
import pneumaticCraft.lib.Textures;

public class JEIAmadronTradeManager extends PneumaticCraftPlugins<AmadronOffer>{
    public JEIAmadronTradeManager(IJeiHelpers jeiHelpers){
        super(jeiHelpers);
    }

    @Override
    public String getTitle(){
        return StatCollector.translateToLocal(Itemss.amadronTablet.getUnlocalizedName() + ".name");
    }

    @Override
    public ResourceDrawable getGuiTexture(){
        return new ResourceDrawable(Textures.WIDGET_AMADRON_OFFER_STRING, 0, 0, 0, 0, 73, 35);
    }

    private class AmadronNEIRecipe extends MultipleInputOutputRecipe{
        private AmadronNEIRecipe(AmadronOffer offer){
            if(offer.getInput() instanceof ItemStack) addIngredient(new PositionedStack((ItemStack)offer.getInput(), 6, 15));
            if(offer.getOutput() instanceof ItemStack) addOutput(new PositionedStack((ItemStack)offer.getOutput(), 51, 15));
            if(offer.getInput() instanceof FluidStack) addInputLiquid(new WidgetCustomTank(6, 15, (FluidStack)offer.getInput()));
            if(offer.getOutput() instanceof FluidStack) addOutputLiquid(new WidgetCustomTank(51, 15, (FluidStack)offer.getOutput()));
            WidgetAmadronOffer widget = new WidgetAmadronOffer(0, 0, 0, offer).setDrawBackground(false);
            widget.setCanBuy(true);
            addWidget(widget);
        }
    }

    protected List<MultipleInputOutputRecipe> getAllRecipes(){
        List<MultipleInputOutputRecipe> recipes = new ArrayList<MultipleInputOutputRecipe>();
        for(AmadronOffer recipe : AmadronOfferManager.getInstance().getAllOffers()) {
            recipes.add(new AmadronNEIRecipe(recipe));
        }
        return recipes;
    }

    private static class WidgetCustomTank extends WidgetTank{

        public WidgetCustomTank(int x, int y, FluidStack stack){
            super(x, y, 16, 16, stack);
        }

        @Override
        public void render(int mouseX, int mouseY, float partialTick){

        }

    }

    @Override
    public Class<AmadronOffer> getRecipeClass(){
        return AmadronOffer.class;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(AmadronOffer recipe){
        return new AmadronNEIRecipe(recipe);
    }

    @Override
    public boolean isRecipeValid(AmadronOffer recipe){
        return true;
    }

}
