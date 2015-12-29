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
import pneumaticCraft.common.tileentity.TileEntityPlasticMixer;
import pneumaticCraft.lib.BBConstants;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;

public class BlockPlasticMixer extends BlockPneumaticCraftModeled{

    protected BlockPlasticMixer(Material par2Material){
        super(par2Material);
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityPlasticMixer.class;
    }

    @Override
    public EnumGuiId getGuiID(){
        return EnumGuiId.PLASTIC_MIXER;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, BlockPos pos){
        setBlockBounds(BBConstants.PLASTIC_MIXER_MIN_POS, 0F, BBConstants.PLASTIC_MIXER_MIN_POS, BBConstants.PLASTIC_MIXER_MAX_POS, 1, BBConstants.PLASTIC_MIXER_MAX_POS);
    }

    @Override
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB axisalignedbb, List arraylist, Entity par7Entity){
        setBlockBounds(BBConstants.PLASTIC_MIXER_MIN_POS, 0F, BBConstants.PLASTIC_MIXER_MIN_POS, BBConstants.PLASTIC_MIXER_MAX_POS, 1, BBConstants.PLASTIC_MIXER_MAX_POS);
        super.addCollisionBoxesToList(world, pos, state, axisalignedbb, arraylist, par7Entity);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }
}
