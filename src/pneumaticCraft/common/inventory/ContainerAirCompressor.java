package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import pneumaticCraft.common.tileentity.TileEntityAirCompressor;

public class ContainerAirCompressor extends ContainerPneumaticBase<TileEntityAirCompressor>{

    public ContainerAirCompressor(InventoryPlayer inventoryPlayer, TileEntityAirCompressor te){
        super(te);
        // Add the burn slot.
        addSlotToContainer(new SlotInventoryLimiting(te, 0, getFuelSlotXOffset(), 54));

        // add the upgrade slots
        addSlotToContainer(new SlotInventoryLimiting(te, 1, 23, 29));
        addSlotToContainer(new SlotInventoryLimiting(te, 2, 41, 29));
        addSlotToContainer(new SlotInventoryLimiting(te, 3, 23, 47));
        addSlotToContainer(new SlotInventoryLimiting(te, 4, 41, 47));

        addPlayerSlots(inventoryPlayer, 84);
    }

    protected int getFuelSlotXOffset(){
        return 80;
    }

}
