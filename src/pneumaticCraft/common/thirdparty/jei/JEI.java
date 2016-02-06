package pneumaticCraft.common.thirdparty.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IItemRegistry;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import pneumaticCraft.client.gui.GuiAssemblyController;
import pneumaticCraft.client.gui.GuiPressureChamber;
import pneumaticCraft.client.gui.GuiRefinery;
import pneumaticCraft.client.gui.GuiThermopneumaticProcessingPlant;
import pneumaticCraft.client.gui.widget.WidgetTank;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.recipes.AssemblyRecipe;
import pneumaticCraft.common.recipes.PneumaticRecipeRegistry;
import pneumaticCraft.common.recipes.PressureChamberRecipe;
import pneumaticCraft.lib.Log;

@JEIPlugin
public class JEI implements IModPlugin{
    private IJeiHelpers jeiHelpers;

    @Override
    public void onJeiHelpersAvailable(IJeiHelpers jeiHelpers){
        this.jeiHelpers = jeiHelpers;
    }

    @Override
    public void onItemRegistryAvailable(IItemRegistry itemRegistry){

    }

    @Override
    public void register(IModRegistry registry){
        Log.info("Initializing PneumaticCraft JEI plugin...");

        register(registry, new JEIPressureChamberRecipeManager(jeiHelpers));
        register(registry, new JEIAssemblyControllerRecipeManager(jeiHelpers));
        register(registry, new JEIThermopneumaticProcessingPlantManager(jeiHelpers));
        register(registry, new JEIRefineryManager(jeiHelpers));
        register(registry, new JEIEtchingAcidManager(registry, jeiHelpers));
        register(registry, new JEIUVLightBoxManager(registry, jeiHelpers));
        register(registry, new JEIAmadronTradeManager(jeiHelpers));
        register(registry, new JEIPlasticMixerManager(jeiHelpers));

        registry.addRecipes(PneumaticRecipeRegistry.getInstance().thermopneumaticProcessingPlantRecipes);
        registry.addRecipes(PressureChamberRecipe.chamberRecipes);
        registry.addRecipes(AssemblyRecipe.drillRecipes);
        registry.addRecipes(AssemblyRecipe.laserRecipes);
        registry.addRecipes(AssemblyRecipe.drillLaserRecipes);
        registry.addRecipes(new JEIPlasticMixerManager(jeiHelpers).getAllRecipes());
        registry.addRecipes(new JEIRefineryManager(jeiHelpers).getAllRecipes());

        registry.addRecipeClickArea(GuiAssemblyController.class, 68, 75, 24, 17, new JEIAssemblyControllerRecipeManager(jeiHelpers).getUid());
        registry.addRecipeClickArea(GuiPressureChamber.class, 100, 7, 40, 40, new JEIPressureChamberRecipeManager(jeiHelpers).getUid());
        registry.addRecipeClickArea(GuiRefinery.class, 25, 20, 48, 22, new JEIRefineryManager(jeiHelpers).getUid());
        registry.addRecipeClickArea(GuiThermopneumaticProcessingPlant.class, 25, 20, 48, 22, new JEIThermopneumaticProcessingPlantManager(jeiHelpers).getUid());

        jeiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(Blockss.droneRedstoneEmitter, 1, OreDictionary.WILDCARD_VALUE));

        registry.addAdvancedGuiHandlers(new GuiTabHandler());
    }

    private void register(IModRegistry registry, PneumaticCraftPlugins plugin){
        registry.addRecipeCategories(plugin);
        if(plugin instanceof IRecipeHandler) registry.addRecipeHandlers((IRecipeHandler)plugin);
    }

    @Override
    public void onRecipeRegistryAvailable(IRecipeRegistry recipeRegistry){

    }

    public static List<ItemStack> toItemStacks(List<PositionedStack> positioned){
        List<ItemStack> stacks = new ArrayList<ItemStack>(positioned.size());
        for(PositionedStack stack : positioned) {
            stacks.addAll(stack.getStacks());
        }
        return stacks;
    }

    public static List<FluidStack> toFluidStacks(List<WidgetTank> widgets){
        List<FluidStack> stacks = new ArrayList<FluidStack>(widgets.size());
        for(WidgetTank widget : widgets) {
            stacks.add(widget.getFluid());
        }
        return stacks;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime){

    }

}
