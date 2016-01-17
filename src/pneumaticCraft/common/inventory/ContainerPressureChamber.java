package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import pneumaticCraft.common.tileentity.TileEntityPressureChamberValve;

public class ContainerPressureChamber extends ContainerPneumaticBase<TileEntityPressureChamberValve>{

    public ContainerPressureChamber(InventoryPlayer inventoryPlayer, TileEntityPressureChamberValve te){
        super(te);

        // add the upgrade slots
        addSlotToContainer(new SlotInventoryLimiting(te, 0, 48, 29));
        addSlotToContainer(new SlotInventoryLimiting(te, 1, 66, 29));
        addSlotToContainer(new SlotInventoryLimiting(te, 2, 48, 47));
        addSlotToContainer(new SlotInventoryLimiting(te, 3, 66, 47));

        addPlayerSlots(inventoryPlayer, 84);
    }

}
