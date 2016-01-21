package pneumaticCraft.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.common.tileentity.TileEntityOmnidirectionalHopper;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;

public class BlockOmnidirectionalHopper extends BlockPneumaticCraftModeled{

    public static final PropertyEnum<EnumFacing> INPUT = PropertyEnum.<EnumFacing> create("input", EnumFacing.class);

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
    protected BlockState createBlockState(){
        return new BlockState(this, ROTATION, INPUT);
    }

    @Override
    public IBlockState getStateFromMeta(int meta){
        return super.getStateFromMeta(meta).withProperty(INPUT, EnumFacing.VALUES[meta / 6 % 6]);
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return 0;//super.getMetaFromState(state) + state.getValue(OUTPUT).ordinal() * 6;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos){
        state = super.getActualState(state, worldIn, pos);
        TileEntityOmnidirectionalHopper te = (TileEntityOmnidirectionalHopper)worldIn.getTileEntity(pos);
        return state.withProperty(INPUT, te.getDirection()).withProperty(ROTATION, te.getRotation());
    }

    @Override
    protected EnumFacing getRotation(IBlockAccess world, BlockPos pos){
        TileEntityOmnidirectionalHopper hopper = (TileEntityOmnidirectionalHopper)world.getTileEntity(pos);
        return hopper.getRotation();
    }

    @Override
    protected void setRotation(World world, BlockPos pos, EnumFacing rotation){
        TileEntityOmnidirectionalHopper hopper = (TileEntityOmnidirectionalHopper)world.getTileEntity(pos);
        hopper.setRotation(rotation);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLiving, ItemStack par6ItemStack){
        TileEntityOmnidirectionalHopper hopper = (TileEntityOmnidirectionalHopper)world.getTileEntity(pos);
        hopper.setDirection(PneumaticCraftUtils.getDirectionFacing(par5EntityLiving, true).getOpposite());
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
                EnumFacing rotation = getRotation(world, pos);
                rotation = EnumFacing.getFront(rotation.ordinal() + 1);
                if(rotation == teOh.getDirection()) rotation = EnumFacing.getFront(rotation.ordinal() + 1);
                setRotation(world, pos, rotation);
            } else {
                EnumFacing rotation = teOh.getDirection();
                rotation = EnumFacing.getFront(rotation.ordinal() + 1);
                if(rotation == getRotation(world, pos)) rotation = EnumFacing.getFront(rotation.ordinal() + 1);
                teOh.setDirection(rotation);
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
