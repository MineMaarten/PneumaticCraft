package pneumaticCraft.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiStatBalloon{
    public int x;
    public int y;
    public int slotNumber;
    public String text = "";

    public GuiStatBalloon(int x, int y){
        this.x = x;
        this.y = y;
    }

    public GuiStatBalloon(int x, int y, int slotNumber){
        this(x, y);
        this.slotNumber = slotNumber;
    }

    public void render(){
        FontRenderer fontRenderer = FMLClientHandler.instance().getClient().fontRendererObj;
        fontRenderer.drawString(text, x, y, -90);
    }

}
