package pneumaticCraft.common.semiblock;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ItemSemiBlockBase extends Item implements ISemiBlockItem{
    public final String semiBlockId;

    public ItemSemiBlockBase(String semiBlockId){
        this.semiBlockId = semiBlockId;
        setUnlocalizedName(semiBlockId);
    }

    public ItemSemiBlockBase(Class<? extends ISemiBlock> semiBlock){
        this(SemiBlockManager.getKeyForSemiBlock(semiBlock));
    }

    @Override
    public ISemiBlock getSemiBlock(World world, BlockPos pos, ItemStack stack){
        return SemiBlockManager.getSemiBlockForKey(semiBlockId);
    }

}
