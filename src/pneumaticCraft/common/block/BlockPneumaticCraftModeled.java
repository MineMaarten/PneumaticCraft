package pneumaticCraft.common.block;

import net.minecraft.block.material.Material;

public abstract class BlockPneumaticCraftModeled extends BlockPneumaticCraft{

    protected BlockPneumaticCraftModeled(Material par2Material){
        super(par2Material);
    }

    @Override
    public boolean isOpaqueCube(){
        return false;
    }

    @Override
    public boolean isFullCube(){
        return false;
    }
}
