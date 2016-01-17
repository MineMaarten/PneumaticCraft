package pneumaticCraft.common.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import pneumaticCraft.common.network.GuiSynced;
import pneumaticCraft.common.network.NetworkHandler;
import pneumaticCraft.common.network.NetworkUtils;
import pneumaticCraft.common.network.PacketUpdateGui;
import pneumaticCraft.common.tileentity.IGUIButtonSensitive;
import pneumaticCraft.common.tileentity.TileEntityBase;

public class ContainerPneumaticBase<Tile extends TileEntityBase> extends Container implements IGUIButtonSensitive{

    public Tile te;
    private final List<SyncedField> syncedFields = new ArrayList<SyncedField>();
    private boolean firstTick = true;
    private int playerSlotsStart;

    public ContainerPneumaticBase(Tile te){
        this.te = te;
        if(te != null) addSyncedFields(te);
    }

    protected void addSyncedField(SyncedField field){
        syncedFields.add(field);
        field.setLazy(false);
    }

    protected void addSyncedFields(Object annotatedObject){
        List<SyncedField> fields = NetworkUtils.getSyncedFields(annotatedObject, GuiSynced.class);
        for(SyncedField field : fields)
            addSyncedField(field);
    }

    public void updateField(int index, Object value){
        syncedFields.get(index).setValue(value);
        if(te != null) te.onGuiUpdate();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player){
        return te.isGuiUseableByPlayer(player);
    }

    @Override
    public void detectAndSendChanges(){
        super.detectAndSendChanges();
        for(int i = 0; i < syncedFields.size(); i++) {
            if(syncedFields.get(i).update() || firstTick) {
                sendToCrafters(new PacketUpdateGui(i, syncedFields.get(i)));
            }
        }
        firstTick = false;
    }

    protected void sendToCrafters(IMessage message){
        for(ICrafting crafter : crafters) {
            if(crafter instanceof EntityPlayerMP) {
                NetworkHandler.sendTo(message, (EntityPlayerMP)crafter);
            }
        }
    }

