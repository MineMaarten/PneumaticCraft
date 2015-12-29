package pneumaticCraft.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import pneumaticCraft.common.tileentity.TileEntityOmnidirectionalHopper;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;

public class BlockOmnidirectionalHopper extends BlockPneumaticCraftModeled{

    public BlockOmnidirectionalHopper(Material par2Material){
        super(par2Material);
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityOmnidirectionalHopper.class;
    }

    @Override
    public EnumGuiId getGuiID(){
        return EnumGuiId.OMNIDIRECTIONAL_HOPPER;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLiving, ItemStack par6ItemStack){
        ((TileEntityOmnidirectionalHopper)world.getTileEntity(pos)).setDirection(PneumaticCraftUtils.getDirectionFacing(par5EntityLiving, true).getOpposite());
        //TODO 1.8 set up second directional thing
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
    public boolean rotateBlock(World world, EntityPlayer player, BlockPos pos, EnumFacing face){
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof TileEntityOmnidirectionalHopper) {
            TileEntityOmnidirectionalHopper teOh = (TileEntityOmnidirectionalHopper)te;
            if(player != null && player.isSneaking()) {
                //TODO 1.8 finish
                //int newMeta = (world.getBlockMetadata(x, y, pos) + 1) % 6;
                //if(newMeta == teOh.getDirection().ordinal()) newMeta = (newMeta + 1) % 6;
                //world.setBlockMetadataWithNotify(x, y, pos, newMeta, 3);
            } else {
                // int newRotation = (teOh.getDirection().ordinal() + 1) % 6;
                // if(newRotation == world.getBlockMetadata(x, y, pos)) newRotation = (newRotation + 1) % 6;
                //teOh.setDirection(EnumFacing.getFront(newRotation));
            }
            return true;
        }
        return false;
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 origin, Vec3 direction){
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof TileEntityOmnidirectionalHopper) {
            EnumFacing o = ((TileEntityOmnidirectionalHopper)te).getDirection();
            boolean isColliding = false;
            setBlockBounds(o.getFrontOffsetX() == 1 ? 10 / 16F : 0, o.getFrontOffsetY() == 1 ? 10 / 16F : 0, o.getFrontOffsetZ() == 1 ? 10 / 16F : 0, o.getFrontOffsetX() == -1 ? 6 / 16F : 1, o.getFrontOffsetY() == -1 ? 6 / 16F : 1, o.getFrontOffsetZ() == -1 ? 6 / 16F : 1);
            if(super.collisionRayTrace(world, pos, origin, direction) != null) isColliding = true;
            setBlockBounds(4 / 16F, 4 / 16F, 4 / 16F, 12 / 16F, 12 / 16F, 12 / 16F);
            if(super.collisionRayTrace(world, pos, origin, direction) != null) isColliding = true;
            setBlockBounds(0, 0, 0, 1, 1, 1);
            return isColliding ? super.collisionRayTrace(world, pos, origin, direction) : null;
        }
        return null;
    }
}
