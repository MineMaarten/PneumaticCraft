package pneumaticCraft.common.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.common.tileentity.TileEntityPressureChamberValve;
import pneumaticCraft.common.tileentity.TileEntityPressureChamberWall;

public class BlockPressureChamberWall extends BlockPneumaticCraft{
    private static final PropertyBool GLASS = PropertyBool.create("glass");

    public BlockPressureChamberWall(Material par2Material){
        super(par2Material);
    }

    private boolean isGlass(IBlockAccess world, BlockPos pos){
        return world.getBlockState(pos).getBlock() == this && world.getBlockState(pos).getValue(GLASS);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side){
        EnumFacing d = side.getOpposite();
        return !isGlass(world, pos.offset(d)) || !isGlass(world, pos);
    }

    @Override
    public boolean isOpaqueCube(){
        return false;
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityPressureChamberWall.class;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List){
        for(int var4 = 0; var4 < 2; ++var4) {
            par3List.add(new ItemStack(this, 1, var4 * 6));
        }
    }

    /**
     * Called when the block is placed in the world.
     */
    @Override
    public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLiving, ItemStack iStack){
        super.onBlockPlacedBy(par1World, pos, state, par5EntityLiving, iStack);
        TileEntityPressureChamberValve.checkIfProperlyFormed(par1World, pos);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float par7, float par8, float par9){
        if(world.isRemote) return true;
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof TileEntityPressureChamberWall) {
            TileEntityPressureChamberValve valve = ((TileEntityPressureChamberWall)te).getCore();
            if(valve != null) {
                return valve.getBlockType().onBlockActivated(world, pos, state, player, side, par7, par8, par9);
            }
        }
        return false;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state){
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof TileEntityPressureChamberWall && !world.isRemote) {
            ((TileEntityPressureChamberWall)te).onBlockBreak();
        }
        super.breakBlock(world, pos, state);

    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and
     * wood.
     */
    @Override
    public int damageDropped(IBlockState state){
        return state.getValue(GLASS) ? 6 : 0; //TODO convert to 1 : 0.
    }

}
