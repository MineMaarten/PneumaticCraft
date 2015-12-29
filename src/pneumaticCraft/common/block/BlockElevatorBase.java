package pneumaticCraft.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import pneumaticCraft.common.tileentity.TileEntityElevatorBase;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;

public class BlockElevatorBase extends BlockPneumaticCraftModeled{

    public BlockElevatorBase(Material par2Material){
        super(par2Material);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state){
        super.onBlockAdded(world, pos, state);
        TileEntityElevatorBase elevatorBase = getCoreTileEntity(world, pos);
        if(elevatorBase != null) {
            elevatorBase.updateMaxElevatorHeight();
        }
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityElevatorBase.class;
    }

    @Override
    public EnumGuiId getGuiID(){
        return EnumGuiId.ELEVATOR;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float par7, float par8, float par9){
        return super.onBlockActivated(world, getCoreElevatorPos(world, pos), state, player, side, par7, par8, par9);
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block){
        super.onNeighborBlockChange(world, pos, state, block);
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof TileEntityElevatorBase) {
            TileEntityElevatorBase thisTe = (TileEntityElevatorBase)te;
            if(thisTe.isCoreElevator()) {
                TileEntityElevatorBase teAbove = getCoreTileEntity(world, pos);
                if(teAbove != null && teAbove != thisTe) {
                    for(int i = 0; i < thisTe.getSizeInventory(); i++) {
                        ItemStack item = thisTe.getStackInSlot(i);
                        if(item != null) {
                            ItemStack leftover = PneumaticCraftUtils.exportStackToInventory((IInventory)teAbove, item, null);
                            thisTe.setInventorySlotContents(i, null);
                            if(leftover != null) {
                                EntityItem entity = new EntityItem(world, teAbove.getPos().getX() + 0.5, teAbove.getPos().getY() + 1.5, teAbove.getPos().getZ() + 0.5, leftover);
                                world.spawnEntityInWorld(entity);
                            }
                        }
                    }
                }
            }
        }
    }

    public static BlockPos getCoreElevatorPos(World world, BlockPos pos){

        if(world.getBlockState(pos.offset(EnumFacing.UP)).getBlock() == Blockss.elevatorBase) {
            return getCoreElevatorPos(world, pos.offset(EnumFacing.UP));
        } else {
            return pos;
        }
    }

    public static TileEntityElevatorBase getCoreTileEntity(World world, BlockPos pos){
        return (TileEntityElevatorBase)world.getTileEntity(getCoreElevatorPos(world, pos));
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state){
        if(world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() == Blockss.elevatorBase) {
            TileEntity te = world.getTileEntity(pos.offset(EnumFacing.DOWN));
            ((TileEntityElevatorBase)te).moveInventoryToThis();
        }
        TileEntityElevatorBase elevatorBase = getCoreTileEntity(world, pos);
        if(elevatorBase != null) {
            elevatorBase.updateMaxElevatorHeight();
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    protected void dropInventory(World world, BlockPos pos){

        TileEntity tileEntity = world.getTileEntity(pos);

        if(!(tileEntity instanceof TileEntityElevatorBase)) return;

        TileEntityElevatorBase inventory = (TileEntityElevatorBase)tileEntity;
        Random rand = new Random();
        for(int i = getInventoryDropStartSlot(inventory); i < getInventoryDropEndSlot(inventory); i++) {

            ItemStack itemStack = inventory.getRealStackInSlot(i);

            if(itemStack != null && itemStack.stackSize > 0) {
                float dX = rand.nextFloat() * 0.8F + 0.1F;
                float dY = rand.nextFloat() * 0.8F + 0.1F;
                float dZ = rand.nextFloat() * 0.8F + 0.1F;

                EntityItem entityItem = new EntityItem(world, pos.getX() + dX, pos.getY() + dY, pos.getZ() + dZ, new ItemStack(itemStack.getItem(), itemStack.stackSize, itemStack.getItemDamage()));

                if(itemStack.hasTagCompound()) {
                    entityItem.getEntityItem().setTagCompound((NBTTagCompound)itemStack.getTagCompound().copy());
                }

                float factor = 0.05F;
                entityItem.motionX = rand.nextGaussian() * factor;
                entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
                entityItem.motionZ = rand.nextGaussian() * factor;
                world.spawnEntityInWorld(entityItem);
                itemStack.stackSize = 0;
            }
        }
    }
}
