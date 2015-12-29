package pneumaticCraft.common.block;

import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockKeroseneLampLight extends BlockAir{

    @Override
    public int getLightValue(IBlockAccess world, BlockPos pos){
        return 15;
    }
}
