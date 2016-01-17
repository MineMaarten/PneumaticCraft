package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import pneumaticCraft.common.tileentity.TileEntityProgrammableController;

public class ContainerProgrammableController extends ContainerPneumaticBase<TileEntityProgrammableController>{

    public ContainerProgrammableController(InventoryPlayer inventoryPlayer, final TileEntityProgrammableController te){
        super(te);

        addSlotToContainer(new SlotInventoryLimiting(te, 0, 71, 36));

        // add the upgrade slots
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 2; j++) {
                addSlotToContainer(new SlotInventoryLimiting(te, i * 2 + j + 1, 21 + j * 18, 29 + i * 18));
            }
        }

        addPlayerSlots(inventoryPlayer, 84);

    }

}
