package pneumaticCraft.client.render.pneumaticArmor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.api.client.pneumaticHelmet.IOptionPage;
import pneumaticCraft.api.client.pneumaticHelmet.IUpgradeRenderHandler;
import pneumaticCraft.api.item.IItemRegistry.EnumUpgrade;
import pneumaticCraft.client.gui.pneumaticHelmet.GuiCoordinateTrackerOptions;
import pneumaticCraft.client.gui.widget.GuiAnimatedStat;
import pneumaticCraft.common.NBTUtil;
import pneumaticCraft.common.ai.EntityPathNavigateDrone;
import pneumaticCraft.common.config.Config;
import pneumaticCraft.common.entity.living.EntityDrone;
import pneumaticCraft.common.item.ItemPneumaticArmor;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.network.NetworkHandler;
import pneumaticCraft.common.network.PacketCoordTrackUpdate;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.lib.PneumaticValues;

public class CoordTrackUpgradeHandler implements IUpgradeRenderHandler{
    private RenderCoordWireframe coordTracker;
    private RenderNavigator navigator;

    public boolean isListeningToCoordTrackerSetting = false;

    public boolean pathEnabled;
    public boolean wirePath;
    public boolean xRayEnabled;
    private int noPathCooldown; //timer used to delay the client recalculating a path when it didn't last time. This prevents
    //gigantic lag, as it uses much performance to find a path when it doesn't have anything cached.
    private int pathCalculateCooldown;
    public int pathUpdateSetting;
    public static final int SEARCH_RANGE = 150;

    public enum EnumNavigationResult{
        NO_PATH, EASY_PATH, DRONE_PATH;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getUpgradeName(){
        return "coordinateTracker";
    }

    @Override
    public void initConfig(Configuration config){
        pathEnabled = config.get("Helmet_Options" + Configuration.CATEGORY_SPLITTER + "Coordinate_Tracker", "Path Enabled", true).getBoolean(true);
        wirePath = config.get("Helmet_Options" + Configuration.CATEGORY_SPLITTER + "Coordinate_Tracker", "Wire Path", true).getBoolean(true);
        xRayEnabled = config.get("Helmet_Options" + Configuration.CATEGORY_SPLITTER + "Coordinate_Tracker", "X-Ray", false).getBoolean(true);
        pathUpdateSetting = config.get("Helmet_Options" + Configuration.CATEGORY_SPLITTER + "Coordinate_Tracker", "Path Update Rate", 1).getInt();
    }

