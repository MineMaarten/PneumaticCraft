package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import pneumaticCraft.common.tileentity.TileEntityThermopneumaticProcessingPlant;

public class ContainerThermopneumaticProcessingPlant extends
        ContainerPneumaticBase<TileEntityThermopneumaticProcessingPlant>{

    public ContainerThermopneumaticProcessingPlant(InventoryPlayer inventoryPlayer,
            TileEntityThermopneumaticProcessingPlant te){
        super(te);

        for(int i = 0; i < 4; i++)
            addSlotToContainer(new SlotInventoryLimiting(te, i, 80 + 18 * i, 93));

        addSlotToContainer(new Slot(te, 4, 46, 14));

        addPlayerSlots(inventoryPlayer, 115);

    }

    @Override
    public boolean canInteractWith(EntityPlayer player){
        return te.isUseableByPlayer(player);
        //return te.isGuiUseableByPlayer(player);
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer){
        super.onContainerClosed(par1EntityPlayer);
        //  te.closeGUI();
    }
}
