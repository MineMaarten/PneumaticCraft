package pneumaticCraft.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.api.block.IPneumaticWrenchable;
import pneumaticCraft.api.client.pneumaticHelmet.EntityTrackEvent;
import pneumaticCraft.api.client.pneumaticHelmet.InventoryTrackEvent;
import pneumaticCraft.api.drone.AmadronRetrievalEvent;
import pneumaticCraft.api.drone.DroneConstructingEvent;
import pneumaticCraft.api.drone.DroneSuicideEvent;
import pneumaticCraft.api.item.IItemRegistry.EnumUpgrade;
import pneumaticCraft.api.item.IPressurizable;
import pneumaticCraft.client.gui.widget.GuiKeybindCheckBox;
import pneumaticCraft.client.render.pneumaticArmor.EntityTrackUpgradeHandler;
import pneumaticCraft.client.render.pneumaticArmor.HUDHandler;
import pneumaticCraft.client.render.pneumaticArmor.hacking.HackableHandler;
import pneumaticCraft.client.render.pneumaticArmor.hacking.entity.HackableEnderman;
import pneumaticCraft.common.ai.IDroneBase;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.config.Config;
import pneumaticCraft.common.entity.EntityProgrammableController;
import pneumaticCraft.common.entity.living.EntityDrone;
import pneumaticCraft.common.fluid.Fluids;
import pneumaticCraft.common.item.ItemAmadronTablet;
import pneumaticCraft.common.item.ItemPneumaticArmor;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.network.NetworkHandler;
import pneumaticCraft.common.network.PacketPlaySound;
import pneumaticCraft.common.network.PacketSetMobTarget;
import pneumaticCraft.common.recipes.AmadronOffer;
import pneumaticCraft.common.recipes.AmadronOfferCustom;
import pneumaticCraft.common.recipes.AmadronOfferManager;
import pneumaticCraft.common.remote.GlobalVariableManager;
import pneumaticCraft.common.thirdparty.ModInteractionUtilImplementation;
import pneumaticCraft.common.tileentity.TileEntityProgrammer;
import pneumaticCraft.common.util.FluidUtils;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.lib.TileEntityConstants;

public class EventHandlerPneumaticCraft{

    private static ItemStack IRON_INGOT = new ItemStack(Items.iron_ingot);
    private static ItemStack IRON_BLOCK = new ItemStack(Blocks.iron_block);

