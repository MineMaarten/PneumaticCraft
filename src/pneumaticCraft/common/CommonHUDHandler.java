package pneumaticCraft.common;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.PneumaticCraft;
import pneumaticCraft.api.client.pneumaticHelmet.IHackableBlock;
import pneumaticCraft.api.client.pneumaticHelmet.IHackableEntity;
import pneumaticCraft.api.client.pneumaticHelmet.IUpgradeRenderHandler;
import pneumaticCraft.api.item.IItemRegistry.EnumUpgrade;
import pneumaticCraft.api.item.IPressurizable;
import pneumaticCraft.client.render.pneumaticArmor.UpgradeRenderHandlerList;
import pneumaticCraft.client.render.pneumaticArmor.hacking.HackableHandler;
import pneumaticCraft.common.item.ItemPneumaticArmor;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.network.NetworkHandler;
import pneumaticCraft.common.network.PacketHackingBlockFinish;
import pneumaticCraft.common.network.PacketHackingEntityFinish;
import pneumaticCraft.common.util.WorldAndCoord;

public class CommonHUDHandler{
    private final HashMap<String, CommonHUDHandler> playerHudHandlers = new HashMap<String, CommonHUDHandler>();
    public int rangeUpgradesInstalled;
    public int speedUpgradesInstalled;
    public boolean[] upgradeRenderersInserted = new boolean[UpgradeRenderHandlerList.instance().upgradeRenderers.size()];
    public boolean[] upgradeRenderersEnabled = new boolean[UpgradeRenderHandlerList.instance().upgradeRenderers.size()];
    public int ticksExisted;
    public float helmetPressure;

    private int hackTime;
    private WorldAndCoord hackedBlock;
    private Entity hackedEntity;

    public static CommonHUDHandler getHandlerForPlayer(EntityPlayer player){
        CommonHUDHandler handler = PneumaticCraft.proxy.getCommonHudHandler().playerHudHandlers.get(player.getName());
        if(handler != null) return handler;
        PneumaticCraft.proxy.getCommonHudHandler().playerHudHandlers.put(player.getName(), new CommonHUDHandler());
        return getHandlerForPlayer(player);
    }

    @SideOnly(Side.CLIENT)
    public static CommonHUDHandler getHandlerForPlayer(){
        return getHandlerForPlayer(FMLClientHandler.instance().getClient().thePlayer);
    }

    @SubscribeEvent
    public void tickEnd(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.END) {
            EntityPlayer player = event.player;
            if(this == PneumaticCraft.proxy.getCommonHudHandler()) {
                getHandlerForPlayer(player).tickEnd(event);
            } else {
                ItemStack helmetStack = player.getCurrentArmor(3);
                if(helmetStack != null && helmetStack.getItem() == Itemss.pneumaticHelmet) {
                    helmetPressure = ((IPressurizable)helmetStack.getItem()).getPressure(helmetStack);
                    if(ticksExisted == 0) {
                        checkHelmetInventory(helmetStack);
                    }
                    ticksExisted++;
                    if(!player.worldObj.isRemote) {
                        if(ticksExisted > getStartupTime() && !player.capabilities.isCreativeMode) {
                            ((IPressurizable)helmetStack.getItem()).addAir(helmetStack, (int)-UpgradeRenderHandlerList.instance().getAirUsage(player, false));
                        }
                    }

                } else {
                    ticksExisted = 0;
                }
                if(!player.worldObj.isRemote) handleHacking(player);
            }
        }
    }

    private void handleHacking(EntityPlayer player){
        if(hackedBlock != null) {
            IHackableBlock hackableBlock = HackableHandler.getHackableForCoord(hackedBlock, player);
            if(hackableBlock != null) {
                if(++hackTime >= hackableBlock.getHackTime(hackedBlock.world, hackedBlock.pos, player)) {
                    hackableBlock.onHackFinished(player.worldObj, hackedBlock.pos, player);
                    PneumaticCraft.proxy.getHackTickHandler().trackBlock(hackedBlock, hackableBlock);
                    NetworkHandler.sendToAllAround(new PacketHackingBlockFinish(hackedBlock), player.worldObj);
                    setHackedBlock(null);
                }
            } else {
                setHackedBlock(null);
            }
        } else if(hackedEntity != null) {
            IHackableEntity hackableEntity = HackableHandler.getHackableForEntity(hackedEntity, player);
            if(hackableEntity != null) {
                if(++hackTime >= hackableEntity.getHackTime(hackedEntity, player)) {
                    hackableEntity.onHackFinished(hackedEntity, player);
                    PneumaticCraft.proxy.getHackTickHandler().trackEntity(hackedEntity, hackableEntity);
                    NetworkHandler.sendToAllAround(new PacketHackingEntityFinish(hackedEntity), new NetworkRegistry.TargetPoint(hackedEntity.worldObj.provider.getDimensionId(), hackedEntity.posX, hackedEntity.posY, hackedEntity.posZ, 64));
                    setHackedEntity(null);
                }
            } else {
                setHackedEntity(null);
            }
        }
    }

    public void checkHelmetInventory(ItemStack helmetStack){
        ItemStack[] helmetStacks = ItemPneumaticArmor.getUpgradeStacks(helmetStack);
        rangeUpgradesInstalled = ItemPneumaticArmor.getUpgrades(EnumUpgrade.RANGE, helmetStack);
        speedUpgradesInstalled = ItemPneumaticArmor.getUpgrades(EnumUpgrade.SPEED, helmetStack);
        upgradeRenderersInserted = new boolean[UpgradeRenderHandlerList.instance().upgradeRenderers.size()];
        for(int i = 0; i < UpgradeRenderHandlerList.instance().upgradeRenderers.size(); i++) {
            upgradeRenderersInserted[i] = isModuleEnabled(helmetStacks, UpgradeRenderHandlerList.instance().upgradeRenderers.get(i));
        }
    }

    private boolean isModuleEnabled(ItemStack[] helmetStacks, IUpgradeRenderHandler handler){
        for(Item requiredUpgrade : handler.getRequiredUpgrades()) {
            boolean found = false;
            for(ItemStack stack : helmetStacks) {
                if(stack != null && stack.getItem() == requiredUpgrade) {
                    found = true;
                    break;
                }
            }
            if(!found) return false;
        }
        return true;
    }

    public int getSpeedFromUpgrades(){
        return 1 + speedUpgradesInstalled;
    }

    public int getStartupTime(){
        return 200 / getSpeedFromUpgrades();
    }

    public void setHackedBlock(WorldAndCoord blockPos){
        hackedBlock = blockPos;
        hackedEntity = null;
        hackTime = 0;
    }

    public void setHackedEntity(Entity entity){
        hackedEntity = entity;
        hackedBlock = null;
        hackTime = 0;
    }
}
