package pneumaticCraft.common.block;

import java.util.List;

import net.minecraft.block.BlockAir;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.common.entity.living.EntityDrone;
import pneumaticCraft.common.tileentity.TileEntityDroneRedstoneEmitter;

public class BlockDroneRedstoneEmitter extends BlockAir implements ITileEntityProvider{

    @Override
    public boolean canProvidePower(){
        return true;
    }

    @Override
    public int getStrongPower(IBlockAccess par1IBlockAccess, BlockPos pos, IBlockState state, EnumFacing side){
        return 0;
    }

    @Override
    public int getWeakPower(IBlockAccess blockAccess, BlockPos pos, IBlockState state, EnumFacing side){
        if(blockAccess instanceof World) {
            World world = (World)blockAccess;
            List<EntityDrone> drones = world.getEntitiesWithinAABB(EntityDrone.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)));
            int signal = 0;
            for(EntityDrone drone : drones) {
                signal = Math.max(signal, drone.getEmittingRedstone(side.getOpposite()));
            }
            return signal;

        } else {
            return 0;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_){
        return new TileEntityDroneRedstoneEmitter();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state){
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }
}
