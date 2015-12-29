package pneumaticCraft.client.gui.widget;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import pneumaticCraft.client.util.RenderUtils;
import pneumaticCraft.lib.Textures;

/**
 * This class is derived from BluePower and edited by MineMaarten:
 * https://github.com/Qmunity/BluePower/blob/FluidCrafting/src/main/java/com/bluepowermod/client/gui/widget/WidgetTank.java
 */
public class WidgetTank extends WidgetBase{

    private final IFluidTank tank;

    public WidgetTank(int id, int x, int y, IFluidTank tank){
        super(id, x, y, 16, 64);
        this.tank = tank;
    }

    public WidgetTank(int x, int y, FluidStack stack){
        super(-1, x, y, 16, 64);
        tank = new FluidTank(stack, 16000);
    }

    public WidgetTank(int x, int y, int width, int height, FluidStack stack){
        super(-1, x, y, width, height);
        tank = new FluidTank(stack, stack.amount);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick){
        GL11.glDisable(GL11.GL_LIGHTING);

        Fluid fluid = tank.getFluid() != null ? tank.getFluid().getFluid() : null;
        ResourceLocation icon = fluid != null ? fluid.getStill() : null;
        int amt = tank.getFluidAmount();
        int capacity = tank.getCapacity();
        int height = 64;
        int width = 16;

        if(fluid != null && icon != null && amt > 0 && capacity > 0) {
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

            double fluidPercentage = amt / (double)capacity;
            double fluidHeight = height * fluidPercentage;

            GL11.glPushMatrix();
            {
                GL11.glTranslated(0, height, 0);
                GL11.glEnable(GL11.GL_BLEND);
                while(fluidHeight > 0) {
                    int iconHeight = 16; //TODO 1.8 iconHeight
                    double moved = Math.min(fluidHeight, iconHeight);
                    GL11.glTranslated(0, -moved, 0);
                    WorldRenderer wr = Tessellator.getInstance().getWorldRenderer();
                    wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                    RenderUtils.glColorHex(fluid.getColor(tank.getFluid()));
                    {
                        wr.pos(x, y, 0).tex(0, 0 + (1 - 0) * (1 - moved / iconHeight)).endVertex();
                        wr.pos(x, y + moved, 0).tex(0, 1).endVertex();
                        wr.pos(x + width, y + moved, 0).tex(1, 1).endVertex();
                        wr.pos(x + width, y, 0).tex(1, 0 + (1 - 0) * (1 - moved / iconHeight)).endVertex();
                    }
                    Tessellator.getInstance().draw();
                    fluidHeight -= moved;
                }
                GL11.glDisable(GL11.GL_BLEND);
            }
            GL11.glPopMatrix();
        }

        GL11.glColor4d(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(Textures.WIDGET_TANK);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 64, 16, 64);
    }

    @Override
    public void addTooltip(int mouseX, int mouseY, List<String> curTip, boolean shift){
        Fluid fluid = null;
        int amt = 0;
        int capacity = 0;

        if(tank.getFluid() != null) {
            fluid = tank.getFluid().getFluid();
            amt = tank.getFluidAmount();
        }
        capacity = tank.getCapacity();

        if(fluid == null || amt == 0 || capacity == 0) {
            curTip.add(amt + "/" + capacity + " mb");
            curTip.add(EnumChatFormatting.GRAY + I18n.format("gui.liquid.empty"));
        } else {
            curTip.add(amt + "/" + capacity + " mb");
            curTip.add(EnumChatFormatting.GRAY + fluid.getLocalizedName(new FluidStack(fluid, amt)));
        }
    }

    public FluidStack getFluid(){
        return tank.getFluid();
    }

    @SideOnly(Side.CLIENT)
    public FluidTank getTank(){
        return (FluidTank)tank;
    }
}
