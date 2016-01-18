package pneumaticCraft.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.PneumaticCraft;
import pneumaticCraft.common.tileentity.TileEntityElevatorBase;
import pneumaticCraft.common.tileentity.TileEntityElevatorFrame;

public class BlockElevatorFrame extends BlockPneumaticCraftModeled{

    public BlockElevatorFrame(Material par2Material){
        super(par2Material);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state){
        super.onBlockAdded(world, pos, state);
        TileEntityElevatorBase elevatorBase = getElevatorTE(world, pos);
        if(elevatorBase != null) {
            elevatorBase.updateMaxElevatorHeight();
        }
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityElevatorFrame.class;
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 origin, Vec3 direction){
        if(world.isRemote) {
            ItemStack playerStack = PneumaticCraft.proxy.getPlayer().getCurrentEquippedItem();
            if(playerStack != null && playerStack.getItem() == Item.getItemFromBlock(this)) {
                return super.collisionRayTrace(world, pos, origin, direction);
            }
        }
        boolean frameXPos = world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blockss.elevatorFrame;
        boolean frameXNeg = world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blockss.elevatorFrame;
        boolean frameZPos = world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blockss.elevatorFrame;
        boolean frameZNeg = world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blockss.elevatorFrame;

        boolean isColliding = false;

        if(!frameXNeg && !frameZNeg) {
            setBlockBounds(0, 0, 0, 2 / 16F, 1, 2 / 16F);
            if(super.collisionRayTrace(world, pos, origin, direction) != null) isColliding = true;
        }
        if(!frameXNeg && !frameZPos) {
            setBlockBounds(0, 0, 14 / 16F, 2 / 16F, 1, 1);
            if(super.collisionRayTrace(world, pos, origin, direction) != null) isColliding = true;
        }
        if(!frameXPos && !frameZPos) {
            setBlockBounds(14 / 16F, 0, 14 / 16F, 1, 1, 1);
            if(super.collisionRayTrace(world, pos, origin, direction) != null) isColliding = true;
        }
        if(!frameXPos && !frameZNeg) {
            setBlockBounds(14 / 16F, 0, 0, 1, 1, 2 / 16F);
            if(super.collisionRayTrace(world, pos, origin, direction) != null) isColliding = true;
        }

        setBlockBounds(0, 0, 0, 1, 1, 1);
        return isColliding ? super.collisionRayTrace(world, pos, origin, direction) : null;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World par1World, BlockPos pos, IBlockState state){
        float blockHeight = getElevatorBlockHeight(par1World, pos);
        if(blockHeight > 0F) {
            // this.setBlockBounds(0, 0, 0, 1, blockHeight, 1);
            // return super.getCollisionBoundingBoxFromPool(par1World, par2,
            // par3, par4);
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            return new AxisAlignedBB(x, y, z, x + 1, y + blockHeight, z + 1);
        } else {
            return null;
        }
        // return null;
    }

    @Override
    public boolean isFullCube(){
        return false;
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity){
        //  float blockHeight = getElevatorBlockHeight(world, x, y, z);
        //   if(blockHeight > 0) {
        // if(entity.posY < y + blockHeight) {
        //     entity.setPosition(entity.posX, y + blockHeight + 2, entity.posZ);
        TileEntityElevatorBase te = getElevatorTE(world, pos);
        if(te != null && te.oldExtension != te.extension) {
            entity.setPosition(entity.posX, te.getPos().getY() + te.extension + entity.getYOffset() + entity.height + 1, entity.posZ);
        }
        entity.fallDistance = 0;
        //}
        //   }
    }

    public static TileEntityElevatorBase getElevatorTE(IBlockAccess world, BlockPos pos){
        while(true) {
            pos = pos.offset(EnumFacing.DOWN);
            if(world.getBlockState(pos).getBlock() == Blockss.elevatorBase) break;
            if(world.getBlockState(pos).getBlock() != Blockss.elevatorFrame || pos.getY() <= 0) return null;
        }
        return (TileEntityElevatorBase)world.getTileEntity(pos);
    }

    private float getElevatorBlockHeight(World world, BlockPos pos){
        TileEntityElevatorBase te = getElevatorTE(world, pos);
        if(te == null) return 0F;
        float blockHeight = te.extension - (pos.getY() - te.getPos().getY()) + 1;
        // System.out.println("blockHeight (" + x + ", " + y + ", " + z + "): " + blockHeight);
        // + blockHeight);
        if(blockHeight < 0F) return 0F;
        if(blockHeight > 1F) return 1F;
        return blockHeight;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state){
        TileEntityElevatorBase elevatorBase = getElevatorTE(world, pos);
        if(elevatorBase != null) {
            elevatorBase.updateMaxElevatorHeight();
        }
        super.breakBlock(world, pos, state);
    }
}
