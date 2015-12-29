package pneumaticCraft.common.block;

import net.minecraft.block.material.Material;

public abstract class BlockPneumaticCraftModeled extends BlockPneumaticCraft{

    protected BlockPneumaticCraftModeled(Material par2Material){
        super(par2Material);
    }

    @Override
    public int getRenderType(){
        return 3;//TODO 1.8 PneumaticCraft.proxy.SPECIAL_RENDER_TYPE_VALUE;
    }

    /* @Override
     public boolean renderAsNormalBlock(){
         return false;
     }*/

    @Override
    public boolean isOpaqueCube(){
        return false;
    }

}
