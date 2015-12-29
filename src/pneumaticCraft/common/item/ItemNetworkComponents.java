package pneumaticCraft.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.api.item.IProgrammable;

public class ItemNetworkComponents extends ItemPneumatic implements IProgrammable{
    public static final int COMPONENT_AMOUNT = 6;

    public static final int DIAGNOSTIC_SUBROUTINE = 0;
    public static final int NETWORK_API = 1;
    public static final int NETWORK_DATA_STORAGE = 2;
    public static final int NETWORK_IO_PORT = 3;
    public static final int NETWORK_REGISTRY = 4;
    public static final int NETWORK_NODE = 5;

    public ItemNetworkComponents(){
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack is){
        return super.getUnlocalizedName(is) + is.getItemDamage();
    }

    @Override
    public int getMetadata(int meta){
        return meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs tab, List subItems){
        for(int i = 0; i < COMPONENT_AMOUNT; i++) {
            subItems.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List infoList, boolean par4){}

    @Override
    public boolean canProgram(ItemStack stack){
        return stack.getItemDamage() == NETWORK_API || stack.getItemDamage() == NETWORK_DATA_STORAGE;
    }

    @Override
    public boolean usesPieces(ItemStack stack){
        return stack.getItemDamage() == NETWORK_API;
    }

    @Override
    public boolean showProgramTooltip(){
        return true;
    }

}
