package pneumaticCraft.common.itemBlock;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import pneumaticCraft.common.tileentity.TileEntityOmnidirectionalHopper;

public class ItemBlockOmnidirectionalHopper extends ItemBlockPneumaticCraft{

    public ItemBlockOmnidirectionalHopper(Block block){
        super(block);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ){
        boolean result = super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
        if(result) {
            TileEntity te = worldIn.getTileEntity(pos.offset(side));
            if(te instanceof TileEntityOmnidirectionalHopper) {
                ((TileEntityOmnidirectionalHopper)te).setRotation(side.getOpposite());
            }
        }
        return result;
    }

}
