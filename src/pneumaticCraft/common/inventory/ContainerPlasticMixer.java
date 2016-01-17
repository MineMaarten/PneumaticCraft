package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import pneumaticCraft.common.tileentity.TileEntityPlasticMixer;

public class ContainerPlasticMixer extends ContainerPneumaticBase<TileEntityPlasticMixer>{

    public ContainerPlasticMixer(InventoryPlayer inventoryPlayer, TileEntityPlasticMixer te){
        super(te);

        // add the upgrade slots
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 2; j++) {
                addSlotToContainer(new SlotInventoryLimiting(te, i * 2 + j, 11 + j * 18, 29 + i * 18));
            }
        }

        addSlotToContainer(new SlotInventoryLimiting(te, TileEntityPlasticMixer.INV_INPUT, 98, 26));
        addSlotToContainer(new SlotInventoryLimiting(te, TileEntityPlasticMixer.INV_OUTPUT, 98, 58));
        for(int i = 0; i < 3; i++) {
            addSlotToContainer(new SlotInventoryLimiting(te, TileEntityPlasticMixer.INV_DYE_RED + i, 127, 22 + i * 18));
        }

        addPlayerSlots(inventoryPlayer, 84);
    }

}
