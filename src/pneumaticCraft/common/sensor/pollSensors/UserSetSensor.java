package pneumaticCraft.common.sensor.pollSensors;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.util.Rectangle;

import pneumaticCraft.api.universalSensor.IPollSensorSetting;

public class UserSetSensor implements IPollSensorSetting{

    @Override
    public String getSensorPath(){
        return "dispenser/Constant";
    }

    @Override
    public int getPollFrequency(TileEntity te){
        return 1;
    }

    @Override
    public boolean needsTextBox(){
        return true;
    }

    @Override
    public List<String> getDescription(){
        List<String> text = new ArrayList<String>();
        text.add("gui.universalSensor.desc.userSetSensor");
        return text;
    }

    @Override
    public int getRedstoneValue(World world, BlockPos pos, int sensorRange, String textBoxText){
        try {
            return Math.min(15, Math.max(0, Integer.parseInt(textBoxText)));
        } catch(Exception e) {
            return 0;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawAdditionalInfo(FontRenderer fontRenderer){}

    @Override
    public Rectangle needsSlot(){
        return null;
    }

}