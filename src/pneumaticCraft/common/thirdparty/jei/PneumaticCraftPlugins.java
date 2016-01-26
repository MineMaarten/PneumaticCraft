package pneumaticCraft.common.thirdparty.jei;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import pneumaticCraft.api.PneumaticRegistry;
import pneumaticCraft.api.heat.IHeatExchangerLogic;
import pneumaticCraft.client.gui.GuiUtils;
import pneumaticCraft.client.gui.widget.IGuiWidget;
import pneumaticCraft.client.gui.widget.WidgetTank;
import pneumaticCraft.client.gui.widget.WidgetTemperature;
import pneumaticCraft.lib.Textures;

public abstract class PneumaticCraftPlugins<T> implements IRecipeCategory, IRecipeHandler<T>{
    private final IGuiHelper guiHelper;
    private final ResourceDrawable background = getGuiTexture();
    private static ITickTimer tickTimer;

    public PneumaticCraftPlugins(IJeiHelpers jeiHelpers){
        this.guiHelper = jeiHelpers.getGuiHelper();
        tickTimer = guiHelper.createTickTimer(60, 60, false);
    }

    public static class MultipleInputOutputRecipe implements IRecipeWrapper{
        private final List<PositionedStack> input = new ArrayList<PositionedStack>();
        private final List<PositionedStack> output = new ArrayList<PositionedStack>();
        private final List<WidgetTank> inputLiquids = new ArrayList<WidgetTank>();
        private final List<WidgetTank> outputLiquids = new ArrayList<WidgetTank>();
        private final List<IGuiWidget> tooltipWidgets = new ArrayList<IGuiWidget>();
        private float pressure;
        private boolean usePressure;
        private int gaugeX, gaugeY;
        private WidgetTemperature tempWidget;
        private IHeatExchangerLogic heatExchanger;

        public void addIngredient(PositionedStack stack){
            input.add(stack);
        }

        public void addIngredient(PositionedStack[] stacks){
            for(PositionedStack stack : stacks) {
                input.add(stack);
            }
        }

        public void addOutput(PositionedStack stack){
            output.add(stack);
        }

        protected void addInputLiquid(FluidStack liquid, int x, int y){
            WidgetTank tank = new WidgetTank(x, y, liquid);
            addInputLiquid(tank);
        }

        protected void addInputLiquid(WidgetTank tank){
            inputLiquids.add(tank);
            recalculateTankSizes();
        }

        protected void addOutputLiquid(FluidStack liquid, int x, int y){
            WidgetTank tank = new WidgetTank(x, y, liquid);
            addOutputLiquid(tank);
        }

        protected void addOutputLiquid(WidgetTank tank){
            outputLiquids.add(tank);
            recalculateTankSizes();
        }

        private void recalculateTankSizes(){
            int maxFluid = 0;
            for(WidgetTank w : inputLiquids) {
                maxFluid = Math.max(maxFluid, w.getTank().getFluidAmount());
            }
            for(WidgetTank w : outputLiquids) {
                maxFluid = Math.max(maxFluid, w.getTank().getFluidAmount());
            }

            if(maxFluid <= 10) {
                maxFluid = 10;
            } else if(maxFluid <= 100) {
                maxFluid = 100;
            } else if(maxFluid <= 1000) {
                maxFluid = 1000;
            } else {
                maxFluid = 16000;
            }
            for(WidgetTank w : inputLiquids) {
                w.getTank().setCapacity(maxFluid);
            }
            for(WidgetTank w : outputLiquids) {
                w.getTank().setCapacity(maxFluid);
            }
        }

        protected void addWidget(IGuiWidget widget){
            tooltipWidgets.add(widget);
        }

        protected void setUsedPressure(int x, int y, float pressure){
            usePressure = true;
            this.pressure = pressure;
            gaugeX = x;
            gaugeY = y;
        }

        protected void setUsedTemperature(int x, int y, double temperature){
            tempWidget = new WidgetTemperature(0, x, y, 273, 673, heatExchanger = PneumaticRegistry.getInstance().getHeatRegistry().getHeatExchangerLogic(), (int)temperature);
        }

        @Override
        public List getInputs(){
            return JEI.toItemStacks(input);
        }

        @Override
        public List getOutputs(){
            return JEI.toItemStacks(output);
        }

        @Override
        public List<FluidStack> getFluidInputs(){
            return JEI.toFluidStacks(inputLiquids);
        }