    @Override
    public void saveToConfig(){
        Configuration config = Config.config;
        config.load();
        config.get("Helmet_Options" + Configuration.CATEGORY_SPLITTER + "Coordinate_Tracker", "Path Enabled", true).set(pathEnabled);
        config.get("Helmet_Options" + Configuration.CATEGORY_SPLITTER + "Coordinate_Tracker", "Wire Path", true).set(wirePath);
        config.get("Helmet_Options" + Configuration.CATEGORY_SPLITTER + "Coordinate_Tracker", "X-Ray", true).set(xRayEnabled);
        config.get("Helmet_Options" + Configuration.CATEGORY_SPLITTER + "Coordinate_Tracker", "Path Update Rate", true).set(pathUpdateSetting);
        config.save();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void update(EntityPlayer player, int rangeUpgrades){
        if(coordTracker != null) coordTracker.ticksExisted++;
        else {
            coordTracker = ItemPneumaticArmor.getCoordTrackLocation(player.getCurrentArmor(3));
            if(coordTracker != null) navigator = new RenderNavigator(coordTracker.worldObj, coordTracker.pos);
        }
        if(noPathCooldown > 0) noPathCooldown--;
        if(navigator != null && pathEnabled && noPathCooldown == 0 && --pathCalculateCooldown <= 0) {
            navigator.updatePath();
            if(!navigator.tracedToDestination()) noPathCooldown = 100;//wait 5 seconds before recalculating a path.
            pathCalculateCooldown = pathUpdateSetting == 2 ? 1 : pathUpdateSetting == 1 ? 20 : 100;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render3D(float partialTicks){
        if(coordTracker != null) {
            if(FMLClientHandler.instance().getClient().thePlayer.worldObj.provider.getDimensionId() != coordTracker.worldObj.provider.getDimensionId()) return;
            coordTracker.render(partialTicks);
            if(pathEnabled && navigator != null) {
                navigator.render(wirePath, xRayEnabled, partialTicks);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render2D(float partialTicks, boolean upgradeEnabled){}

    @Override
    public Item[] getRequiredUpgrades(){
        return new Item[]{Itemss.upgrades.get(EnumUpgrade.COORDINATE_TRACKER)};
    }

    @Override
    public float getEnergyUsage(int rangeUpgrades, EntityPlayer player){
        return PneumaticValues.USAGE_COORD_TRACKER;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void reset(){
        coordTracker = null;
        navigator = null;
    }

    @SubscribeEvent
    public boolean onPlayerInteract(PlayerInteractEvent event){
        if(event.action == Action.RIGHT_CLICK_BLOCK && isListeningToCoordTrackerSetting) {
            isListeningToCoordTrackerSetting = false;
            EnumFacing dir = event.face;
            reset();
            ItemStack stack = event.entityPlayer.getCurrentArmor(3);
            if(stack != null) {
                NBTTagCompound tag = NBTUtil.getCompoundTag(stack, "CoordTracker");
                tag.setInteger("dimID", event.entity.worldObj.provider.getDimensionId());
                NBTUtil.setPos(tag, event.pos.offset(dir));
            }
            NetworkHandler.sendToServer(new PacketCoordTrackUpdate(event.entity.worldObj, event.pos.offset(dir)));
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public EnumNavigationResult navigateToSurface(EntityPlayer player){
        World worldObj = player.worldObj;
        BlockPos navigatingPos = worldObj.getHeight(new BlockPos(player));
        PathEntity path = PneumaticCraftUtils.getPathFinder().createEntityPathTo(player.worldObj, player, navigatingPos, SEARCH_RANGE);
        if(path != null) {
            for(int i = 0; i < path.getCurrentPathLength(); i++) {
                PathPoint pathPoint = path.getPathPointFromIndex(i);
                BlockPos pathPos = new BlockPos(pathPoint.xCoord, pathPoint.yCoord, pathPoint.zCoord);
                if(worldObj.canSeeSky(pathPos)) {
                    coordTracker = new RenderCoordWireframe(worldObj, pathPos);
                    navigator = new RenderNavigator(worldObj, pathPos);
                    return EnumNavigationResult.EASY_PATH;
                }
            }
        }
        path = getDronePath(player, navigatingPos);
        if(path != null) {
            for(int i = 0; i < path.getCurrentPathLength(); i++) {
                PathPoint pathPoint = path.getPathPointFromIndex(i);
                BlockPos pathPos = new BlockPos(pathPoint.xCoord, pathPoint.yCoord, pathPoint.zCoord);
                if(worldObj.canSeeSky(pathPos)) {
                    coordTracker = new RenderCoordWireframe(worldObj, pathPos);
                    navigator = new RenderNavigator(worldObj, pathPos);
                    return EnumNavigationResult.DRONE_PATH;
                }
            }
        }
        return EnumNavigationResult.NO_PATH;
    }

    public static PathEntity getDronePath(EntityPlayer player, BlockPos pos){
        World worldObj = player.worldObj;
        EntityDrone drone = new EntityDrone(worldObj);
        drone.setPosition(player.posX, player.posY - 2, player.posZ);
        return new EntityPathNavigateDrone(drone, worldObj).getPathToPos(pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IOptionPage getGuiOptionsPage(){
        return new GuiCoordinateTrackerOptions();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiAnimatedStat getAnimatedStat(){
        return null;
    }

}
