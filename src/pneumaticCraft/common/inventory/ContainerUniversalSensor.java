package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import pneumaticCraft.common.tileentity.TileEntityUniversalSensor;

public class ContainerUniversalSensor extends ContainerPneumaticBase<TileEntityUniversalSensor>{

    public ContainerUniversalSensor(InventoryPlayer inventoryPlayer, TileEntityUniversalSensor te){
        super(te);

        // add the upgrade slots
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 2; j++) {
                addSlotToContainer(new SlotInventoryLimiting(te, i * 2 + j, 19 + j * 18, 108 + i * 18));
            }
        }

        addPlayerSlots(inventoryPlayer, 157);
    }

}
