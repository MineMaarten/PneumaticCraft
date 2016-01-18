package pneumaticCraft.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import pneumaticCraft.common.tileentity.TileEntityBase;

public class SlotUpgrade extends Slot{

    private final TileEntityBase te;

    public SlotUpgrade(TileEntityBase inventoryIn, int index, int xPosition, int yPosition){
        super((IInventory)inventoryIn, index, xPosition, yPosition);
        te = inventoryIn;
    }

    @Override
    public boolean isItemValid(ItemStack stack){
        return te.canInsertUpgrade(getSlotIndex(), stack);
    }
}
