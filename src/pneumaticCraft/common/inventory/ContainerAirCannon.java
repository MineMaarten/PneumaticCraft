package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.tileentity.TileEntityAirCannon;

public class ContainerAirCannon extends ContainerPneumaticBase<TileEntityAirCannon>{

    public ContainerAirCannon(InventoryPlayer inventoryPlayer, TileEntityAirCannon te){
        super(te);

        // add the upgrade slots
        addSlotToContainer(new SlotInventoryLimiting(te, 2, 8, 29));
        addSlotToContainer(new SlotInventoryLimiting(te, 3, 26, 29));
        addSlotToContainer(new SlotInventoryLimiting(te, 4, 8, 47));
        addSlotToContainer(new SlotInventoryLimiting(te, 5, 26, 47));

        // add the gps slot
        addSlotToContainer(new SlotItemSpecific(te, Itemss.GPSTool, 1, 51, 29));

        // add the cannoned slot.
        addSlotToContainer(new Slot(te, 0, 79, 40));

        addPlayerSlots(inventoryPlayer, 84);

    }

}
