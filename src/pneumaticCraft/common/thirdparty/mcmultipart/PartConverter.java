package pneumaticCraft.common.thirdparty.mcmultipart;

import java.util.Arrays;
import java.util.Collection;

import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IPartConverter;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import pneumaticCraft.common.block.Blockss;

public class PartConverter implements IPartConverter{

    @Override
    public Collection<Block> getConvertableBlocks(){
        return Arrays.asList(Blockss.pressureTube, Blockss.advancedPressureTube);
    }

    @Override
    public Collection<? extends IMultipart> convertBlock(IBlockAccess world, BlockPos pos){
        Block block = world.getBlockState(pos).getBlock();
        if(block == Blockss.pressureTube) {
            return Arrays.asList(new PartPressureTube());
        } else {
            return Arrays.asList(new PartAdvancedPressureTube());
        }
    }

}
