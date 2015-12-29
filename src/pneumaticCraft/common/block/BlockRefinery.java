package pneumaticCraft.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import pneumaticCraft.common.tileentity.TileEntityRefinery;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;

public class BlockRefinery extends BlockPneumaticCraftModeled{

    public BlockRefinery(Material par2Material){
        super(par2Material);
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityRefinery.class;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float par7, float par8, float par9){
        TileEntityRefinery refinery = (TileEntityRefinery)world.getTileEntity(pos);
        refinery = refinery.getMasterRefinery();
        return super.onBlockActivated(world, refinery.getPos(), state, player, side, par7, par8, par9);
    }

    @Override
    public EnumGuiId getGuiID(){
        return EnumGuiId.REFINERY;
    }

    @Override
    public boolean isRotatable(){
        return true;
    }
}
