package pneumaticCraft.common.sensor.pollSensors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.lwjgl.util.Rectangle;

import pneumaticCraft.api.item.IItemRegistry.EnumUpgrade;
import pneumaticCraft.api.universalSensor.IPollSensorSetting;
import pneumaticCraft.common.item.Itemss;

public class TwitchStreamerSensor implements IPollSensorSetting{

    @Override
    public String getSensorPath(){
        return "World/Twitch";
    }

    @Override
    public Set<Item> getRequiredUpgrades(){
        Set<Item> upgrades = new HashSet<Item>();
        upgrades.add(Itemss.upgrades.get(EnumUpgrade.DISPENSER));
        return upgrades;
    }

    @Override
    public boolean needsTextBox(){
        return true;
    }

    @Override
    public void drawAdditionalInfo(FontRenderer fontRenderer){

    }

    @Override
    public List<String> getDescription(){
        List<String> info = new ArrayList<String>();
        info.add(EnumChatFormatting.BLACK + "Emits a redstone signal when the name of the streamer typed in is streaming at this moment.");
        return info;
    }

    @Override
    public Rectangle needsSlot(){
        return null;
    }

    @Override
    public int getPollFrequency(TileEntity te){
        return 20;
    }

    @Override
    public int getRedstoneValue(World world, BlockPos pos, int sensorRange, String textBoxText){
        return TwitchStream.isOnline(textBoxText) ? 15 : 0;
    }

}
