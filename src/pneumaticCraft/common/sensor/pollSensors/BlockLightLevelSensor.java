package pneumaticCraft.common.sensor.pollSensors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.util.Rectangle;

import pneumaticCraft.api.item.IItemRegistry.EnumUpgrade;
import pneumaticCraft.api.universalSensor.IBlockAndCoordinatePollSensor;
import pneumaticCraft.common.item.Itemss;

public class BlockLightLevelSensor implements IBlockAndCoordinatePollSensor{

    @Override
    public String getSensorPath(){
        return "Block/Light Level";
    }

    @Override
    public Set<Item> getRequiredUpgrades(){
        Set<Item> upgrades = new HashSet<Item>();
        upgrades.add(Itemss.upgrades.get(EnumUpgrade.BLOCK_TRACKER));
        upgrades.add(Itemss.GPSTool);
        return upgrades;
    }

    @Override
    public int getPollFrequency(){
        return 5;
    }

    @Override
    public boolean needsTextBox(){
        return false;
    }

    @Override
    public List<String> getDescription(){
        List<String> text = new ArrayList<String>();
        text.add(EnumChatFormatting.BLACK + "Emits a redstone of which the strength is equal to the light level at the location stored in the GPS Tool. In case of multiple locations, the location with the highest light value is used.");
        return text;
    }

    @Override
    public int getRedstoneValue(World world, BlockPos pos, int sensorRange, String textBoxText, Set<BlockPos> positions){
        int lightValue = 0;
        for(BlockPos p : positions) {
            lightValue = Math.max(lightValue, world.getLight(p));
        }
        return lightValue;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawAdditionalInfo(FontRenderer fontRenderer){}

    @Override
    public Rectangle needsSlot(){
        return null;
    }

}