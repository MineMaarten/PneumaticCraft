package pneumaticCraft.common.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import pneumaticCraft.client.gui.GuiGPSTool;
import pneumaticCraft.common.NBTUtil;
import pneumaticCraft.common.remote.GlobalVariableManager;

public class ItemGPSTool extends ItemPneumatic{
    public ItemGPSTool(){
        setMaxStackSize(1);
    }

    @Override
    public boolean onItemUse(ItemStack IStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float par8, float par9, float par10){
        setGPSLocation(IStack, pos);
        if(!world.isRemote) player.addChatComponentMessage(new ChatComponentTranslation(EnumChatFormatting.GREEN + "[GPS Tool] Set Coordinates to " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "."));
        return true; // we don't want to use the item.

    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
        if(world.isRemote) {
            BlockPos pos = getGPSLocation(stack);
            FMLCommonHandler.instance().showGuiScreen(new GuiGPSTool(pos != null ? pos : new BlockPos(0, 0, 0), getVariable(stack)));
        }
        return stack;
    }

    // the information displayed as tooltip info. (saved coordinates in this
    // case)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List infoList, boolean par4){
        super.addInformation(stack, player, infoList, par4);
        NBTTagCompound compound = stack.getTagCompound();
        if(compound != null) {
            int x = compound.getInteger("x");
            int y = compound.getInteger("y");
            int z = compound.getInteger("z");
            if(x != 0 || y != 0 || z != 0) {
                infoList.add("\u00a72Set to " + x + ", " + y + ", " + z);
            }
            String varName = getVariable(stack);
            if(!varName.equals("")) {
                infoList.add(I18n.format("gui.tooltip.gpsTool.variable", varName));
            }
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean heldItem){
        String var = getVariable(stack);
        if(!var.equals("") && !world.isRemote) {
            BlockPos pos = GlobalVariableManager.getInstance().getPos(var);
            setGPSLocation(stack, pos);
        }
    }

    public static BlockPos getGPSLocation(ItemStack gpsTool){
        NBTTagCompound compound = gpsTool.getTagCompound();
        if(compound != null) {
            String var = getVariable(gpsTool);
            if(!var.equals("") && FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                BlockPos pos = GlobalVariableManager.getInstance().getPos(var);
                setGPSLocation(gpsTool, pos);
            }
            int x = compound.getInteger("x");
            int y = compound.getInteger("y");
            int z = compound.getInteger("z");
            if(x != 0 || y != 0 || z != 0) {
                return new BlockPos(x, y, z);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static void setGPSLocation(ItemStack gpsTool, BlockPos pos){
        NBTUtil.setPos(gpsTool, pos);
        String var = getVariable(gpsTool);
        if(!var.equals("")) GlobalVariableManager.getInstance().set(var, pos);
    }

    public static void setVariable(ItemStack gpsTool, String variable){
        NBTUtil.setString(gpsTool, "variable", variable);
    }

    public static String getVariable(ItemStack gpsTool){
        return gpsTool.hasTagCompound() ? gpsTool.getTagCompound().getString("variable") : "";
    }
}
