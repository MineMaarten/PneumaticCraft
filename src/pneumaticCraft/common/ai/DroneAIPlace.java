package pneumaticCraft.common.ai;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import pneumaticCraft.common.entity.living.EntityDrone;
import pneumaticCraft.common.progwidgets.ISidedWidget;
import pneumaticCraft.common.progwidgets.ProgWidgetAreaItemBase;
import pneumaticCraft.common.progwidgets.ProgWidgetPlace;
import pneumaticCraft.lib.PneumaticValues;

public class DroneAIPlace extends DroneAIBlockInteraction{

    /** 
     * @param drone
     * @param speed
     * @param widget needs to implement IBlockOrdered and IDirectionalWidget.
     */
    public DroneAIPlace(IDroneBase drone, ProgWidgetAreaItemBase widget){
        super(drone, widget);
    }

    @Override
    protected boolean respectClaims(){
        return true;
    }

    @Override
    protected boolean isValidPosition(BlockPos pos){
        if(drone.world().isAirBlock(pos)) {
            boolean failedOnPlacement = false;
            for(int i = 0; i < drone.getInv().getSizeInventory(); i++) {
                ItemStack droneStack = drone.getInv().getStackInSlot(i);
                if(droneStack != null && droneStack.getItem() instanceof ItemBlock) {
                    if(widget.isItemValidForFilters(droneStack)) {
                        Block placingBlock = ((ItemBlock)droneStack.getItem()).getBlock();
                        EnumFacing side = ProgWidgetPlace.getDirForSides(((ISidedWidget)widget).getSides());
                        if(drone.world().canBlockBePlaced(placingBlock, pos, false, side, drone instanceof EntityDrone ? (EntityDrone)drone : null, droneStack)) {
                            return true;
                        } else {
                            if(drone.world().canBlockBePlaced(placingBlock, pos, true, side, drone instanceof EntityDrone ? (EntityDrone)drone : null, droneStack)) {
                                drone.addDebugEntry("gui.progWidget.place.debug.cantPlaceBlock", pos);
                            } else {
                                drone.addDebugEntry("gui.progWidget.place.debug.entityInWay", pos);
                            }
                            failedOnPlacement = true;
                        }
                    }
                }
            }
            if(!failedOnPlacement) abort();
        }
        return false;
    }

    //TODO 1.8 test
    @Override
    protected boolean doBlockInteraction(BlockPos pos, double distToBlock){
        if(drone.getPathNavigator().hasNoPath()) {
            EnumFacing side = ProgWidgetPlace.getDirForSides(((ISidedWidget)widget).getSides());
            for(int i = 0; i < drone.getInv().getSizeInventory(); i++) {
                ItemStack droneStack = drone.getInv().getStackInSlot(i);
                if(droneStack != null && droneStack.getItem() instanceof ItemBlock && ((ItemBlock)droneStack.getItem()).getBlock().canPlaceBlockOnSide(drone.world(), pos, ProgWidgetPlace.getDirForSides(((ISidedWidget)widget).getSides()))) {
                    if(widget.isItemValidForFilters(droneStack)) {
                        ItemBlock itemBlock = (ItemBlock)droneStack.getItem();
                        Block block = itemBlock.getBlock();
                        if(drone.world().canBlockBePlaced(block, pos, false, side, drone instanceof EntityDrone ? (EntityDrone)drone : null, droneStack)) {
                            int newMeta = itemBlock.getMetadata(droneStack.getMetadata());
                            setFakePlayerAccordingToDir();
                            IBlockState iblockstate1 = block.onBlockPlaced(drone.world(), pos, side, side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ(), newMeta, drone.getFakePlayer());
                            if(itemBlock.placeBlockAt(droneStack, drone.getFakePlayer(), drone.world(), pos, side, side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ(), iblockstate1)) {
                                drone.addAir(null, -PneumaticValues.DRONE_USAGE_PLACE);
                                drone.world().playSoundEffect(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, block.stepSound.getPlaceSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getFrequency() * 0.8F);
                                if(--droneStack.stackSize <= 0) {
                                    drone.getInv().setInventorySlotContents(i, null);
                                }
                            }
                            return false;
                        }
                    }
                }
            }
            return false;
        } else {
            return true;
        }
    }

    private void setFakePlayerAccordingToDir(){
        EntityPlayer fakePlayer = drone.getFakePlayer();
        Vec3 pos = drone.getDronePos();
        fakePlayer.posX = pos.xCoord;
        fakePlayer.posZ = pos.zCoord;
        switch(ProgWidgetPlace.getDirForSides(((ISidedWidget)widget).getSides())){
            case UP:
                fakePlayer.rotationPitch = -90;
                fakePlayer.posY = pos.yCoord - 10;//do this for PistonBase.determineDirection()
                return;
            case DOWN:
                fakePlayer.rotationPitch = 90;
                fakePlayer.posY = pos.yCoord + 10;//do this for PistonBase.determineDirection()
                return;
            case NORTH:
                fakePlayer.rotationYaw = 180;
                fakePlayer.posY = pos.yCoord;//do this for PistonBase.determineDirection()
                break;
            case EAST:
                fakePlayer.rotationYaw = 270;
                fakePlayer.posY = pos.yCoord;//do this for PistonBase.determineDirection()
                break;
            case SOUTH:
                fakePlayer.rotationYaw = 0;
                fakePlayer.posY = pos.yCoord;//do this for PistonBase.determineDirection()
                break;
            case WEST:
                fakePlayer.rotationYaw = 90;
                fakePlayer.posY = pos.yCoord;//do this for PistonBase.determineDirection()
                break;
        }
    }

}
