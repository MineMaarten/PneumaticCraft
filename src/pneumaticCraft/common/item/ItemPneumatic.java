package pneumaticCraft.common.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.PneumaticCraft;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.lib.ModIds;

public class ItemPneumatic extends Item{
    public ItemPneumatic(){
        setCreativeTab(PneumaticCraft.tabPneumaticCraft);
    }

    public ItemPneumatic(String unlocalizedName){
        this();
        if(unlocalizedName == null) throw new IllegalStateException("Item " + this + " has no unlocalized name!");
        setUnlocalizedName(unlocalizedName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List curInfo, boolean extraInfo){
        super.addInformation(stack, player, curInfo, extraInfo);
        curInfo.addAll(PneumaticCraftUtils.convertStringIntoList(EnumChatFormatting.RED + "PneumaticCraft is highly unstable at this point! The item/block you're looking at probably does not have a texture. It is recommended only to use the mod for worldgen purposes while it is being stabilized for MC1.8.8.", 40));
        addTooltip(stack, player, curInfo);
    }

    public static void addTooltip(ItemStack stack, EntityPlayer player, List curInfo){
        String info = "gui.tooltip." + stack.getItem().getUnlocalizedName();
        String translatedInfo = I18n.format(info);
        if(!translatedInfo.equals(info)) {
            if(PneumaticCraft.proxy.isSneakingInGui()) {
                translatedInfo = EnumChatFormatting.AQUA + translatedInfo;
                if(!Loader.isModLoaded(ModIds.IGWMOD)) translatedInfo += " \\n \\n" + I18n.format("gui.tab.info.assistIGW");
                curInfo.addAll(PneumaticCraftUtils.convertStringIntoList(translatedInfo, 60));
            } else {
                curInfo.add(EnumChatFormatting.AQUA + I18n.format("gui.tooltip.sneakForInfo"));
            }
        }
    }
}
