package pneumaticCraft.common.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.common.tileentity.TileEntityHeatSink;
import pneumaticCraft.lib.BBConstants;

public class BlockHeatSink extends BlockPneumaticCraftModeled{

    protected BlockHeatSink(Material par2Material){
        super(par2Material);
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityHeatSink.class;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, BlockPos pos){
        EnumFacing dir = getRotation(blockAccess, pos);
        setBlockBounds(dir.getFrontOffsetX() <= 0 ? 0 : 1F - BBConstants.HEAT_SINK_THICKNESS, dir.getFrontOffsetY() <= 0 ? 0 : 1F - BBConstants.HEAT_SINK_THICKNESS, dir.getFrontOffsetZ() <= 0 ? 0 : 1F - BBConstants.HEAT_SINK_THICKNESS, dir.getFrontOffsetX() >= 0 ? 1 : BBConstants.HEAT_SINK_THICKNESS, dir.getFrontOffsetY() >= 0 ? 1 : BBConstants.HEAT_SINK_THICKNESS, dir.getFrontOffsetZ() >= 0 ? 1 : BBConstants.HEAT_SINK_THICKNESS);
    }

    @Override
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB axisalignedbb, List arraylist, Entity par7Entity){
        setBlockBoundsBasedOnState(world, pos);
        super.addCollisionBoxesToList(world, pos, state, axisalignedbb, arraylist, par7Entity);
    }

    @Override
    public boolean isRotatable(){
        return true;
    }

    @Override
    protected boolean canRotateToTopOrBottom(){
        return true;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity){
        TileEntityHeatSink heatSink = (TileEntityHeatSink)world.getTileEntity(pos);
        if(heatSink.getHeatExchangerLogic(null).getTemperature() > 323) {
            entity.setFire(3);
        }
    }
}
