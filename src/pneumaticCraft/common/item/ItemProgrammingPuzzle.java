package pneumaticCraft.common.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.PneumaticCraft;
import pneumaticCraft.common.progwidgets.IProgWidget;
import pneumaticCraft.common.progwidgets.WidgetRegistrator;
import pneumaticCraft.lib.Names;

public class ItemProgrammingPuzzle extends ItemPneumatic{

    public ItemProgrammingPuzzle(){
        hasSubtypes = true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(ItemStack stack, EntityPlayer player, List par3List, boolean par4){
        super.addInformation(stack, player, par3List, par4);
        par3List.add(new ItemStack(Itemss.plastic, 1, stack.getItemDamage()).getDisplayName());
    }

    @Override
    public String getUnlocalizedName(ItemStack stack){
        return super.getUnlocalizedName(stack) + "." + EnumDyeColor.byDyeDamage(MathHelper.clamp_int(stack.getItemDamage(), 0, 15));
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List){
        addItems(par3List);
    }

    public static void addItems(List<ItemStack> list){
        for(int i = 0; i < 16; i++) {
            for(IProgWidget widget : WidgetRegistrator.registeredWidgets) {
                if(widget.getCraftingColorIndex() == i) {
                    list.add(new ItemStack(Itemss.programmingPuzzle, 1, i));
                    break;
                }
            }
        }
    }

    @Override
    public void registerItemVariants(){
        ResourceLocation resLoc = new ResourceLocation(Names.MOD_ID, getUnlocalizedName().substring(5));
        ModelBakery.registerItemVariants(this, resLoc);
        for(int i = 0; i < 16; i++)
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, i, new ModelResourceLocation(resLoc, "inventory"));
    }

    public static IProgWidget getWidgetForPiece(ItemStack stack){
        List<IProgWidget> widgets = getWidgetsForColor(stack.getItemDamage());
        if(widgets.size() > 0) {
            World world = PneumaticCraft.proxy.getClientWorld();
            return widgets.get((int)(world.getTotalWorldTime() % (widgets.size() * 20) / 20));
        } else {
            return null;
        }
    }

    private static List<IProgWidget> getWidgetsForColor(int color){
        List<IProgWidget> widgets = new ArrayList<IProgWidget>();
        for(IProgWidget widget : WidgetRegistrator.registeredWidgets) {
            if(widget.getCraftingColorIndex() == color) {
                widgets.add(widget);
            }
        }
        return widgets;
    }

    public static ItemStack getStackForColor(int color){
        return new ItemStack(Itemss.programmingPuzzle, 1, color);
    }

    public static ItemStack getStackForWidgetKey(String widgetKey){
        /*for(IProgWidget widget : TileEntityProgrammer.registeredWidgets) {
            if(widget.getWidgetString().equals(widgetKey)) {
                return new ItemStack(Itemss.programmingPuzzle, 1, widget.getCraftingColorIndex());
            }
        }*/
        ItemStack stack = new ItemStack(Itemss.programmingPuzzle);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("type", widgetKey);
        stack.setTagCompound(tag);
        return stack;
        //    throw new IllegalArgumentException("No widget registered with the name " + widgetKey + "! This is not possible?!");
    }

    public static IProgWidget getWidgetForClass(Class<? extends IProgWidget> clazz){
        for(IProgWidget widget : WidgetRegistrator.registeredWidgets) {
            if(widget.getClass() == clazz) return widget;
        }
        throw new IllegalArgumentException("Widget " + clazz.getCanonicalName() + " isn't registered!");
    }

    public static IProgWidget getWidgetForName(String name){
        for(IProgWidget widget : WidgetRegistrator.registeredWidgets) {
            if(widget.getWidgetString().equals(name)) return widget;
        }
        throw new IllegalArgumentException("Widget " + name + " isn't registered!");
    }

}
