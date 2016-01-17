package pneumaticCraft.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPressureChamberGlass extends BlockPressureChamberWallBase{

    public BlockPressureChamberGlass(Material par2Material){
        super(par2Material);
    }

    private boolean isGlass(IBlockAccess world, BlockPos pos){
        return world.getBlockState(pos).getBlock() == this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side){
        EnumFacing d = side.getOpposite();
        return !isGlass(world, pos.offset(d)) || !isGlass(world, pos);
    }

    @Override
    public boolean isOpaqueCube(){
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer(){
        return EnumWorldBlockLayer.CUTOUT;
    }

}
