package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import pneumaticCraft.common.tileentity.TileEntityLiquidHopper;

public class ContainerLiquidHopper extends ContainerPneumaticBase<TileEntityLiquidHopper>{

    public ContainerLiquidHopper(InventoryPlayer inventoryPlayer, TileEntityLiquidHopper te){
        super(te);

        addSlotToContainer(new SlotInventoryLimiting(te, 0, 48, 29));
        addSlotToContainer(new SlotInventoryLimiting(te, 1, 66, 29));
        addSlotToContainer(new SlotInventoryLimiting(te, 2, 48, 47));
        addSlotToContainer(new SlotInventoryLimiting(te, 3, 66, 47));

        addPlayerSlots(inventoryPlayer, 84);

    }

    @Override
    public boolean canInteractWith(EntityPlayer player){
        return te.isUseableByPlayer(player);
        //return te.isGuiUseableByPlayer(player);
    }

}
