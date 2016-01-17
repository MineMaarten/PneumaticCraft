package pneumaticCraft.common.sensor;

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
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.util.Rectangle;

import pneumaticCraft.api.universalSensor.IBlockAndCoordinateEventSensor;
import pneumaticCraft.api.universalSensor.IBlockAndCoordinatePollSensor;
import pneumaticCraft.api.universalSensor.IEventSensorSetting;
import pneumaticCraft.api.universalSensor.IPollSensorSetting;
import pneumaticCraft.api.universalSensor.ISensorRegistry;
import pneumaticCraft.api.universalSensor.ISensorSetting;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.sensor.eventSensors.BlockInteractSensor;
import pneumaticCraft.common.sensor.eventSensors.PlayerAttackSensor;
import pneumaticCraft.common.sensor.eventSensors.PlayerItemPickupSensor;
import pneumaticCraft.common.sensor.pollSensors.BlockComparatorSensor;
import pneumaticCraft.common.sensor.pollSensors.BlockHeatSensor;
import pneumaticCraft.common.sensor.pollSensors.BlockLightLevelSensor;
import pneumaticCraft.common.sensor.pollSensors.BlockMetadataSensor;
import pneumaticCraft.common.sensor.pollSensors.BlockPresenceSensor;
import pneumaticCraft.common.sensor.pollSensors.BlockRedstoneSensor;
import pneumaticCraft.common.sensor.pollSensors.PlayerHealthSensor;
import pneumaticCraft.common.sensor.pollSensors.TwitchStreamerSensor;
import pneumaticCraft.common.sensor.pollSensors.UserSetSensor;
import pneumaticCraft.common.sensor.pollSensors.WorldDayLightSensor;
import pneumaticCraft.common.sensor.pollSensors.WorldGlobalVariableSensor;
import pneumaticCraft.common.sensor.pollSensors.WorldPlayersInServerSensor;
import pneumaticCraft.common.sensor.pollSensors.WorldRainingSensor;
import pneumaticCraft.common.sensor.pollSensors.WorldTicktimeSensor;
import pneumaticCraft.common.sensor.pollSensors.WorldTimeSensor;
import pneumaticCraft.common.sensor.pollSensors.WorldWeatherForecaster;
import pneumaticCraft.common.sensor.pollSensors.entity.EntityInRangeSensor;
import pneumaticCraft.common.tileentity.TileEntityUniversalSensor;
import pneumaticCraft.common.util.PneumaticCraftUtils;

public class SensorHandler implements ISensorRegistry{
    private static final SensorHandler INSTANCE = new SensorHandler();

    public static SensorHandler getInstance(){
        return INSTANCE;
    }

    public void init(){
        registerSensor(new EntityInRangeSensor());
        registerSensor(new PlayerAttackSensor());
        registerSensor(new PlayerItemPickupSensor());
        registerSensor(new BlockInteractSensor());
        registerSensor(new WorldDayLightSensor());
        registerSensor(new WorldRainingSensor());
        registerSensor(new WorldTimeSensor());
        registerSensor(new WorldWeatherForecaster());
        registerSensor(new WorldPlayersInServerSensor());
        registerSensor(new WorldTicktimeSensor());
        registerSensor(new WorldGlobalVariableSensor());
        registerSensor(new BlockPresenceSensor());
        registerSensor(new BlockMetadataSensor());
        registerSensor(new BlockComparatorSensor());
        registerSensor(new BlockRedstoneSensor());
        registerSensor(new BlockLightLevelSensor());
        registerSensor(new BlockHeatSensor());
        registerSensor(new UserSetSensor());
        registerSensor(new TwitchStreamerSensor());
        registerSensor(new PlayerHealthSensor());
    }

    private final List<ISensorSetting> sensors = new ArrayList<ISensorSetting>();
    private final List<String> sensorPaths = new ArrayList<String>();

    public ISensorSetting getSensorFromPath(String buttonPath){
        for(int i = 0; i < sensorPaths.size(); i++) {
            if(sensorPaths.get(i).equals(buttonPath)) return sensors.get(i);
        }
        return null;
    }

    public ISensorSetting getSensorByIndex(int index){
        return sensors.get(index);
    }

    public String[] getSensorNames(){
        String[] sensorNames = new String[sensorPaths.size()];
        for(int i = 0; i < sensorNames.length; i++) {
            sensorNames[i] = sensorPaths.get(i).substring(sensorPaths.get(i).lastIndexOf('/') + 1);
        }
        return sensorNames;
    }

    /**
     * The last part of the path
     * @param name
     * @return
     */
    public ISensorSetting getSensorForName(String name){
        String[] sensorNames = getSensorNames();
        for(int i = 0; i < sensorNames.length; i++) {
            if(sensorNames[i].equals(name)) return sensors.get(i);
        }
        return null;
    }

    public List<String> getUpgradeInfo(){
        List<String> text = new ArrayList<String>();
        text.add(EnumChatFormatting.GRAY + "The following combinations of upgrades are used in sensors to work:");
        for(String sensorPath : sensorPaths) {
            Set<Item> requiredStacks = getRequiredStacksFromText(sensorPath.split("/")[0]);
            String upgradeTitle = "";
            for(Item stack : requiredStacks) {
                upgradeTitle = upgradeTitle + stack.getUnlocalizedName() + " + "; //TODO 1.8 localize
            }
            upgradeTitle = EnumChatFormatting.BLACK + "-" + upgradeTitle.substring(0, upgradeTitle.length() - 3).replace("Machine Upgrade: ", "");
            if(!text.contains(upgradeTitle)) text.add(upgradeTitle);
        }
        return text;
    }

