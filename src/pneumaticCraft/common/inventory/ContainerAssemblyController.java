package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.tileentity.TileEntityAssemblyController;

public class ContainerAssemblyController extends ContainerPneumaticBase<TileEntityAssemblyController>{

    public ContainerAssemblyController(InventoryPlayer inventoryPlayer, TileEntityAssemblyController te){
        super(te);
        // Add the burn slot.
        addSlotToContainer(new SlotItemSpecific(te, Itemss.assemblyProgram, 0, 74, 38));

        // add the upgrade slots
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 2; j++) {
                addSlotToContainer(new SlotInventoryLimiting(te, i * 2 + j + 1, 13 + j * 18, 31 + i * 18));
            }
        }

        addPlayerSlots(inventoryPlayer, 84);
    }

}
