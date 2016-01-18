package pneumaticCraft.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import pneumaticCraft.common.tileentity.TileEntityAirCompressor;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;

public class BlockAirCompressor extends BlockPneumaticCraftModeled{

    public static final PropertyBool ON = PropertyBool.create("on");

    public BlockAirCompressor(Material par2Material){
        super(par2Material);
    }

    @Override
    protected BlockState createBlockState(){
        return new BlockState(this, ROTATION, ON);
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return super.getMetaFromState(state) + (state.getValue(ON) ? 6 : 0);
    }

    @Override
    public IBlockState getStateFromMeta(int meta){
        return super.getStateFromMeta(meta).withProperty(ON, meta >= 6);
    }

    @Override
    public EnumGuiId getGuiID(){
        return EnumGuiId.AIR_COMPRESSOR;
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityAirCompressor.class;
    }

    @Override
    public boolean isRotatable(){
        return true;
    }
}
