package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import pneumaticCraft.common.tileentity.TileEntityOmnidirectionalHopper;

public class ContainerOmnidirectionalHopper extends ContainerPneumaticBase<TileEntityOmnidirectionalHopper>{

    public ContainerOmnidirectionalHopper(InventoryPlayer inventoryPlayer, TileEntityOmnidirectionalHopper te){
        super(te);

        // add the upgrade slots
        addSlotToContainer(new SlotInventoryLimiting(te, 5, 23, 29));
        addSlotToContainer(new SlotInventoryLimiting(te, 6, 41, 29));
        addSlotToContainer(new SlotInventoryLimiting(te, 7, 23, 47));
        addSlotToContainer(new SlotInventoryLimiting(te, 8, 41, 47));

        // Add the hopper slots. TODO 1.8 test slot order shift clicking
        for(int i = 0; i < 5; i++)
            addSlotToContainer(new Slot(te, i, 68 + i * 18, 36));

        addPlayerSlots(inventoryPlayer, 84);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player){
        return te.isUseableByPlayer(player);
    }
}
