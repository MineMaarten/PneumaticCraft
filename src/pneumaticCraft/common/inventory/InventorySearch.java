package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import pneumaticCraft.common.NBTUtil;
import pneumaticCraft.common.item.ItemPneumaticArmor;
import pneumaticCraft.common.network.NetworkHandler;
import pneumaticCraft.common.network.PacketUpdateSearchStack;

public class InventorySearch implements IInventory{

    private final ItemStack helmetStack;

    public InventorySearch(EntityPlayer player){
        helmetStack = player.getCurrentArmor(3);
    }

    @Override
    public int getSizeInventory(){
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int i){
        return ItemPneumaticArmor.getSearchedStack(helmetStack);
    }

    @Override
    public ItemStack decrStackSize(int i, int j){
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int i){
        return null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack){
        NBTTagCompound tag = NBTUtil.getCompoundTag(helmetStack, "SearchStack");
        tag.setInteger("itemID", itemstack != null ? Item.getIdFromItem(itemstack.getItem()) : -1);
        tag.setInteger("itemDamage", itemstack != null ? itemstack.getItemDamage() : -1);
        NetworkHandler.sendToServer(new PacketUpdateSearchStack(itemstack));
    }

    @Override
    public String getName(){
        return "Inventory Search";
    }

    @Override
    public int getInventoryStackLimit(){
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer){
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack){
        return false;
    }

    @Override
    public boolean hasCustomName(){
        return true;
    }

    @Override
    public void markDirty(){}

    @Override
    public void openInventory(EntityPlayer player){}

    @Override
    public void closeInventory(EntityPlayer player){}

    @Override
    public IChatComponent getDisplayName(){
        return null;
    }

    @Override
    public int getField(int id){
        return 0;
    }

    @Override
    public void setField(int id, int value){}

    @Override
    public int getFieldCount(){
        return 0;
    }

    @Override
    public void clear(){}

}
