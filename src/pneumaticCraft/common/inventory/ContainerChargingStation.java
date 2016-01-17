package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import pneumaticCraft.common.tileentity.TileEntityChargingStation;

public class ContainerChargingStation extends ContainerPneumaticBase<TileEntityChargingStation>{

    public ContainerChargingStation(InventoryPlayer inventoryPlayer, TileEntityChargingStation te){
        super(te);

        // add the cannoned slot.
        addSlotToContainer(new Slot(te, 0, 91, 39){
            @Override
            public int getSlotStackLimit(){
                return 1;
            }
        });

        // add the upgrade slots
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 2; j++) {
                addSlotToContainer(new SlotInventoryLimiting(te, i * 2 + j + 1, 42 + j * 18, 29 + i * 18));
            }
        }

        addPlayerSlots(inventoryPlayer, 84);

        // Add the player's armor slots to the container.
        //TODO 1.8 test shift clicking
        for(int i = 0; i < 4; i++) {
            addSlotToContainer(new SlotPneumaticArmor(inventoryPlayer.player, inventoryPlayer, inventoryPlayer.getSizeInventory() - 1 - i, 9, 8 + i * 18, i));
        }
    }
}
