package pneumaticCraft.client.render.tileentity;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.client.FMLClientHandler;

import org.lwjgl.opengl.GL11;

import pneumaticCraft.common.block.BlockPressureTube;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.block.tubes.TubeModule;
import pneumaticCraft.common.tileentity.TileEntityPressureTube;

public class PressureTubeModuleRenderer extends TileEntitySpecialRenderer<TileEntityPressureTube>{

    @Override
    public void renderTileEntityAt(TileEntityPressureTube tile, double x, double y, double z, float partialTicks, int destroyStage){
        GL11.glPushMatrix();

        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        GlStateManager.disableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.color(1, 1, 1);

        GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
        GL11.glRotatef(0, 0.0F, 1.0F, 0.0F);

        GL11.glScalef(1.0F, -1F, -1F);
        attachFakeModule(tile);
        boolean[] renderSides = Arrays.copyOf(tile.sidesConnected, tile.sidesConnected.length);
        for(int i = 0; i < 6; i++) {
            if(tile.modules[i] != null && tile.modules[i].isInline()) {
                renderSides[i] = true;
            }
        }
        GL11.glPopMatrix();

        for(int i = 0; i < tile.modules.length; i++) {
            TubeModule module = tile.modules[i];
            if(module != null) {
                if(module.isFake()) {
                    /*      GL11.glEnable(GL11.GL_BLEND);
                          GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
                          GL11.glColor4d(1, 1, 1, 0.3);*/
                }

                module.renderDynamic(x, y, z, partialTicks, 0, false);
                if(module.isFake()) {
                    tile.modules[i] = null;
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glColor4d(1, 1, 1, 1);
                }
            }
        }
        GL11.glColor4d(1, 1, 1, 1);

        GlStateManager.enableLighting();
        GlStateManager.enableAlpha();
    }

    private void attachFakeModule(TileEntityPressureTube tile){
        MovingObjectPosition pos = Minecraft.getMinecraft().objectMouseOver;
        if(pos != null && pos.getBlockPos() != null && pos.getBlockPos().equals(tile.getPos()) && Minecraft.getMinecraft().theWorld.getTileEntity(pos.getBlockPos()) == tile) {
            ((BlockPressureTube)Blockss.pressureTube).tryPlaceModule(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().theWorld, tile.getPos(), pos.sideHit, true);
        }
    }
}
