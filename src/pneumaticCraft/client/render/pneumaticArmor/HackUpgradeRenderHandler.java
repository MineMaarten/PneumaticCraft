package pneumaticCraft.client.render.pneumaticArmor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import pneumaticCraft.api.client.IGuiAnimatedStat;
import pneumaticCraft.api.client.pneumaticHelmet.IOptionPage;
import pneumaticCraft.api.client.pneumaticHelmet.IUpgradeRenderHandler;
import pneumaticCraft.api.item.IItemRegistry.EnumUpgrade;
import pneumaticCraft.common.item.ItemPneumaticArmor;
import pneumaticCraft.common.item.Itemss;

public class HackUpgradeRenderHandler implements IUpgradeRenderHandler{

    @Override
    public String getUpgradeName(){
        return "hackingUpgrade";
    }

    @Override
    public void initConfig(Configuration config){

    }

    @Override
    public void saveToConfig(){

    }

    @Override
    public void update(EntityPlayer player, int rangeUpgrades){

    }

    @Override
    public void render3D(float partialTicks){

    }

    @Override
    public void render2D(float partialTicks, boolean helmetEnabled){

    }

    @Override
    public IGuiAnimatedStat getAnimatedStat(){
        return null;
    }

    @Override
    public Item[] getRequiredUpgrades(){
        return new Item[]{Itemss.upgrades.get(EnumUpgrade.SECURITY)};
    }

    private static boolean enabledForStacks(ItemStack[] upgradeStacks){
        for(ItemStack stack : upgradeStacks) {
            if(stack != null && stack.getItem() == Itemss.upgrades.get(EnumUpgrade.SECURITY)) return true;
        }
        return false;
    }

    public static boolean enabledForPlayer(EntityPlayer player){
        ItemStack helmet = player.getCurrentArmor(3);
        if(helmet != null) {
            return enabledForStacks(ItemPneumaticArmor.getUpgradeStacks(helmet));
        } else {
            return false;
        }
    }

    @Override
    public float getEnergyUsage(int rangeUpgrades, EntityPlayer player){
        return 0;
    }

    @Override
    public void reset(){

    }

    @Override
    public IOptionPage getGuiOptionsPage(){
        return null;
    }

}
