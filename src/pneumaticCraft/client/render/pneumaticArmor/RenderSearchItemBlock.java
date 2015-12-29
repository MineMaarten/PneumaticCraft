package pneumaticCraft.client.render.pneumaticArmor;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;

import org.lwjgl.opengl.GL11;

import pneumaticCraft.common.item.ItemPneumaticArmor;
import pneumaticCraft.common.util.PneumaticCraftUtils;

public class RenderSearchItemBlock{

    private final BlockPos pos;
    private final World world;

    public RenderSearchItemBlock(World world, BlockPos pos){
        this.world = world;
        this.pos = pos;
    }

    private int getSearchedItemCount(){
        if(world.getTileEntity(pos) instanceof IInventory) {
            int itemCount = 0;
            IInventory inventory = (IInventory)world.getTileEntity(pos);
            ItemStack searchStack = ItemPneumaticArmor.getSearchedStack(FMLClientHandler.instance().getClient().thePlayer.getCurrentArmor(3));
            if(searchStack == null) return 0;
            for(int l = 0; l < inventory.getSizeInventory(); l++) {
                if(inventory.getStackInSlot(l) != null) {
                    itemCount += getSearchedItemCount(inventory.getStackInSlot(l), searchStack);
                }
            }
            return itemCount;
        }
        return 0;
    }

    public static int getSearchedItemCount(ItemStack stack, ItemStack searchStack){
        int itemCount = 0;
        if(stack.isItemEqual(searchStack)) {
            itemCount += stack.stackSize;
        }
        List<ItemStack> inventoryItems = PneumaticCraftUtils.getStacksInItem(stack);
        for(ItemStack s : inventoryItems) {
            itemCount += getSearchedItemCount(s, searchStack);
        }
        return itemCount;
    }

    public boolean isAlreadyTrackingCoord(BlockPos pos){
        return pos.equals(this.pos);
    }

    public boolean renderSearchBlock(int totalCount){
        int itemCount = getSearchedItemCount();
        renderSearch(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, itemCount, totalCount);
        return itemCount > 0;
    }

    public static void renderSearch(double x, double y, double z, int itemCount, int totalCount){
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glColor4d(0, 1, 0, 0.5D);
        GL11.glRotatef(180.0F - Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F - Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        // GL11.glLineWidth(1.0F);
        double ratio = (double)itemCount / totalCount;
        double diff = (1 - ratio) / 1.5D;
        double size = 1 - diff;
        /*
        for(double i = size; i > 0; i -= 0.06D) {
            GL11.glPushMatrix();
            GL11.glScaled(i, i, i);
            renderCircle();
            GL11.glPopMatrix();
        }
        */
        WorldRenderer wr = Tessellator.getInstance().getWorldRenderer();
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        wr.pos(-size, size, 0).tex(0, 1).endVertex();
        wr.pos(-size, -size, 0).tex(0, 0).endVertex();
        wr.pos(size, -size, 0).tex(1, 0).endVertex();
        wr.pos(size, size, 0).tex(1, 1).endVertex();

        Tessellator.getInstance().draw();

        GL11.glPopMatrix();
    }
    /*
        private static void renderCircle(){

            WorldRenderer wr = Tessellator.getInstance().getWorldRenderer();
            tess.startDrawing(GL11.GL_POLYGON);
            for(int i = 0; i < PneumaticCraftUtils.circlePoints; i++) {
                wr.pos(PneumaticCraftUtils.sin[i], PneumaticCraftUtils.cos[i], 0);
            }

            Tessellator.getInstance().draw();

        }
        */
}
