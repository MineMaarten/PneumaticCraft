package pneumaticCraft.api.universalSensor;

import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;

import org.lwjgl.util.Rectangle;

public interface IBlockAndCoordinateEventSensor{
    /**
     * See {@link ISensorSetting#getSensorPath()}
     * @return
     */
    public String getSensorPath();

    /**
     * Extended version of the normal emitRedstoneOnEvent. This method will only invoke with a valid GPS tool, and when all the coordinates are within range.
     * @param event
     * @param sensor
     * @param range
     * @param positions When only one GPS Tool is inserted this contains the position of just that tool. If two GPS Tools are inserted, These are both corners of a box, and every coordinate in this box is added to the positions argument.
     * @return
     */
    public int emitRedstoneOnEvent(Event event, TileEntity sensor, int range, Set<BlockPos> positions);

    /**
     * See {@link IEventSensorSetting#getRedstonePulseLength()}
     * @return
     */
    public int getRedstonePulseLength();

    /**
     * See {@link ISensorSetting#needsTextBox()}
     * @return
     */
    public boolean needsTextBox();

    /**
     * See {@link ISensorSetting#needsSlot()}
     */
    public Rectangle needsSlot();

    /**
     * See {@link ISensorSetting#getDescription()}
     * @return
     */
    public List<String> getDescription();

    /**
     * Called by GuiScreen#drawScreen this method can be used to render additional things like status/info text.
     * @param fontRenderer
     */
    public void drawAdditionalInfo(FontRenderer fontRenderer);
}
