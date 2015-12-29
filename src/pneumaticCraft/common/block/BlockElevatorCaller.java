package pneumaticCraft.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.common.tileentity.TileEntityElevatorBase;
import pneumaticCraft.common.tileentity.TileEntityElevatorCaller;
import pneumaticCraft.common.util.PneumaticCraftUtils;

public class BlockElevatorCaller extends BlockPneumaticCraft{

    protected BlockElevatorCaller(Material par2Material){
        super(par2Material);
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityElevatorCaller.class;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
        TileEntityElevatorCaller te = (TileEntityElevatorCaller)world.getTileEntity(pos);
        if(!world.isRemote) {
            MovingObjectPosition mop = PneumaticCraftUtils.getEntityLookedObject(player);
            if(mop != null && mop.subHit >= 0) {
                setSurroundingElevators(world, pos, mop.subHit);
            } else if(player.isSneaking()) {
                te.camoStack = player.getCurrentEquippedItem();
                return te.camoStack != null && te.camoStack.getItem() instanceof ItemBlock;
            }
        }
        return te.getRotation().getOpposite() == side;
    }

    @Override
    public void setBlockBoundsForItemRender(){
        setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, BlockPos pos){
        return true;
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 origin, Vec3 direction){
        setBlockBounds(0, 0, 0, 1, 1, 1);
        MovingObjectPosition rayTrace = super.collisionRayTrace(world, pos, origin, direction);
        EnumFacing orientation = getRotation(world, pos).getOpposite();
        if(rayTrace != null && rayTrace.sideHit == orientation) {
            TileEntity te = world.getTileEntity(pos);
            if(te instanceof TileEntityElevatorCaller) {
                TileEntityElevatorCaller caller = (TileEntityElevatorCaller)te;
                for(TileEntityElevatorCaller.ElevatorButton button : caller.getFloors()) {
                    float startX = 0, startZ = 0, endX = 0, endZ = 0;
                    switch(orientation){
                        case NORTH:
                            startZ = 0F;
                            endZ = 0.01F;
                            endX = 1 - (float)button.posX;
                            startX = 1 - ((float)button.posX + (float)button.width);
                            break;
                        case SOUTH:
                            startZ = 0.99F;
                            endZ = 1F;
                            startX = (float)button.posX;
                            endX = (float)button.posX + (float)button.width;
                            break;
                        case WEST:
                            startX = 0F;
                            endX = 0.01F;
                            startZ = (float)button.posX;
                            endZ = (float)button.posX + (float)button.width;
                            break;
                        case EAST:
                            startX = 0.99F;
                            endX = 1F;
                            endZ = 1 - (float)button.posX;
                            startZ = 1 - ((float)button.posX + (float)button.width);
                            break;
                    }

                    setBlockBounds(startX, 1 - (float)(button.posY + button.height), startZ, endX, 1 - (float)button.posY, endZ);
                    MovingObjectPosition buttonTrace = super.collisionRayTrace(world, pos, origin, direction);
                    if(buttonTrace != null) {
                        if(startX > 0.01F && startX < 0.98F) startX += 0.01F;
                        if(startZ > 0.01F && startZ < 0.98F) startZ += 0.01F;
                        if(endX > 0.02F && endX < 0.99F) endX -= 0.01F;
                        if(endZ > 0.02F && endZ < 0.99F) endZ -= 0.01F;
                        setBlockBounds(startX, 1.01F - (float)(button.posY + button.height), startZ, endX, 0.99F - (float)button.posY, endZ);
                        buttonTrace.subHit = button.floorNumber;
                        return buttonTrace;
                    }
                }
            }
        }

        setBlockBounds(0, 0, 0, 1, 1, 1);
        return rayTrace;
    }

    public static void setSurroundingElevators(World world, BlockPos pos, int floor){
        for(EnumFacing dir : EnumFacing.HORIZONTALS) {
            TileEntityElevatorBase elevator = getElevatorBase(world, pos.offset(dir));
            if(elevator != null) {
                elevator.goToFloor(floor);
            }
        }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state){
        super.onBlockAdded(worldIn, pos, state);
        updateElevatorButtons(worldIn, pos);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state){
        updateElevatorButtons(world, pos);
        super.breakBlock(world, pos, state);
    }

    private void updateElevatorButtons(World world, BlockPos pos){
        for(EnumFacing dir : EnumFacing.VALUES) {
            if(dir.getAxis() != Axis.Y) {
                TileEntityElevatorBase elevator = getElevatorBase(world, pos.offset(dir).offset(EnumFacing.DOWN, 2));
                if(elevator != null) {
                    elevator.updateFloors();
                }
            }
        }
    }

    private static TileEntityElevatorBase getElevatorBase(World world, BlockPos pos){
        Block block = world.getBlockState(pos).getBlock();
        TileEntityElevatorBase elevator = null;
        if(block == Blockss.elevatorFrame) {
            elevator = BlockElevatorFrame.getElevatorTE(world, pos);
        }
        if(block == Blockss.elevatorBase) {
            TileEntity te = world.getTileEntity(pos);
            if(te instanceof TileEntityElevatorBase && ((TileEntityElevatorBase)te).isCoreElevator()) {
                elevator = (TileEntityElevatorBase)te;
            }
        }
        return elevator;
    }

    @Override
    public boolean isRotatable(){
        return true;
    }

    @Override
    public boolean isOpaqueCube(){
        return false;//this should return false, because otherwise I can't give color to the rendered elevator buttons for some reason...
    }

    @Override
    public int getRenderType(){
        setBlockBoundsForItemRender();
        return super.getRenderType();
    }

    @Override
    public boolean canProvidePower(){
        return true;
    }

    /*@Override TODO elevator camo
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
        TileEntityElevatorCaller te = (TileEntityElevatorCaller)world.getTileEntity(x, y, z);
        if(te.camoBlock != null && PneumaticCraftUtils.isRenderIDCamo(te.camoBlock.getRenderType())) {
            return te.camoBlock.getIcon(side, te.camoStack.getItemDamage());
        }
        return getIcon(side, world.getBlockMetadata(x, y, z));
    }*/

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
        if(te instanceof TileEntityElevatorCaller) {
            TileEntityElevatorCaller teEc = (TileEntityElevatorCaller)te;
            return teEc.getEmittingRedstone() ? 15 : 0;
        }

        return 0;
    }
}
