package pneumaticCraft.common.thirdparty.computercraft;

import java.util.Collections;
import java.util.Set;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import pneumaticCraft.common.block.BlockPneumaticCraftModeled;

public class BlockDroneInterface extends BlockPneumaticCraftModeled{

    protected BlockDroneInterface(Material par2Material){
        super(par2Material);
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityDroneInterface.class;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side){
        return side == EnumFacing.DOWN;
    }

    @Override
    public Set<Item> getApplicableUpgrades(){
        return Collections.EMPTY_SET;
    }
}
