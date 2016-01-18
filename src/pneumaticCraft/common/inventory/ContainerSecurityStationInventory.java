package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.tileentity.TileEntitySecurityStation;

public class ContainerSecurityStationInventory extends ContainerPneumaticBase<TileEntitySecurityStation>{

    public ContainerSecurityStationInventory(InventoryPlayer inventoryPlayer, TileEntitySecurityStation te){
        super(te);

        //add the network slots
        for(int i = 0; i < 7; i++) {
            for(int j = 0; j < 5; j++) {
                addSlotToContainer(new SlotItemSpecific(te, Itemss.networkComponent, j + i * 5, 17 + j * 18, 22 + i * 18));
            }
        }

        // add the upgrade slots
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 2; j++) {
                addSlotToContainer(new SlotUpgrade(te, i * 2 + j + TileEntitySecurityStation.UPGRADE_SLOT_START, 128 + j * 18, 62 + i * 18));
            }
        }

        addPlayerSlots(inventoryPlayer, 157);
    }

}
