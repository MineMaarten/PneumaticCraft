package pneumaticCraft.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.common.tileentity.TileEntityPneumaticDoor;
import pneumaticCraft.common.tileentity.TileEntityPneumaticDoorBase;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;

public class BlockPneumaticDoorBase extends BlockPneumaticCraftModeled{

    public BlockPneumaticDoorBase(Material par2Material){
        super(par2Material);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType(){
        return 0;
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityPneumaticDoorBase.class;
    }

    @Override
    public EnumGuiId getGuiID(){
        return EnumGuiId.PNEUMATIC_DOOR;
    }

    /**
     * Called when the block is placed in the world.
     */
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLiving, ItemStack par6ItemStack){
        TileEntityPneumaticDoorBase doorBase = (TileEntityPneumaticDoorBase)world.getTileEntity(pos);
        doorBase.orientation = PneumaticCraftUtils.getDirectionFacing(par5EntityLiving, false);
        updateDoorSide(doorBase);
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block){
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof TileEntityPneumaticDoorBase) {
            updateDoorSide((TileEntityPneumaticDoorBase)te);
            EnumFacing dir = ((TileEntityPneumaticDoorBase)te).orientation;
            if(world.getBlockState(pos.offset(dir)).getBlock() == Blockss.pneumaticDoor) {
                Blockss.pneumaticDoor.onNeighborBlockChange(world, pos.offset(dir), world.getBlockState(pos.offset(dir)), block);
            }
        }
    }

    private void updateDoorSide(TileEntityPneumaticDoorBase doorBase){
        TileEntity teDoor = doorBase.getWorld().getTileEntity(doorBase.getPos().offset(doorBase.orientation));
        if(teDoor instanceof TileEntityPneumaticDoor) {
            TileEntityPneumaticDoor door = (TileEntityPneumaticDoor)teDoor;
            if(doorBase.orientation.rotateY() == door.getRotation() && door.rightGoing || doorBase.orientation.rotateYCCW() == EnumFacing.getFront(door.getBlockMetadata() % 6) && !door.rightGoing) {
                door.rightGoing = !door.rightGoing;
                door.setRotation(0);
            }
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
    public boolean rotateBlock(World world, EntityPlayer player, BlockPos pos, EnumFacing side){
        if(player.isSneaking()) {
            return super.rotateBlock(world, player, pos, side);
        } else {
            TileEntity te = world.getTileEntity(pos);
            if(te instanceof TileEntityPneumaticDoorBase) {
                TileEntityPneumaticDoorBase teDb = (TileEntityPneumaticDoorBase)te;
                teDb.orientation = teDb.orientation.rotateY();
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side){
        TileEntityPneumaticDoorBase te = (TileEntityPneumaticDoorBase)world.getTileEntity(pos.offset(side, -1));
        ItemStack camoStack = te.getStackInSlot(TileEntityPneumaticDoorBase.CAMO_SLOT);
        if(camoStack != null && camoStack.getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock)camoStack.getItem()).getBlock();
            if(PneumaticCraftUtils.isRenderIDCamo(block.getRenderType())) {
                return true;
            }
        }
        return false;
    }
}
