package pneumaticCraft.common.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.common.tileentity.TileEntityAirCannon;
import pneumaticCraft.lib.BBConstants;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;

public class BlockAirCannon extends BlockPneumaticCraftModeled{

    public BlockAirCannon(Material par2Material){
        super(par2Material);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, BlockPos pos){
        setBlockBounds(BBConstants.AIR_CANNON_MIN_POS_SIDE, 0F, BBConstants.AIR_CANNON_MIN_POS_SIDE, BBConstants.AIR_CANNON_MAX_POS_SIDE, BBConstants.AIR_CANNON_MAX_POS_TOP, BBConstants.AIR_CANNON_MAX_POS_SIDE);
    }

    @Override
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB axisalignedbb, List arraylist, Entity par7Entity){
        setBlockBounds(BBConstants.AIR_CANNON_MIN_POS_SIDE, BBConstants.AIR_CANNON_MIN_POS_SIDE, BBConstants.AIR_CANNON_MIN_POS_SIDE, BBConstants.AIR_CANNON_MAX_POS_SIDE, BBConstants.AIR_CANNON_MAX_POS_TOP, BBConstants.AIR_CANNON_MAX_POS_SIDE);
        super.addCollisionBoxesToList(world, pos, state, axisalignedbb, arraylist, par7Entity);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public EnumGuiId getGuiID(){
        return EnumGuiId.AIR_CANNON;
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityAirCannon.class;
    }

    @Override
    public boolean isRotatable(){
        return true;
    }
}
