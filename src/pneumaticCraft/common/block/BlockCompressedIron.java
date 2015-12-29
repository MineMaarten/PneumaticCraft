package pneumaticCraft.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.common.tileentity.TileEntityCompressedIronBlock;

public class BlockCompressedIron extends BlockPneumaticCraft{

    protected BlockCompressedIron(Material par2Material){
        super(par2Material);
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityCompressedIronBlock.class;
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     * TODO 1.8 test, renderpass?
     */
    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass){
        TileEntityCompressedIronBlock te = (TileEntityCompressedIronBlock)world.getTileEntity(pos);
        int heatLevel = te.getHeatLevel();
        double[] color = TileEntityCompressedIronBlock.getColorForHeatLevel(heatLevel);
        return 0xFF000000 + ((int)(color[0] * 255) << 16) + ((int)(color[1] * 255) << 8) + (int)(color[2] * 255);
    }

}
