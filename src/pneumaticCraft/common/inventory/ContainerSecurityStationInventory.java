package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.tileentity.TileEntitySecurityStation;

public class ContainerSecurityStationInventory extends ContainerPneumaticBase<TileEntitySecurityStation>{

    public ContainerSecurityStationInventory(InventoryPlayer inventoryPlayer, TileEntitySecurityStation te){
        super(te);

        //add the network slots
        for(int i = 0; i < 7; i++) {
            for(int j = 0; j < 5; j++) {
                addSlotToContainer(new SlotItemSpecific(te, Itemss.networkComponent, j + i * 5, 17 + j * 18, 22 + i * 18));
            }
        }

        // add the upgrade slots
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 2; j++) {
                addSlotToContainer(new SlotInventoryLimiting(te, i * 2 + j + TileEntitySecurityStation.UPGRADE_SLOT_START, 128 + j * 18, 62 + i * 18));
            }
        }

        // Add the player's inventory slots to the container
        for(int inventoryRowIndex = 0; inventoryRowIndex < 3; ++inventoryRowIndex) {
            for(int inventoryColumnIndex = 0; inventoryColumnIndex < 9; ++inventoryColumnIndex) {
                addSlotToContainer(new Slot(inventoryPlayer, inventoryColumnIndex + inventoryRowIndex * 9 + 9, 8 + inventoryColumnIndex * 18, 157 + inventoryRowIndex * 18));
            }
        }

        // Add the player's action bar slots to the container
        for(int actionBarSlotIndex = 0; actionBarSlotIndex < 9; ++actionBarSlotIndex) {
            addSlotToContainer(new Slot(inventoryPlayer, actionBarSlotIndex, 8 + actionBarSlotIndex * 18, 215));
        }
    }

}