    protected void addPlayerSlots(InventoryPlayer inventoryPlayer, int yOffset){
        playerSlotsStart = inventorySlots.size();

        // Add the player's inventory slots to the container
        for(int inventoryRowIndex = 0; inventoryRowIndex < 3; ++inventoryRowIndex) {
            for(int inventoryColumnIndex = 0; inventoryColumnIndex < 9; ++inventoryColumnIndex) {
                addSlotToContainer(new Slot(inventoryPlayer, inventoryColumnIndex + inventoryRowIndex * 9 + 9, 8 + inventoryColumnIndex * 18, yOffset + inventoryRowIndex * 18));
            }
        }

        // Add the player's action bar slots to the container
        for(int actionBarSlotIndex = 0; actionBarSlotIndex < 9; ++actionBarSlotIndex) {
            addSlotToContainer(new Slot(inventoryPlayer, actionBarSlotIndex, 8 + actionBarSlotIndex * 18, yOffset + 58));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2){

        ItemStack var3 = null;
        Slot var4 = inventorySlots.get(par2);

        if(var4 != null && var4.getHasStack()) {
            ItemStack var5 = var4.getStack();
            var3 = var5.copy();

            if(par2 < playerSlotsStart) {
                if(!mergeItemStack(var5, playerSlotsStart, playerSlotsStart + 36, false)) return null;

                var4.onSlotChange(var5, var3);
            } else {
                /* int validStartSlot = -1; Unncessary, as Forge now adds respect to slot.isItemValid to mergeItemStack
                 for(int i = 0; i < playerSlotsStart; i++) {
                     boolean slotValid = inventorySlots.get(i).isItemValid(var5);
                     if(slotValid && validStartSlot == -1) {
                         validStartSlot = i;
                     } else if(!slotValid && validStartSlot >= 0) {
                         if(!mergeItemStack(var5, validStartSlot, i, false)) return null;
                         validStartSlot = -1;
                     }
                 }
                 if(validStartSlot >= 0) {
                     if(!mergeItemStack(var5, validStartSlot, playerSlotsStart, false)) return null;
                 }*/
                if(!mergeItemStack(var5, 0, playerSlotsStart, false)) return null;
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

    /**
     * Source: Buildcraft
     */
    @Override
    public ItemStack slotClick(int slotNum, int modifier, int mouseButton, EntityPlayer player){
        Slot slot = slotNum < 0 ? null : (Slot)inventorySlots.get(slotNum);
        if(slot instanceof IPhantomSlot) {
            return slotClickPhantom(slot, modifier, mouseButton, player);
        }
        return super.slotClick(slotNum, modifier, mouseButton, player);
    }

    private ItemStack slotClickPhantom(Slot slot, int mouseButton, int modifier, EntityPlayer player){
        ItemStack stack = null;

        if(mouseButton == 2) {
            if(((IPhantomSlot)slot).canAdjust()) {
                slot.putStack(null);
            }
        } else if(mouseButton == 0 || mouseButton == 1) {
            InventoryPlayer playerInv = player.inventory;
            slot.onSlotChanged();
            ItemStack stackSlot = slot.getStack();
            ItemStack stackHeld = playerInv.getItemStack();

            if(stackSlot != null) {
                stack = stackSlot.copy();
            }

            if(stackSlot == null) {
                if(stackHeld != null && slot.isItemValid(stackHeld)) {
                    fillPhantomSlot(slot, stackHeld, mouseButton, modifier);
                }
            } else if(stackHeld == null) {
                adjustPhantomSlot(slot, mouseButton, modifier);
                slot.onPickupFromSlot(player, playerInv.getItemStack());
            } else if(slot.isItemValid(stackHeld)) {
                if(canStacksMerge(stackSlot, stackHeld)) {
                    adjustPhantomSlot(slot, mouseButton, modifier);
                } else {
                    fillPhantomSlot(slot, stackHeld, mouseButton, modifier);
                }
            }
        }
        return stack;
    }

    public boolean canStacksMerge(ItemStack stack1, ItemStack stack2){
        if(stack1 == null || stack2 == null) return false;
        if(!stack1.isItemEqual(stack2)) return false;
        if(!ItemStack.areItemStackTagsEqual(stack1, stack2)) return false;
        return true;

    }

    protected void adjustPhantomSlot(Slot slot, int mouseButton, int modifier){
        if(!((IPhantomSlot)slot).canAdjust()) {
            return;
        }
        ItemStack stackSlot = slot.getStack();
        int stackSize;
        if(modifier == 1) {
            stackSize = mouseButton == 0 ? (stackSlot.stackSize + 1) / 2 : stackSlot.stackSize * 2;
        } else {
            stackSize = mouseButton == 0 ? stackSlot.stackSize - 1 : stackSlot.stackSize + 1;
        }

        if(stackSize > slot.getSlotStackLimit()) {
            stackSize = slot.getSlotStackLimit();
        }

        stackSlot.stackSize = stackSize;

        if(stackSlot.stackSize <= 0) {
            slot.putStack((ItemStack)null);
        }
    }

    protected void fillPhantomSlot(Slot slot, ItemStack stackHeld, int mouseButton, int modifier){
        if(!((IPhantomSlot)slot).canAdjust()) {
            return;
        }
        int stackSize = mouseButton == 0 ? stackHeld.stackSize : 1;
        if(stackSize > slot.getSlotStackLimit()) {
            stackSize = slot.getSlotStackLimit();
        }
        ItemStack phantomStack = stackHeld.copy();
        phantomStack.stackSize = stackSize;

        slot.putStack(phantomStack);
    }

    @Override
    public void handleGUIButtonPress(int guiID, EntityPlayer player){
        if(te instanceof IGUIButtonSensitive) {
            ((IGUIButtonSensitive)te).handleGUIButtonPress(guiID, player);
        }
    }
}
