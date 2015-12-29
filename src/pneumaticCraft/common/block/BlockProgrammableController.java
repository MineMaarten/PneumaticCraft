package pneumaticCraft.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import pneumaticCraft.common.tileentity.TileEntityProgrammableController;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;

public class BlockProgrammableController extends BlockPneumaticCraft{

    public BlockProgrammableController(Material par2Material){
        super(par2Material);
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityProgrammableController.class;
    }

    @Override
    public EnumGuiId getGuiID(){
        return EnumGuiId.PROGRAMMABLE_CONTROLLER;
    }

    /**
     * Returns true if the block is emitting direct/strong redstone power on the
     * specified side. Args: World, X, Y, Z, side. Note that the side is
     * reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    @Override
    public int getStrongPower(IBlockAccess par1IBlockAccess, BlockPos pos, IBlockState state, EnumFacing side){
        return 0;
    }

    /**
     * Returns true if the block is emitting indirect/weak redstone power on the
     * specified side. If isBlockNormalCube returns true, standard redstone
     * propagation rules will apply instead and this will not be called. Args:
     * World, X, Y, Z, side. Note that the side is reversed - eg it is 1 (up)
     * when checking the bottom of the block.
     */
    @Override
    public int getWeakPower(IBlockAccess par1IBlockAccess, BlockPos pos, IBlockState state, EnumFacing side){
        TileEntity te = par1IBlockAccess.getTileEntity(pos);
        if(te instanceof TileEntityProgrammableController) {
            return ((TileEntityProgrammableController)te).getEmittingRedstone(side.getOpposite());
        }

        return 0;
    }

    /**
     * Called to determine whether to allow the a block to handle its own indirect power rather than using the default rules.
     * @param world The world
     * @param x The x position of this block instance
     * @param y The y position of this block instance
     * @param z The z position of this block instance
     * @param side The INPUT side of the block to be powered - ie the opposite of this block's output side
     * @return Whether Block#getWeakPower should be called when determining indirect power
     */
    @Override
    public boolean shouldCheckWeakPower(IBlockAccess world, BlockPos pos, EnumFacing side){
        return false;
    }
}
