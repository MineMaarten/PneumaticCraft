package pneumaticCraft.common.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.PneumaticCraft;
import pneumaticCraft.common.tileentity.TileEntityPressureChamberInterface;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;

public class BlockPressureChamberInterface extends BlockPressureChamberWall{

    public BlockPressureChamberInterface(Material par2Material){
        super(par2Material);
    }

    @Override
    public int getRenderType(){
        return 3;//TODO 1.8 PneumaticCraft.proxy.SPECIAL_RENDER_TYPE_VALUE;
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityPressureChamberInterface.class;
    }

    @Override
    public EnumGuiId getGuiID(){
        return EnumGuiId.PRESSURE_CHAMBER_INTERFACE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List){
        par3List.add(new ItemStack(this, 1, 0));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float par7, float par8, float par9){
        if(player.isSneaking() || getGuiID() == null) return false;
        else {
            if(!world.isRemote) {
                TileEntity te = world.getTileEntity(pos);

                if(te != null) {
                    player.openGui(PneumaticCraft.instance, getGuiID().ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
                }
            }

            return true;
        }
    }

    @Override
    public boolean isRotatable(){
        return true;
    }

    @Override
    protected boolean canRotateToTopOrBottom(){
        return true;
    }

    @Override
    protected int getInventoryDropEndSlot(IInventory inventory){
        return 5;
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and
     * wood.
     */
    @Override
    public int damageDropped(IBlockState state){
        return 0;
    }

}
