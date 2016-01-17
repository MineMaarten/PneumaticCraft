package pneumaticCraft.common.item;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.api.item.IItemRegistry.EnumUpgrade;
import pneumaticCraft.api.item.IPressurizable;
import pneumaticCraft.api.item.IProgrammable;
import pneumaticCraft.api.item.IUpgradeAcceptor;
import pneumaticCraft.common.NBTUtil;
import pneumaticCraft.common.entity.living.EntityDrone;
import pneumaticCraft.common.progwidgets.IProgWidget;
import pneumaticCraft.common.tileentity.TileEntityProgrammer;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.lib.Log;
import pneumaticCraft.lib.PneumaticValues;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;

public class ItemDrone extends ItemPneumatic implements IPressurizable, IChargingStationGUIHolderItem, IProgrammable,
        IUpgradeAcceptor{

    public ItemDrone(){
        setMaxStackSize(1);
    }

    @Override
    public boolean onItemUse(ItemStack iStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float vecX, float vecY, float vecZ){
        if(!world.isRemote) {
            EntityDrone drone = new EntityDrone(world, player);

            BlockPos placePos = pos.offset(side);
            drone.setPosition(placePos.getX() + 0.5, placePos.getY() + 0.5, placePos.getZ() + 0.5);
            world.spawnEntityInWorld(drone);

            NBTTagCompound stackTag = iStack.getTagCompound();
            NBTTagCompound entityTag = new NBTTagCompound();
            drone.writeEntityToNBT(entityTag);
            if(stackTag != null) {
                entityTag.setTag("widgets", stackTag.getTagList("widgets", 10).copy());
                entityTag.setFloat("currentAir", stackTag.getFloat("currentAir"));
                entityTag.setInteger("color", stackTag.getInteger("color"));
                NBTTagCompound invTag = stackTag.getCompoundTag("UpgradeInventory");
                if(invTag != null) entityTag.setTag("Inventory", invTag.copy());
            }
            drone.readEntityFromNBT(entityTag);
            if(iStack.hasDisplayName()) drone.setCustomNameTag(iStack.getDisplayName());

            drone.naturallySpawned = false;
            //TODO 1.8 check if valid replacement drone.onSpawnWithEgg(null);
            drone.onInitialSpawn(world.getDifficultyForLocation(placePos), (IEntityLivingData)null);
            iStack.stackSize--;
        }
        return true;
    }

    public static void setProgWidgets(List<IProgWidget> widgets, ItemStack iStack){
        NBTUtil.initNBTTagCompound(iStack);
        TileEntityProgrammer.setWidgetsToNBT(widgets, iStack.getTagCompound());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs tab, List subItems){
        subItems.add(new ItemStack(this));
        ItemStack chargedStack = new ItemStack(this);
        addAir(chargedStack, (int)(PneumaticValues.DRONE_VOLUME * PneumaticValues.DRONE_MAX_PRESSURE));
        subItems.add(chargedStack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4){
        list.add("Pressure: " + PneumaticCraftUtils.roundNumberTo(getPressure(stack), 1) + " bar");
        super.addInformation(stack, player, list, par4);
    }

    @Override
    public float getPressure(ItemStack iStack){
        float volume = ItemPneumaticArmor.getUpgrades(EnumUpgrade.VOLUME, iStack) * PneumaticValues.VOLUME_VOLUME_UPGRADE + PneumaticValues.DRONE_VOLUME;
        float oldVolume = NBTUtil.getFloat(iStack, "volume");
        if(volume < oldVolume) {
            float currentAir = NBTUtil.getFloat(iStack, "currentAir");
            currentAir *= volume / oldVolume;
            NBTUtil.setFloat(iStack, "currentAir", currentAir);
        }
        NBTUtil.setFloat(iStack, "volume", volume);
        return NBTUtil.getFloat(iStack, "currentAir") / volume;
    }

    @Override
    public void addAir(ItemStack iStack, int amount){
        NBTUtil.setFloat(iStack, "currentAir", NBTUtil.getFloat(iStack, "currentAir") + amount);
    }

    @Override
    public float maxPressure(ItemStack iStack){
        return PneumaticValues.DRONE_MAX_PRESSURE;
    }

    @Override
    public EnumGuiId getGuiID(){
        return EnumGuiId.DRONE;
    }

    @Override
    public boolean canProgram(ItemStack stack){
        return true;
    }

    @Override
    public boolean usesPieces(ItemStack stack){
        return true;
    }

    @Override
    public boolean showProgramTooltip(){
        return true;
    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    @Override
    public void onUpdate(ItemStack iStack, World world, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_){
        if(!world.isRemote && NBTUtil.hasTag(iStack, "Inventory") && iStack.getTagCompound().getTag("Inventory") instanceof NBTTagCompound) {
            Log.info("Converting 'Inventory' tag to 'UpgradeInventory' in Pneumatic items");
            iStack.getTagCompound().setTag("UpgradeInventory", iStack.getTagCompound().getTag("Inventory"));
            iStack.getTagCompound().removeTag("Inventory");
        }
    }

    @Override
    public Set<Item> getApplicableUpgrades(){
        Set<Item> set = new HashSet<Item>();
        set.add(Itemss.upgrades.get(EnumUpgrade.VOLUME));
        set.add(Itemss.upgrades.get(EnumUpgrade.DISPENSER));
        set.add(Itemss.upgrades.get(EnumUpgrade.ITEM_LIFE));
        set.add(Itemss.upgrades.get(EnumUpgrade.SECURITY));
        set.add(Itemss.upgrades.get(EnumUpgrade.SPEED));
        set.add(Itemss.upgrades.get(EnumUpgrade.ENTITY_TRACKER));
        return set;
    }

    @Override
    public String getName(){
        return getUnlocalizedName() + ".name";
    }

}
