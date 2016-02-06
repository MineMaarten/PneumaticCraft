package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import pneumaticCraft.common.tileentity.TileEntityChargingStation;

public class ContainerChargingStationItemInventory extends ContainerPneumaticBase<TileEntityChargingStation>{

    public InventoryPneumaticInventoryItem armor;

    public ContainerChargingStationItemInventory(InventoryPlayer inventoryPlayer, TileEntityChargingStation te){
        super(te);
        if(te.getStackInSlot(TileEntityChargingStation.CHARGE_INVENTORY_INDEX) == null) throw new IllegalArgumentException("instanciating ContainerPneumaticArmor with a charge item being null!");
        armor = te.getChargeableInventory();

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                addSlotToContainer(new SlotInventoryLimiting(armor, i * 3 + j, 31 + j * 18, 24 + i * 18));
            }
        }

        // addSlotToContainer(new Slot(teChargingStation, 0, 91, 39));

        addPlayerSlots(inventoryPlayer, 84);

        // Add the player's armor slots to the container.
        for(int i = 0; i < 4; i++) {
            addSlotToContainer(new SlotPneumaticArmor(inventoryPlayer.player, inventoryPlayer, inventoryPlayer.getSizeInventory() - 1 - i, 9, 8 + i * 18, i));
        }

        addSlotToContainer(new SlotUntouchable(te, 0, -50000, -50000)); //Allows the charging stack to sync.

    }
}
