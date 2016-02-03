package pneumaticCraft.common.ai;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemRedstone;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import pneumaticCraft.common.progwidgets.IBlockRightClicker;
import pneumaticCraft.common.progwidgets.ISidedWidget;
import pneumaticCraft.common.progwidgets.ProgWidgetAreaItemBase;
import pneumaticCraft.common.progwidgets.ProgWidgetPlace;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.lib.Log;

public class DroneAIBlockInteract extends DroneAIBlockInteraction{

    private final List<BlockPos> visitedPositions = new ArrayList<BlockPos>();

    public DroneAIBlockInteract(IDroneBase drone, ProgWidgetAreaItemBase widget){
        super(drone, widget);
        drone.getFakePlayer().setSneaking(((IBlockRightClicker)widget).isSneaking());
    }

    @Override
    protected boolean isValidPosition(BlockPos pos){
        return !visitedPositions.contains(pos) && (widget.isItemFilterEmpty() || DroneAIDig.isBlockValidForFilter(drone.world(), drone, pos, widget));
    }

    @Override
    protected boolean doBlockInteraction(BlockPos pos, double distToBlock){
        visitedPositions.add(pos);
        boolean result = rightClick(pos);
        if(drone.getFakePlayer().getCurrentEquippedItem() != null && drone.getFakePlayer().getCurrentEquippedItem().stackSize <= 0) {
            drone.getFakePlayer().setCurrentItemOrArmor(0, null);
        }
        transferToDroneFromFakePlayer(drone);
        return result;
    }

    public static void transferToDroneFromFakePlayer(IDroneBase drone){
        //transfer items
        for(int j = 1; j < drone.getFakePlayer().inventory.mainInventory.length; j++) {
            ItemStack excessStack = drone.getFakePlayer().inventory.mainInventory[j];
            if(excessStack != null) {
                ItemStack remainder = PneumaticCraftUtils.exportStackToInventory(drone.getInv(), excessStack, null);
                if(remainder != null) {
                    drone.dropItem(remainder);
                }
                drone.getFakePlayer().inventory.mainInventory[j] = null;
            }
        }

    }

    private boolean rightClick(BlockPos pos){

        EnumFacing faceDir = ProgWidgetPlace.getDirForSides(((ISidedWidget)widget).getSides());
        EntityPlayer player = drone.getFakePlayer();
        World worldObj = drone.world();
        int dx = faceDir.getFrontOffsetX();
        int dy = faceDir.getFrontOffsetY();
        int dz = faceDir.getFrontOffsetZ();

        player.setPosition(pos.getX() + 0.5, pos.getY() + 0.5 - player.eyeHeight, pos.getZ() + 0.5);
        player.rotationPitch = faceDir.getFrontOffsetY() * -90;
        switch(faceDir){
            case NORTH:
                player.rotationYaw = 180;
                break;
            case SOUTH:
                player.rotationYaw = 0;
                break;
            case WEST:
                player.rotationYaw = 90;
                break;
            case EAST:
                player.rotationYaw = -90;
        }

        try {
            PlayerInteractEvent event = ForgeEventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_AIR, worldObj, pos, faceDir);
            if(event.isCanceled()) return false;

            IBlockState state = worldObj.getBlockState(pos);
            Block block = state.getBlock();

            ItemStack stack = player.getCurrentEquippedItem();
            if(stack != null && stack.getItem().onItemUseFirst(stack, player, worldObj, pos, faceDir, dx, dy, dz)) return false;

            if(!worldObj.isAirBlock(pos) && block.onBlockActivated(worldObj, pos, state, player, faceDir, dx, dy, dz)) return false;

            if(stack != null) {
                boolean isGoingToShift = false;
                if(stack.getItem() instanceof ItemReed || stack.getItem() instanceof ItemRedstone) {
                    isGoingToShift = true;
                }
                if(stack.getItem().onItemUse(stack, player, worldObj, pos, faceDir, dx, dy, dz)) return false;

                ItemStack copy = stack.copy();
                player.setCurrentItemOrArmor(0, stack.getItem().onItemRightClick(stack, worldObj, player));
                if(!copy.isItemEqual(stack)) return true;
            }
            return false;
        } catch(Throwable e) {
            Log.error("DroneAIBlockInteract crashed! Stacktrace: ");
            e.printStackTrace();
            return false;
        }
    }

}
