package pneumaticCraft.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.common.tileentity.TileEntityAerialInterface;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;

public class BlockAerialInterface extends BlockPneumaticCraft{
    public BlockAerialInterface(Material par2Material){
        super(par2Material);
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityAerialInterface.class;
    }

    @Override
    public EnumGuiId getGuiID(){
        return EnumGuiId.AERIAL_INTERFACE;
    }

    @Override
    public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack par6ItemStack){
        TileEntity te = par1World.getTileEntity(pos);
        if(te instanceof TileEntityAerialInterface && entity instanceof EntityPlayer) {
            ((TileEntityAerialInterface)te).setPlayer(((EntityPlayer)entity).getGameProfile());
        }
    }

    /**
     * Called to determine whether to allow the a block to handle its own indirect power rather than using the default rules.
     * @param world The world
     * @param pos Block position in world
     * @param side The INPUT side of the block to be powered - ie the opposite of this block's output side
     * @return Whether Block#getWeakPower should be called when determining indirect power
     */
    @Override
    public boolean shouldCheckWeakPower(IBlockAccess world, BlockPos pos, EnumFacing side){
        return true;
    }

    @Override
    protected int getInventoryDropEndSlot(IInventory inventory){
        return 4;
    }

    /**
     * Produce an peripheral implementation from a block location.
     * @see dan200.computercraft.api.ComputerCraftAPI#registerPeripheralProvider(IPeripheralProvider)
     * @return a peripheral, or null if there is not a peripheral here you'd like to handle.
     */
    /* @Override TODO Computercraft dep
     @Optional.Method(modid = ModIds.COMPUTERCRAFT)
     public IPeripheral getPeripheral(World world, int x, int y, int z, int side){
         return side == 0 || side == 1 ? super.getPeripheral(world, x, y, z, side) : null;
     }*/
}
