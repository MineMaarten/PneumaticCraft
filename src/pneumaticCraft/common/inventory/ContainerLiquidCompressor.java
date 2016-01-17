package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import pneumaticCraft.common.tileentity.TileEntityLiquidCompressor;

public class ContainerLiquidCompressor extends ContainerPneumaticBase<TileEntityLiquidCompressor>{

    public ContainerLiquidCompressor(InventoryPlayer inventoryPlayer, TileEntityLiquidCompressor te){
        super(te);

        // add the upgrade slots
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 2; j++) {
                addSlotToContainer(new SlotInventoryLimiting(te, i * 2 + j, 11 + j * 18, 29 + i * 18));
            }
        }

        addSlotToContainer(new SlotFullFluidContainer(te, 4, getFluidContainerOffset(), 22));
        addSlotToContainer(new Slot(te, 5, getFluidContainerOffset(), 55));

        addPlayerSlots(inventoryPlayer, 84);
    }

    protected int getFluidContainerOffset(){
        return 62;
    }

}
