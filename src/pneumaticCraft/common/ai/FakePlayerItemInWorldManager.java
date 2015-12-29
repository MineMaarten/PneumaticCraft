package pneumaticCraft.common.ai;

import java.lang.reflect.Field;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import pneumaticCraft.lib.Log;

public class FakePlayerItemInWorldManager extends ItemInWorldManager{

    private static Field isDigging, acknowledged;
    public boolean isAccepted;
    private final IDroneBase drone;

    public FakePlayerItemInWorldManager(World par1World, EntityPlayerMP player, IDroneBase drone){
        super(par1World);
        thisPlayerMP = player;
        this.drone = drone;
    }

    @Override
    public void onBlockClicked(BlockPos pos, EnumFacing side){
        super.onBlockClicked(pos, side);
        isAccepted = isDigging();
        //TODO 1.8 fix    uncheckedTryHarvestBlock(pos);
        blockRemoving(pos);
    }

    public boolean isDigging(){
        if(isDigging == null) isDigging = ReflectionHelper.findField(ItemInWorldManager.class, "field_73088_d", "isDestroyingBlock");
        try {
            return isDigging.getBoolean(this);
        } catch(Exception e) {
            Log.error("Drone FakePlayerItemInWorldManager failed with reflection (Digging)!");
            e.printStackTrace();
            return true;
        }
    }

    public boolean isAcknowledged(){
        if(acknowledged == null) acknowledged = ReflectionHelper.findField(ItemInWorldManager.class, "field_73097_j", "receivedFinishDiggingPacket");
        try {
            return acknowledged.getBoolean(this);
        } catch(Exception e) {
            Log.error("Drone FakePlayerItemInWorldManager failed with reflection (Acknowledge get)!");
            e.printStackTrace();
            return true;
        }
    }

    public void cancelDigging(){
        cancelDestroyingBlock();
    }

    /**
     * Attempts to harvest a block at the given coordinate
     */
    @Override
    public boolean tryHarvestBlock(BlockPos pos){
        return super.tryHarvestBlock(pos);//TODO 1.8 test if overriding is necessary.
        /*BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(theWorld, getGameType(), thisPlayerMP, pos);
        if(event.isCanceled()) {
            return false;
        } else {
            ItemStack stack = thisPlayerMP.getCurrentEquippedItem();
            if(stack != null && stack.getItem().onBlockStartBreak(stack, x, y, z, thisPlayerMP)) {
                return false;
            }
            Block block = theWorld.getBlock(pos);
            int l = theWorld.getBlockMetadata(pos);
            theWorld.playAuxSFXAtEntity(thisPlayerMP, 2001, pos, Block.getIdFromBlock(block) + (theWorld.getBlockMetadata(pos) << 12));
            boolean flag = false;

            ItemStack itemstack = thisPlayerMP.getCurrentEquippedItem();

            if(itemstack != null) {
                itemstack.func_150999_a(theWorld, block, pos, thisPlayerMP);

                if(itemstack.stackSize == 0) {
                    thisPlayerMP.destroyCurrentEquippedItem();
                }
            }

            if(removeBlock(x, y, z)) {
                block.harvestBlock(theWorld, thisPlayerMP, pos, l);
                flag = true;
            }

            // Drop experience
            if(!isCreative() && flag && event != null) {
                block.dropXpOnBlockBreak(theWorld, pos, event.getExpToDrop());
            }
            drone.addAir(null, -PneumaticValues.DRONE_USAGE_DIG);
            return true;
        }*/
    }

    /**
     * Removes a block and triggers the appropriate events
     */
    /*private boolean removeBlock(int par1, int par2, int par3){
        Block block = theWorld.getBlock(par1, par2, par3);
        int l = theWorld.getBlockMetadata(par1, par2, par3);
        block.onBlockHarvested(theWorld, par1, par2, par3, l, thisPlayerMP);
        boolean flag = block != null && block.removedByPlayer(theWorld, thisPlayerMP, par1, par2, par3);

        if(flag) {
            block.onBlockDestroyedByPlayer(theWorld, par1, par2, par3, l);
        }

        return flag;
    }*/

}
