package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import pneumaticCraft.common.AchievementHandler;
import pneumaticCraft.common.fluid.Fluids;
import pneumaticCraft.common.tileentity.TileEntityRefinery;

public class ContainerRefinery extends ContainerPneumaticBase<TileEntityRefinery>{

    public ContainerRefinery(InventoryPlayer inventoryPlayer, TileEntityRefinery te){
        super(te);

        TileEntityRefinery refinery = te;
        refinery.onNeighborTileUpdate();
        while(refinery.getTileCache()[EnumFacing.UP.ordinal()].getTileEntity() instanceof TileEntityRefinery) {
            refinery = (TileEntityRefinery)refinery.getTileCache()[EnumFacing.UP.ordinal()].getTileEntity();
            addSyncedFields(refinery);
            refinery.onNeighborTileUpdate();
        }

        addPlayerSlots(inventoryPlayer, 84);

        if(te.getTankInfo(EnumFacing.UP)[0].fluid != null && te.getTankInfo(EnumFacing.UP)[0].fluid.getFluid() == Fluids.oil) {
            AchievementHandler.giveAchievement(inventoryPlayer.player, new ItemStack(Fluids.getBucket(Fluids.oil)));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player){
        return true;//te.isUseableByPlayer(player);
        //return te.isGuiUseableByPlayer(player);
    }

    /**
     * @param itemStack
     *            ItemStack to merge into inventory
     * @param start
     *            minimum slot to attempt fill
     * @param end
     *            maximum slot to attempt fill
     * @param backwards
     *            go backwards
     * @return true if stacks merged successfully public boolean
     *         mergeItemStack(itemStack, start, end, backwards)
     */

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2){

        ItemStack var3 = null;
        Slot var4 = inventorySlots.get(par2);

        if(var4 != null && var4.getHasStack()) {
            ItemStack var5 = var4.getStack();
            var3 = var5.copy();

            if(par2 < 27) {
                if(!mergeItemStack(var5, 27, 36, false)) return null;
                var4.onSlotChange(var5, var3);
            } else {
                if(!mergeItemStack(var5, 0, 27, false)) return null;
                var4.onSlotChange(var5, var3);
            }

            if(var5.stackSize == 0) {
                var4.putStack((ItemStack)null);
            } else {
                var4.onSlotChanged();
            }

            if(var5.stackSize == var3.stackSize) return null;

            var4.onPickupFromSlot(par1EntityPlayer, var5);
        }

        return var3;
    }
}
