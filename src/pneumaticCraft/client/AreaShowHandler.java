package pneumaticCraft.client;

import java.util.Set;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockPos;

import org.lwjgl.opengl.GL11;

import pneumaticCraft.client.util.RenderUtils;

public class AreaShowHandler{
    private final Set<BlockPos> showingPositions;
    private final int color;
    private int renderList;

    public AreaShowHandler(Set<BlockPos> area, int color){
        showingPositions = area;
        this.color = color;
        compileRenderList();
    }

    private void compileRenderList(){
        renderList = GL11.glGenLists(1);
        GL11.glNewList(renderList, GL11.GL_COMPILE);

        WorldRenderer wr = Tessellator.getInstance().getWorldRenderer();
        RenderUtils.glColorHex(color, 150);
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        for(BlockPos pos : showingPositions) {
            wr.setTranslation(pos.getX() + 0.25F, pos.getY() + 0.25F, pos.getZ() + 0.25F);

            wr.pos(0, 0, 0).endVertex();
            wr.pos(0, 0.5, 0).endVertex();
            wr.pos(0.5, 0.5, 0).endVertex();
            wr.pos(0.5, 0, 0).endVertex();

            wr.pos(0.5, 0, 0.5).endVertex();
            wr.pos(0.5, 0.5, 0.5).endVertex();
            wr.pos(0, 0.5, 0.5).endVertex();
            wr.pos(0, 0, 0.5).endVertex();

            wr.pos(0, 0, 0).endVertex();
            wr.pos(0, 0, 0.5).endVertex();
            wr.pos(0, 0.5, 0.5).endVertex();
            wr.pos(0, 0.5, 0).endVertex();

            wr.pos(0.5, 0.5, 0).endVertex();
            wr.pos(0.5, 0.5, 0.5).endVertex();
            wr.pos(0.5, 0, 0.5).endVertex();
            wr.pos(0.5, 0, 0).endVertex();

            wr.pos(0, 0, 0).endVertex();
            wr.pos(0.5, 0, 0).endVertex();
            wr.pos(0.5, 0, 0.5).endVertex();
            wr.pos(0, 0, 0.5).endVertex();

            wr.pos(0, 0.5, 0.5).endVertex();
            wr.pos(0.5, 0.5, 0.5).endVertex();
            wr.pos(0.5, 0.5, 0).endVertex();
            wr.pos(0, 0.5, 0).endVertex();
        }

        Tessellator.getInstance().draw();

        wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        RenderUtils.glColorHex(0, 150);

        for(BlockPos pos : showingPositions) {
            wr.setTranslation(pos.getX() + 0.25F, pos.getY() + 0.25F, pos.getZ() + 0.25F);

            wr.pos(0, 0, 0).endVertex();
            wr.pos(0, 0.5, 0).endVertex();
            wr.pos(0.5, 0.5, 0).endVertex();
            wr.pos(0.5, 0, 0).endVertex();

            wr.pos(0.5, 0, 0.5).endVertex();
            wr.pos(0.5, 0.5, 0.5).endVertex();
            wr.pos(0, 0.5, 0.5).endVertex();
            wr.pos(0, 0, 0.5).endVertex();

            wr.pos(0, 0, 0).endVertex();
            wr.pos(0, 0, 0.5).endVertex();
            wr.pos(0, 0.5, 0.5).endVertex();
            wr.pos(0, 0.5, 0).endVertex();

            wr.pos(0.5, 0.5, 0).endVertex();
            wr.pos(0.5, 0.5, 0.5).endVertex();
            wr.pos(0.5, 0, 0.5).endVertex();
            wr.pos(0.5, 0, 0).endVertex();

            wr.pos(0, 0, 0).endVertex();
            wr.pos(0.5, 0, 0).endVertex();
            wr.pos(0.5, 0, 0.5).endVertex();
            wr.pos(0, 0, 0.5).endVertex();

            wr.pos(0, 0.5, 0.5).endVertex();
            wr.pos(0.5, 0.5, 0.5).endVertex();
            wr.pos(0.5, 0.5, 0).endVertex();
            wr.pos(0, 0.5, 0).endVertex();
        }

        wr.setTranslation(0, 0, 0);
        Tessellator.getInstance().draw();
        GL11.glEndList();
    }

    public void render(){
        GL11.glCallList(renderList);
    }
}