    public Set<Item> getUniversalSensorUpgrades(){
        Set<Item> items = new HashSet<Item>();
        for(ISensorSetting sensor : sensors) {
            items.addAll(sensor.getRequiredUpgrades());
        }
        return items;
    }

    private String sortRequiredUpgrades(String path){
        String[] requiredUpgrades = path.split("/")[0].split("_");
        PneumaticCraftUtils.sortStringArrayAlphabetically(requiredUpgrades);
        String newPath = "";
        for(String upgrade : requiredUpgrades) {
            newPath = newPath + upgrade + "_";
        }
        return newPath.substring(0, newPath.length() - 1) + path.replace(path.split("/")[0], "");//cut off the last '_'
    }

    public String[] getDirectoriesAtLocation(String path){
        List<String> directories = new ArrayList<String>();
        for(String sensorPath : sensorPaths) {
            if(sensorPath.startsWith(path) && !sensorPath.equals(path)) {

                //if path equals "entityTracker/player/" and sensor path equals "entityTracker/player/speed", to directories will "speed" be added.
                String[] folders = sensorPath.substring(path.length()).split("/");
                if(folders[0].equals("") && folders.length > 1) {
                    if(!directories.contains(folders[1])) directories.add(folders[1]);
                } else {
                    if(!directories.contains(folders[0])) directories.add(folders[0]);
                }

            }
        }
        String[] directoryArray = directories.toArray(new String[directories.size()]);
        PneumaticCraftUtils.sortStringArrayAlphabetically(directoryArray);
        return directoryArray;
    }

    public Set<Item> getRequiredStacksFromText(String text){
        return new HashSet<Item>(); //TODO 1.8
    }

    @Override
    public void registerSensor(ISensorSetting sensor){
        sensors.add(sensor);
        sensorPaths.add(sortRequiredUpgrades(sensor.getSensorPath()));
    }

    @Override
    public void registerSensor(IBlockAndCoordinateEventSensor sensor){
        registerSensor(new BlockAndCoordinateEventSensor(sensor));
    }

    @Override
    public void registerSensor(IBlockAndCoordinatePollSensor sensor){
        registerSensor(new BlockAndCoordinatePollSensor(sensor));
    }

    private class BlockAndCoordinateEventSensor implements IEventSensorSetting{
        private final IBlockAndCoordinateEventSensor coordinateSensor;

        public BlockAndCoordinateEventSensor(IBlockAndCoordinateEventSensor sensor){
            coordinateSensor = sensor;
        }

        @Override
        public String getSensorPath(){
            return coordinateSensor.getSensorPath();
        }

        @Override
        public boolean needsTextBox(){
            return coordinateSensor.needsTextBox();
        }

        @Override
        public List<String> getDescription(){
            return coordinateSensor.getDescription();
        }

        @Override
        public int emitRedstoneOnEvent(Event event, TileEntity tile, int sensorRange, String textboxText){
            TileEntityUniversalSensor teUs = (TileEntityUniversalSensor)tile;
            Set<BlockPos> positions = teUs.getGPSPositions();
            return positions == null ? 0 : coordinateSensor.emitRedstoneOnEvent(event, teUs, sensorRange, positions);
        }

        @Override
        public int getRedstonePulseLength(){
            return coordinateSensor.getRedstonePulseLength();
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void drawAdditionalInfo(FontRenderer fontRenderer){
            coordinateSensor.drawAdditionalInfo(fontRenderer);
        }

        @Override
        public Rectangle needsSlot(){
            return coordinateSensor.needsSlot();
        }

        @Override
        public Set<Item> getRequiredUpgrades(){
            Set<Item> upgrades = new HashSet<Item>(coordinateSensor.getRequiredUpgrades());
            upgrades.add(Itemss.GPSTool);
            return upgrades;
        }
    }

    private class BlockAndCoordinatePollSensor implements IPollSensorSetting{
        private final IBlockAndCoordinatePollSensor coordinateSensor;

        public BlockAndCoordinatePollSensor(IBlockAndCoordinatePollSensor sensor){
            coordinateSensor = sensor;
        }

        @Override
        public String getSensorPath(){
            return coordinateSensor.getSensorPath();
        }

        @Override
        public boolean needsTextBox(){
            return coordinateSensor.needsTextBox();
        }

        @Override
        public List<String> getDescription(){
            return coordinateSensor.getDescription();
        }

        @Override
        public int getPollFrequency(TileEntity te){
            TileEntityUniversalSensor us = (TileEntityUniversalSensor)te;
            Set<BlockPos> positions = us.getGPSPositions();
            int mult = positions == null ? 1 : positions.size();
            return coordinateSensor.getPollFrequency() * mult;
        }

        @Override
        public int getRedstoneValue(World world, BlockPos pos, int sensorRange, String textBoxText){
            TileEntity te = world.getTileEntity(pos);
            if(te instanceof TileEntityUniversalSensor) {
                TileEntityUniversalSensor teUs = (TileEntityUniversalSensor)te;
                Set<BlockPos> positions = teUs.getGPSPositions();
                return positions == null ? 0 : coordinateSensor.getRedstoneValue(world, pos, sensorRange, textBoxText, positions);
            }
            return 0;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void drawAdditionalInfo(FontRenderer fontRenderer){
            coordinateSensor.drawAdditionalInfo(fontRenderer);
        }

        @Override
        public Rectangle needsSlot(){
            return coordinateSensor.needsSlot();
        }

        @Override
        public Set<Item> getRequiredUpgrades(){
            Set<Item> upgrades = new HashSet<Item>(coordinateSensor.getRequiredUpgrades());
            upgrades.add(Itemss.GPSTool);
            return upgrades;
        }
    }

}
