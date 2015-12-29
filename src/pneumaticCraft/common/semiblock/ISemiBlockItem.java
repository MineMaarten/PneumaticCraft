package pneumaticCraft.common.semiblock;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface ISemiBlockItem{
    public ISemiBlock getSemiBlock(World world, BlockPos pos, ItemStack stack);
}