    @SubscribeEvent
    public void handleIronExplosions(ExplosionEvent.Detonate event){
        Iterator<Entity> iterator = event.getAffectedEntities().iterator();
        while(iterator.hasNext()) {
            Entity entity = iterator.next();
            if(entity instanceof EntityItem) {
                ItemStack stack = ((EntityItem)entity).getEntityItem();
                if(stack != null && !entity.isDead && PneumaticCraftUtils.isSameOreDictStack(stack, IRON_INGOT) || PneumaticCraftUtils.isSameOreDictStack(stack, IRON_BLOCK)) {
                    Random rand = new Random();
                    if(stack.stackSize >= 3 || rand.nextDouble() >= Config.configCompressedIngotLossRate / 100D) {
                        Item newItem = PneumaticCraftUtils.isSameOreDictStack(stack, IRON_INGOT) ? Itemss.ingotIronCompressed : Item.getItemFromBlock(Blockss.compressedIron);
                        ItemStack newStack = new ItemStack(newItem, stack.stackSize, stack.getItemDamage());
                        if(stack.stackSize >= 3) {
                            newStack.stackSize = (int)(stack.stackSize * (rand.nextDouble() * Math.min(Config.configCompressedIngotLossRate * 0.02D, 0.2D) + (Math.max(0.9D, 1D - Config.configCompressedIngotLossRate * 0.01D) - Config.configCompressedIngotLossRate * 0.01D)));
                        }
                        ((EntityItem)entity).setEntityItemStack(newStack);
                        iterator.remove();
                        for(EntityPlayer player : event.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(event.explosion.getPosition().xCoord - 32, event.explosion.getPosition().yCoord - 32, event.explosion.getPosition().zCoord - 32, event.explosion.getPosition().xCoord + 32, event.explosion.getPosition().yCoord + 32, event.explosion.getPosition().zCoord + 32))) {
                            AchievementHandler.giveAchievement(player, newStack);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityConstruction(EntityConstructing event){
        HackableHandler.onEntityConstruction(event.entity);
        if(event.entity instanceof IDroneBase) {
            MinecraftForge.EVENT_BUS.post(new DroneConstructingEvent((IDroneBase)event.entity));
        }
    }

    @SubscribeEvent
    public void onPlayerPickup(EntityItemPickupEvent event){
        if(event.item != null && event.entityPlayer != null) AchievementHandler.giveAchievement(event.entityPlayer, event.item.getEntityItem());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEnderTeleport(EnderTeleportEvent event){
        if(!HackableEnderman.onEndermanTeleport(event.entity)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void FillBucket(FillBucketEvent event){
        MovingObjectPosition p = event.target;
        if(event.current == null || event.current.getItem() != Items.bucket || !FluidUtils.isSourceBlock(event.world, p.getBlockPos())) return;
        ItemStack result = attemptFill(event.world, event.target);
        if(result != null) {
            event.result = result;
            AchievementHandler.giveAchievement(event.entityPlayer, result);
            event.setResult(Result.ALLOW);
        }
    }

    private ItemStack attemptFill(World world, MovingObjectPosition p){
        Block id = world.getBlockState(p.getBlockPos()).getBlock();
        for(Map.Entry<Block, Item> entry : Fluids.fluidBlockToBucketMap.entrySet()) {
            if(id == entry.getKey()) {
                world.setBlockToAir(p.getBlockPos());
                return new ItemStack(entry.getValue());
            }
        }
        return null;
    }

    @SubscribeEvent
    public void onPlayerClick(PlayerInteractEvent event){
        if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) return;
        IBlockState interactedBlockState = event.world.getBlockState(event.pos);
        Block interactedBlock = interactedBlockState.getBlock();
        if(!event.entityPlayer.capabilities.isCreativeMode || !event.entityPlayer.canCommandSenderUseCommand(2, "securityStation")) {
            if(event.world != null && !event.world.isRemote) {
                if(interactedBlock != Blockss.securityStation || event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
                    ItemStack heldItem = event.entityPlayer.getCurrentEquippedItem();
                    boolean tryingToPlaceSecurityStation = heldItem != null && heldItem.getItem() instanceof ItemBlock && ((ItemBlock)heldItem.getItem()).getBlock() == Blockss.securityStation;
                    int blockingStations = PneumaticCraftUtils.getProtectingSecurityStations(event.entity.worldObj, event.pos, event.entityPlayer, true, tryingToPlaceSecurityStation);
                    if(blockingStations > 0) {
                        event.setCanceled(true);
                        event.entityPlayer.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocalFormatted(tryingToPlaceSecurityStation ? "message.securityStation.stationPlacementPrevented" : "message.securityStation.accessPrevented", blockingStations)));
                    }
                }
            }
        }

        /**
         * Due to some weird quirk that causes Block#onBlockActivated not getting called on the server when the player is sneaking, this is a workaround.
         */
        if(!event.isCanceled() && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && !event.world.isRemote) {
            if(event.entityPlayer.isSneaking() && (interactedBlock == Blockss.elevatorCaller || interactedBlock == Blockss.chargingStation)) {
                event.setCanceled(interactedBlock.onBlockActivated(event.world, event.pos, interactedBlockState, event.entityPlayer, event.face, 0, 0, 0));
            } else if(event.entityPlayer.getCurrentEquippedItem() != null && ModInteractionUtilImplementation.getInstance().isModdedWrench(event.entityPlayer.getCurrentEquippedItem().getItem())) {
                if(interactedBlock instanceof IPneumaticWrenchable) {
                    ((IPneumaticWrenchable)interactedBlock).rotateBlock(event.world, event.entityPlayer, event.pos, event.face);
                }
            }
        }

        if(!event.isCanceled() && interactedBlock == Blocks.cobblestone) {
            AchievementHandler.checkFor9x9(event.entityPlayer, event.pos);
        }
    }

    /**
     * Used by PneumaticHelmet
     * @param event
     */
    @SubscribeEvent
    public void onMobTargetSet(LivingSetAttackTargetEvent event){
        if(event.entity instanceof EntityCreature) {
            if(!event.entity.worldObj.isRemote) {
                NetworkHandler.sendToAllAround(new PacketSetMobTarget((EntityCreature)event.entity, event.target), new NetworkRegistry.TargetPoint(event.entity.worldObj.provider.getDimensionId(), event.entity.posX, event.entity.posY, event.entity.posZ, TileEntityConstants.PACKET_UPDATE_DISTANCE));
            } else {
                warnPlayerIfNecessary(event);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void warnPlayerIfNecessary(LivingSetAttackTargetEvent event){
        EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
        if(event.target == player && (event.entityLiving instanceof EntityGolem || event.entityLiving instanceof EntityMob)) {
            ItemStack helmetStack = player.getCurrentArmor(3);
            if(helmetStack != null && helmetStack.getItem() == Itemss.pneumaticHelmet && ((IPressurizable)helmetStack.getItem()).getPressure(helmetStack) > 0 && ItemPneumaticArmor.getUpgrades(EnumUpgrade.ENTITY_TRACKER, helmetStack) > 0 && GuiKeybindCheckBox.trackedCheckboxes.get("pneumaticHelmet.upgrade.coreComponents").checked && GuiKeybindCheckBox.trackedCheckboxes.get("pneumaticHelmet.upgrade." + EntityTrackUpgradeHandler.UPGRADE_NAME).checked) {
                HUDHandler.instance().getSpecificRenderer(EntityTrackUpgradeHandler.class).warnIfNecessary(event.entityLiving);
            }
        } else {
            HUDHandler.instance().getSpecificRenderer(EntityTrackUpgradeHandler.class).removeTargetingEntity(event.entityLiving);
        }
    }

    @SubscribeEvent
    public void quetziMoo(ServerChatEvent event){
        if(event.username.equals("Quetzz") && event.message.equals("m00")) {
            for(int i = 0; i < 4; i++)
                NetworkHandler.sendTo(new PacketPlaySound("mob.cow.say", event.player.posX, event.player.posY, event.player.posZ, 1, 1, true), event.player);
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event){
        if(!event.world.isRemote) {
            if(event.world.provider.getDimensionId() == 0) {
                GlobalVariableManager.overworld = event.world;
                event.world.loadItemData(GlobalVariableManager.class, GlobalVariableManager.DATA_KEY);
            }
        }
    }

    @SubscribeEvent
    public void onEntityTracking(EntityTrackEvent event){
        if(event.trackingEntity instanceof EntityProgrammableController) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onInventoryTracking(InventoryTrackEvent event){
        if(event.getTileEntity() instanceof TileEntityProgrammer) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onDroneSuicide(DroneSuicideEvent event){
        if(event.drone instanceof EntityDrone) {
            EntityDrone drone = (EntityDrone)event.drone;
            AmadronOffer offer = drone.getHandlingOffer();
            if(offer != null) {
                int times = drone.getOfferTimes();
                if(offer.getInput() instanceof ItemStack) {
                    int requiredCount = ((ItemStack)offer.getInput()).stackSize * times;
                    for(int i = 0; i < drone.getInv().getSizeInventory(); i++) {
                        if(drone.getInv().getStackInSlot(i) != null) {
                            requiredCount -= drone.getInv().getStackInSlot(i).stackSize;
                        }
                    }
                    if(requiredCount <= 0) {
                        for(int i = 0; i < drone.getInv().getSizeInventory(); i++) {
                            drone.getInv().setInventorySlotContents(i, null);
                        }
                        MinecraftForge.EVENT_BUS.post(new AmadronRetrievalEvent(event.drone));
                    }
                } else {
                    int requiredCount = ((FluidStack)offer.getInput()).amount * times;
                    if(drone.getTank().getFluidAmount() >= requiredCount) {
                        MinecraftForge.EVENT_BUS.post(new AmadronRetrievalEvent(event.drone));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onAmadronSuccess(AmadronRetrievalEvent event){
        EntityDrone drone = (EntityDrone)event.drone;
        AmadronOffer offer = drone.getHandlingOffer();

        boolean shouldDeliver = false;
        if(offer instanceof AmadronOfferCustom) {
            AmadronOffer realOffer = AmadronOfferManager.getInstance().get(offer);
            if(realOffer != null) {//If we find the non-inverted offer, that means the Drone just has completed trading with a different player.
                ((AmadronOfferCustom)realOffer).addPayment(drone.getOfferTimes());
                ((AmadronOfferCustom)realOffer).addStock(-drone.getOfferTimes());
                realOffer.onTrade(drone.getOfferTimes(), drone.getBuyingPlayer());
                shouldDeliver = true;
            }
            realOffer = AmadronOfferManager.getInstance().get(((AmadronOfferCustom)offer).copy().invert());
            if(realOffer != null) {//If we find the inverted offer, that means the Drone has just restocked.
                ((AmadronOfferCustom)realOffer).addStock(drone.getOfferTimes());
            }
        } else {
            shouldDeliver = true;
        }
        if(shouldDeliver) {
            ItemStack usedTablet = drone.getUsedTablet();
            if(offer.getOutput() instanceof ItemStack) {
                ItemStack offeringItems = (ItemStack)offer.getOutput();
                int producedItems = offeringItems.stackSize * drone.getOfferTimes();
                List<ItemStack> stacks = new ArrayList<ItemStack>();
                while(producedItems > 0) {
                    ItemStack stack = offeringItems.copy();
                    stack.stackSize = Math.min(producedItems, stack.getMaxStackSize());
                    stacks.add(stack);
                    producedItems -= stack.stackSize;
                }
                BlockPos pos = ItemAmadronTablet.getItemProvidingLocation(usedTablet);
                if(pos != null) {
                    World world = PneumaticCraftUtils.getWorldForDimension(ItemAmadronTablet.getItemProvidingDimension(usedTablet));
                    DroneRegistry.getInstance().deliverItemsAmazonStyle(world, pos, stacks.toArray(new ItemStack[stacks.size()]));
                }
            } else {
                FluidStack offeringFluid = ((FluidStack)offer.getOutput()).copy();
                offeringFluid.amount *= drone.getOfferTimes();
                BlockPos pos = ItemAmadronTablet.getLiquidProvidingLocation(usedTablet);
                if(pos != null) {
                    World world = PneumaticCraftUtils.getWorldForDimension(ItemAmadronTablet.getLiquidProvidingDimension(usedTablet));
                    DroneRegistry.getInstance().deliverFluidAmazonStyle(world, pos, offeringFluid);
                }
            }
        }
    }
}
