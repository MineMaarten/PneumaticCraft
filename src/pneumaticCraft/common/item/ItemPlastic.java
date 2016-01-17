package pneumaticCraft.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPlastic extends ItemPneumatic{

    public static final int SQUID_PLANT_DAMAGE = 0;
    public static final int FIRE_FLOWER_DAMAGE = 1;
    public static final int CREEPER_PLANT_DAMAGE = 2;
    public static final int SLIME_PLANT_DAMAGE = 3;
    public static final int RAIN_PLANT_DAMAGE = 4;
    public static final int ENDER_PLANT_DAMAGE = 5;
    public static final int LIGHTNING_PLANT_DAMAGE = 6;
    public static final int ADRENALINE_PLANT_DAMAGE = 7;
    public static final int BURST_PLANT_DAMAGE = 8;
    public static final int POTION_PLANT_DAMAGE = 9;
    public static final int REPULSION_PLANT_DAMAGE = 10;
    public static final int HELIUM_PLANT_DAMAGE = 11;
    public static final int CHOPPER_PLANT_DAMAGE = 12;
    public static final int MUSIC_PLANT_DAMAGE = 13;
    public static final int PROPULSION_PLANT_DAMAGE = 14;
    public static final int FLYING_FLOWER_DAMAGE = 15;

    public ItemPlastic(){
        setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs tab, List subItems){
        for(int i = 0; i < 16; i++) {
            if(i == ItemPlastic.ADRENALINE_PLANT_DAMAGE) continue;
            if(i == ItemPlastic.MUSIC_PLANT_DAMAGE) continue;
            subItems.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public int getMetadata(int meta){
        return meta;
    }

    @Override
    protected String getModelLocation(ItemStack stack){
        return getUnlocalizedName().substring(5);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack itemStack, int renderPass){
        int plasticColour = getColour(itemStack);
        if(plasticColour < 0) {
            plasticColour = Integer.parseInt("ffffff", 16);
        }
        return plasticColour;
    }

    private int getColour(ItemStack iStack){
        switch(iStack.getItemDamage()){
            case ItemPlastic.ADRENALINE_PLANT_DAMAGE:
                return Integer.parseInt("b1b1b1", 16);
            case ItemPlastic.BURST_PLANT_DAMAGE:
                return Integer.parseInt("848484", 16);
            case ItemPlastic.CHOPPER_PLANT_DAMAGE:
                return Integer.parseInt("82ace7", 16);
            case ItemPlastic.CREEPER_PLANT_DAMAGE:
                return Integer.parseInt("4a6b18", 16);
            case ItemPlastic.ENDER_PLANT_DAMAGE:
                return Integer.parseInt("8230b2", 16);
            case ItemPlastic.FIRE_FLOWER_DAMAGE:
                return Integer.parseInt("a72222", 16);
            case ItemPlastic.FLYING_FLOWER_DAMAGE:
                return Integer.parseInt("ffffff", 16);
            case ItemPlastic.HELIUM_PLANT_DAMAGE:
                return Integer.parseInt("e5e62a", 16);
            case ItemPlastic.LIGHTNING_PLANT_DAMAGE:
                return Integer.parseInt("1a6482", 16);
            case ItemPlastic.MUSIC_PLANT_DAMAGE:
                return Integer.parseInt("be5cb8", 16);
            case ItemPlastic.POTION_PLANT_DAMAGE:
                return Integer.parseInt("f7b4d6", 16);
            case ItemPlastic.PROPULSION_PLANT_DAMAGE:
                return Integer.parseInt("e69e34", 16);
            case ItemPlastic.RAIN_PLANT_DAMAGE:
                return Integer.parseInt("0a2b7a", 16);
            case ItemPlastic.REPULSION_PLANT_DAMAGE:
                return Integer.parseInt("83d41c", 16);
            case ItemPlastic.SLIME_PLANT_DAMAGE:
                return Integer.parseInt("795400", 16);
            case ItemPlastic.SQUID_PLANT_DAMAGE:
                return Integer.parseInt("000000", 16);
        }
        return -1;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack){
        return super.getUnlocalizedName(stack) + "." + EnumDyeColor.byDyeDamage(MathHelper.clamp_int(stack.getItemDamage(), 0, 15));
    }
}