        @Override
        public List<FluidStack> getFluidOutputs(){
            return JEI.toFluidStacks(outputLiquids);
        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight){
            // TODO Auto-generated method stub

        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY){
            for(IGuiWidget widget : tooltipWidgets) {
                widget.render(0, 0, 0);
            }
            if(usePressure) {
                drawAnimatedPressureGauge(gaugeX, gaugeY, -1, pressure, 5, 7);
            }
            if(tempWidget != null) {
                heatExchanger.setTemperature(tickTimer.getValue() * (tempWidget.getScales()[0] - 273) / tickTimer.getMaxValue() + 273);
                tempWidget.render(0, 0, 0);
            }
        }

        @Override
        public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight){
            // TODO Auto-generated method stub

        }

        @Override
        public List<String> getTooltipStrings(int mouseX, int mouseY){
            List<String> currenttip = new ArrayList<String>();

            Point mouse = new Point(mouseX, mouseY);
            for(IGuiWidget widget : tooltipWidgets) {
                if(widget.getBounds().contains(mouse)) {
                    widget.addTooltip(mouse.x, mouse.y, currenttip, false);
                }
            }
            if(tempWidget != null) {
                if(tempWidget.getBounds().contains(mouse)) {
                    heatExchanger.setTemperature(tempWidget.getScales()[0]);
                    tempWidget.addTooltip(mouse.x, mouse.y, currenttip, false);
                }
            }

            return currenttip;
        }

        @Override
        public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton){
            return false;
        }

    }

    public static void drawAnimatedPressureGauge(int x, int y, float minPressure, float minWorkingPressure, float dangerPressure, float maxPressure){
        GuiUtils.drawPressureGauge(FMLClientHandler.instance().getClient().fontRendererObj, minPressure, maxPressure, dangerPressure, minWorkingPressure, minWorkingPressure * ((float)tickTimer.getValue() / tickTimer.getMaxValue()), x, y, 90);
    }

    @Override
    @Nonnull
    public String getUid(){
        return getTitle();
    }

    @Override
    public String getRecipeCategoryUid(){
        return getUid();
    }

    public abstract ResourceDrawable getGuiTexture();

    @Override
    public IDrawable getBackground(){
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft){

    }

    @Override
    public void drawAnimations(Minecraft minecraft){

    }

    protected void drawProgressBar(int x, int y, int u, int v, int width, int height, IDrawableAnimated.StartDirection startDirection){
        IDrawableStatic drawable = guiHelper.createDrawable(background.getResource(), u, v, width, height);
        IDrawableAnimated animation = guiHelper.createAnimatedDrawable(drawable, 60, startDirection, false);
        animation.draw(Minecraft.getMinecraft(), x, y);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper){
        if(recipeWrapper instanceof PneumaticCraftPlugins.MultipleInputOutputRecipe) {
            PneumaticCraftPlugins.MultipleInputOutputRecipe recipe = (PneumaticCraftPlugins.MultipleInputOutputRecipe)recipeWrapper;

            for(int i = 0; i < recipe.getInputs().size(); i++) {
                recipeLayout.getItemStacks().init(i, true, recipe.input.get(i).getX() - 1, recipe.input.get(i).getY() - 1);
                recipeLayout.getItemStacks().set(i, recipe.input.get(i).getStacks());
            }

            for(int i = 0; i < recipe.getOutputs().size(); i++) {
                recipeLayout.getItemStacks().init(i + recipe.input.size(), false, recipe.output.get(i).getX() - 1, recipe.output.get(i).getY() - 1);
                recipeLayout.getItemStacks().set(i + recipe.input.size(), recipe.output.get(i).getStacks());
            }

            IDrawable tankOverlay = new ResourceDrawable(Textures.WIDGET_TANK, 0, 0, 0, 0, 16, 64, 16, 64);
            for(int i = 0; i < recipe.getFluidInputs().size(); i++) {
                WidgetTank tank = recipe.inputLiquids.get(i);
                recipeLayout.getFluidStacks().init(i, true, tank.x, tank.y, tank.getBounds().width, tank.getBounds().height, tank.getTank().getCapacity(), true, tankOverlay);
                recipeLayout.getFluidStacks().set(i, tank.getFluid());
            }

            for(int i = 0; i < recipe.getFluidOutputs().size(); i++) {
                WidgetTank tank = recipe.outputLiquids.get(i);
                recipeLayout.getFluidStacks().init(recipe.getFluidInputs().size() + i, false, tank.x, tank.y, tank.getBounds().width, tank.getBounds().height, tank.getTank().getCapacity(), true, tankOverlay);
                recipeLayout.getFluidStacks().set(recipe.getFluidInputs().size() + i, tank.getFluid());
            }

        }
    }
}