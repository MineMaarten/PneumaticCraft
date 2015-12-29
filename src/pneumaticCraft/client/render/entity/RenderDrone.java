package pneumaticCraft.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import pneumaticCraft.client.model.entity.ModelDrone;
import pneumaticCraft.common.entity.living.EntityDroneBase;
import pneumaticCraft.lib.Textures;

public class RenderDrone extends RendererLivingEntity<EntityDroneBase>{
    private final ModelDrone model;

    public RenderDrone(boolean isLogisticsDrone){
        super(Minecraft.getMinecraft().getRenderManager(), null, 0);
        model = new ModelDrone(isLogisticsDrone);
    }

    public void renderDrone(EntityDroneBase drone, double par2, double par4, double par6, float par8, float par9){
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);

        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0.76F, 0);
        GL11.glScalef(0.5F, -0.5F, -0.5F);
        bindEntityTexture(drone);
        model.setLivingAnimations(drone, 0, 0, par9);
        model.render(drone, 0, 0, 0, 0, par9, 1 / 16F);
        GL11.glPopMatrix();

        drone.renderExtras(par2, par4, par6, par9);
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityDroneBase par1Entity){
        return Textures.MODEL_DRONE;
    }

    @Override
    public void doRender(EntityDroneBase par1Entity, double par2, double par4, double par6, float par8, float par9){
        renderDrone(par1Entity, par2, par4, par6, par8, par9);
        renderName(par1Entity, par2, par4, par6); //TODO 1.8 test (renaming)
    }

    @Override
    protected boolean canRenderName(EntityDroneBase p_110813_1_){
        return super.canRenderName(p_110813_1_) && (p_110813_1_.getAlwaysRenderNameTagForRender() || ((EntityLiving)p_110813_1_).hasCustomName() && p_110813_1_ == renderManager.pointedEntity);
    }
}
