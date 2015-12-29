package pneumaticCraft.common.item;

import java.util.List;
import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.common.fluid.Fluids;
import pneumaticCraft.lib.TileEntityConstants;

public class ItemEmptyPCB extends ItemNonDespawning{
    private static Random rand = new Random();

    public ItemEmptyPCB(){
        setMaxStackSize(1);
        setMaxDamage(100);
        setNoRepair();
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List){
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, getMaxDamage()));
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List infoList, boolean par4){
        super.addInformation(stack, player, infoList, par4);
        if(stack.getItemDamage() < 100) {
            infoList.add("Etch success chance: " + (100 - stack.getItemDamage()) + "%");
        } else {
            infoList.add("Put in a UV Light Box to progress...");
        }
        if(stack.hasTagCompound()) {
            infoList.add("Etching progress: " + stack.getTagCompound().getInteger("etchProgress") + "%");
        } else if(stack.getItemDamage() < 100) {
            infoList.add("Throw in Etching Acid to develop...");
        }
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem){
        super.onEntityItemUpdate(entityItem);
        ItemStack stack = entityItem.getEntityItem();
        if(Fluids.areFluidsEqual(FluidRegistry.lookupFluidForBlock(entityItem.worldObj.getBlockState(new BlockPos(entityItem)).getBlock()), Fluids.etchingAcid)) {
            if(!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            int etchProgress = stack.getTagCompound().getInteger("etchProgress");
            if(etchProgress < 100) {
                if(entityItem.ticksExisted % (TileEntityConstants.PCB_ETCH_TIME / 5) == 0) stack.getTagCompound().setInteger("etchProgress", etchProgress + 1);
            } else {
                entityItem.setEntityItemStack(new ItemStack(rand.nextInt(100) >= stack.getItemDamage() ? Itemss.unassembledPCB : Itemss.failedPCB));
            }
        }
        return false;
    }
}
