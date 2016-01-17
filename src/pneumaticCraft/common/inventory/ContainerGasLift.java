package pneumaticCraft.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import pneumaticCraft.common.AchievementHandler;
import pneumaticCraft.common.fluid.Fluids;
import pneumaticCraft.common.tileentity.TileEntityGasLift;

public class ContainerGasLift extends ContainerPneumaticBase<TileEntityGasLift>{

    public ContainerGasLift(InventoryPlayer inventoryPlayer, TileEntityGasLift te){
        super(te);

        // add the upgrade slots
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 2; j++) {
                addSlotToContainer(new SlotInventoryLimiting(te, i * 2 + j, 11 + j * 18, 29 + i * 18));
            }
        }

        addSlotToContainer(new SlotInventoryLimiting(te, 4, 55, 48));

        addPlayerSlots(inventoryPlayer, 84);

        if(te.getTankInfo(EnumFacing.UP)[0].fluid != null && te.getTankInfo(EnumFacing.UP)[0].fluid.getFluid() == Fluids.oil) {
            AchievementHandler.giveAchievement(inventoryPlayer.player, new ItemStack(Fluids.getBucket(Fluids.oil)));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player){
        return te.isUseableByPlayer(player);
    }

}
