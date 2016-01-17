package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import pneumaticCraft.common.tileentity.TileEntityPneumaticDoorBase;

public class ContainerPneumaticDoor extends ContainerPneumaticBase<TileEntityPneumaticDoorBase>{

    public ContainerPneumaticDoor(InventoryPlayer inventoryPlayer, TileEntityPneumaticDoorBase te){
        super(te);

        // add the upgrade slots
        addSlotToContainer(new SlotInventoryLimiting(te, 0, 23, 29));
        addSlotToContainer(new SlotInventoryLimiting(te, 1, 41, 29));
        addSlotToContainer(new SlotInventoryLimiting(te, 2, 23, 47));
        addSlotToContainer(new SlotInventoryLimiting(te, 3, 41, 47));

        // Add the camo slot.
        addSlotToContainer(new Slot(te, 4, 77, 36));

        addPlayerSlots(inventoryPlayer, 84);
    }
}
