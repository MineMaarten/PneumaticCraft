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
import pneumaticCraft.common.tileentity.TileEntityAssemblyDrill;
import pneumaticCraft.lib.BBConstants;

public class BlockAssemblyDrill extends BlockPneumaticCraftModeled{

    public BlockAssemblyDrill(Material par2Material){
        super(par2Material);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, BlockPos pos){
        setBlockBounds(BBConstants.ASSEMBLY_ROBOT_MIN_POS, 0F, BBConstants.ASSEMBLY_ROBOT_MIN_POS, BBConstants.ASSEMBLY_ROBOT_MAX_POS, BBConstants.ASSEMBLY_ROBOT_MAX_POS_TOP, BBConstants.ASSEMBLY_ROBOT_MAX_POS);
    }

    @Override
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB axisalignedbb, List arraylist, Entity par7Entity){
        setBlockBounds(BBConstants.ASSEMBLY_ROBOT_MIN_POS, BBConstants.ASSEMBLY_ROBOT_MIN_POS, BBConstants.ASSEMBLY_ROBOT_MIN_POS, BBConstants.ASSEMBLY_ROBOT_MAX_POS, BBConstants.ASSEMBLY_ROBOT_MAX_POS_TOP, BBConstants.ASSEMBLY_ROBOT_MAX_POS);
        super.addCollisionBoxesToList(world, pos, state, axisalignedbb, arraylist, par7Entity);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityAssemblyDrill.class;
    }
}
