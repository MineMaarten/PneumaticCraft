package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import pneumaticCraft.common.tileentity.TileEntitySentryTurret;

public class ContainerSentryTurret extends ContainerPneumaticBase<TileEntitySentryTurret>{

    public ContainerSentryTurret(InventoryPlayer inventoryPlayer, TileEntitySentryTurret te){
        super(te);

        // Add the hopper slots.
        for(int i = 0; i < 4; i++)
            addSlotToContainer(new SlotInventoryLimiting(te, i + 4, 80 + i * 18, 29));

        // add the upgrade slots
        addSlotToContainer(new SlotInventoryLimiting(te, 0, 23, 29));
        addSlotToContainer(new SlotInventoryLimiting(te, 1, 41, 29));
        addSlotToContainer(new SlotInventoryLimiting(te, 2, 23, 47));
        addSlotToContainer(new SlotInventoryLimiting(te, 3, 41, 47));

        addPlayerSlots(inventoryPlayer, 84);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player){
        return te.isUseableByPlayer(player);
    }
}
