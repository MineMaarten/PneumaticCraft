package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import pneumaticCraft.common.tileentity.TileEntityPressureChamberInterface;

public class ContainerPressureChamberInterface extends ContainerPneumaticBase<TileEntityPressureChamberInterface>{

    public ContainerPressureChamberInterface(InventoryPlayer inventoryPlayer, TileEntityPressureChamberInterface te){
        super(te);

        // add the transfer slot.
        addSlotToContainer(new SlotUntouchable(te, 0, 66, 35));

        // add the upgrade slots
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 2; j++) {
                addSlotToContainer(new SlotInventoryLimiting(te, i * 2 + j + 1, 20 + j * 18, 26 + i * 18));
            }
        }

        addPlayerSlots(inventoryPlayer, 84);

        // add the export filter slots
        // Add them after the player slots so they won't be shift-clicked.
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                addSlotToContainer(new SlotPhantomUnstackable(te, i * 3 + j + 5, 115 + j * 18, 25 + i * 18){
                    @Override
                    public boolean isItemValid(ItemStack stack){
                        return true;
                    }
                });
            }
        }
    }

}
