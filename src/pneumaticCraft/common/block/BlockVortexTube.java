package pneumaticCraft.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import pneumaticCraft.api.tileentity.IPneumaticMachine;
import pneumaticCraft.common.thirdparty.ModInteractionUtils;
import pneumaticCraft.common.tileentity.TileEntityVortexTube;

public class BlockVortexTube extends BlockPneumaticCraftModeled{

    protected BlockVortexTube(Material par2Material){
        super(par2Material);
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityVortexTube.class;
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
    protected boolean rotateCustom(World world, BlockPos pos, IBlockState state, EnumFacing side){
        EnumFacing rotation = getRotation(world, pos);
        if(rotation.getAxis() == side.getAxis()) {
            TileEntityVortexTube te = (TileEntityVortexTube)world.getTileEntity(pos);
            te.rotateRoll(rotation == side ? 1 : -1);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLiving, ItemStack par6ItemStack){
        super.onBlockPlacedBy(world, pos, state, par5EntityLiving, par6ItemStack);
        TileEntityVortexTube te = (TileEntityVortexTube)world.getTileEntity(pos);
        for(int i = 0; i < 4; i++) {
            te.rotateRoll(1);
            EnumFacing d = te.getTubeDirection();
            IPneumaticMachine pneumaticMachine = ModInteractionUtils.getInstance().getMachine(world.getTileEntity(pos.offset(d)));
            if(pneumaticMachine != null && pneumaticMachine.isConnectedTo(d.getOpposite())) break;
        }
    }
}
