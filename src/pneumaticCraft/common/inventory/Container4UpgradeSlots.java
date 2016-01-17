package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import pneumaticCraft.common.tileentity.TileEntityBase;

public class Container4UpgradeSlots<Tile extends TileEntityBase> extends ContainerPneumaticBase<Tile>{

    public Container4UpgradeSlots(InventoryPlayer inventoryPlayer, Tile te){
        super(te);

        // add the upgrade slots
        addSlotToContainer(new SlotInventoryLimiting((IInventory)te, 0, 48, 29));
        addSlotToContainer(new SlotInventoryLimiting((IInventory)te, 1, 66, 29));
        addSlotToContainer(new SlotInventoryLimiting((IInventory)te, 2, 48, 47));
        addSlotToContainer(new SlotInventoryLimiting((IInventory)te, 3, 66, 47));

        addPlayerSlots(inventoryPlayer, 84);
    }
}
