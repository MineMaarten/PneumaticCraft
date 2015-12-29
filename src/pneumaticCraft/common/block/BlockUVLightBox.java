package pneumaticCraft.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.common.tileentity.TileEntityUVLightBox;
import pneumaticCraft.lib.BBConstants;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;

public class BlockUVLightBox extends BlockPneumaticCraftModeled{

    public BlockUVLightBox(Material par2Material){
        super(par2Material);

    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, BlockPos pos){
        EnumFacing facing = getRotation(blockAccess, pos);
        if(facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
            setBlockBounds(BBConstants.UV_LIGHT_BOX_LENGTH_MIN, 0, BBConstants.UV_LIGHT_BOX_WIDTH_MIN, 1 - BBConstants.UV_LIGHT_BOX_LENGTH_MIN, BBConstants.UV_LIGHT_BOX_TOP_MAX, 1 - BBConstants.UV_LIGHT_BOX_WIDTH_MIN);
        } else {
            setBlockBounds(BBConstants.UV_LIGHT_BOX_WIDTH_MIN, 0, BBConstants.UV_LIGHT_BOX_LENGTH_MIN, 1 - BBConstants.UV_LIGHT_BOX_WIDTH_MIN, BBConstants.UV_LIGHT_BOX_TOP_MAX, 1 - BBConstants.UV_LIGHT_BOX_LENGTH_MIN);
        }
    }

    @Override
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB axisalignedbb, List arraylist, Entity par7Entity){
        setBlockBoundsBasedOnState(world, pos);
        super.addCollisionBoxesToList(world, pos, state, axisalignedbb, arraylist, par7Entity);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityUVLightBox.class;
    }

    @Override
    public EnumGuiId getGuiID(){
        return EnumGuiId.UV_LIGHT_BOX;
    }

    @Override
    public int getLightValue(IBlockAccess world, BlockPos pos){
        Block block = world.getBlockState(pos).getBlock();
        if(block != null && block != this) {
            return block.getLightValue(world, pos);
        }
        TileEntity te = world.getTileEntity(pos);
        if(te != null && te instanceof TileEntityUVLightBox) {
            return ((TileEntityUVLightBox)te).getLightLevel();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isRotatable(){
        return true;
    }
}
