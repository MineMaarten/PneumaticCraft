package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import pneumaticCraft.common.tileentity.TileEntityKeroseneLamp;

public class ContainerKeroseneLamp extends ContainerPneumaticBase<TileEntityKeroseneLamp>{

    public ContainerKeroseneLamp(InventoryPlayer inventoryPlayer, TileEntityKeroseneLamp te){
        super(te);

        addSlotToContainer(new SlotFullFluidContainer(te, 0, 132, 22));
        addSlotToContainer(new SlotOutput(te, 1, 132, 55));

        addPlayerSlots(inventoryPlayer, 84);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player){
        return te.isGuiUseableByPlayer(player);
    }
}
