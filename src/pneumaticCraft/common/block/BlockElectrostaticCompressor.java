package pneumaticCraft.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import pneumaticCraft.common.tileentity.TileEntityElectrostaticCompressor;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;

public class BlockElectrostaticCompressor extends BlockPneumaticCraftModeled{
    public BlockElectrostaticCompressor(Material par2Material){
        super(par2Material);
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityElectrostaticCompressor.class;
    }

    @Override
    public EnumGuiId getGuiID(){
        return EnumGuiId.ELECTROSTATIC_COMPRESSOR;
    }

    @Override
    public boolean isFullBlock(){
        return true;
    }
}
