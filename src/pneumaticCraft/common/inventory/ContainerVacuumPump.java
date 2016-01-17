package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import pneumaticCraft.common.tileentity.TileEntityVacuumPump;

public class ContainerVacuumPump extends ContainerPneumaticBase<TileEntityVacuumPump>{

    public ContainerVacuumPump(InventoryPlayer inventoryPlayer, TileEntityVacuumPump te){
        super(te);

        // add the upgrade slots
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 2; j++) {
                addSlotToContainer(new SlotInventoryLimiting(te, i * 2 + j, 71 + j * 18, 29 + i * 18));
            }
        }

        addPlayerSlots(inventoryPlayer, 84);
    }
}
