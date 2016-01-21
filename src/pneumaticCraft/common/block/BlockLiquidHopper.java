package pneumaticCraft.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.common.tileentity.TileEntityLiquidHopper;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;

public class BlockLiquidHopper extends BlockOmnidirectionalHopper{

    public BlockLiquidHopper(Material par2Material){
        super(par2Material);
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityLiquidHopper.class;
    }

    @Override
    public EnumGuiId getGuiID(){
        return EnumGuiId.LIQUID_HOPPER;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer(){
        return EnumWorldBlockLayer.TRANSLUCENT;
    }
}
