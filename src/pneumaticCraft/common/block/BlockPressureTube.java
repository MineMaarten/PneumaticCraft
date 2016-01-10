package pneumaticCraft.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

import pneumaticCraft.common.block.tubes.ModuleRegistrator;
import pneumaticCraft.common.block.tubes.TubeModule;
import pneumaticCraft.common.item.ItemTubeModule;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.thirdparty.ModInteractionUtils;
import pneumaticCraft.common.tileentity.TileEntityPressureTube;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.lib.BBConstants;
import pneumaticCraft.lib.PneumaticValues;

public class BlockPressureTube extends BlockPneumaticCraftModeled{

    public AxisAlignedBB[] boundingBoxes = new AxisAlignedBB[6];
    private final float dangerPressure, criticalPressure;
    private final int volume;
    private static final Object CENTER_TUBE_HIT_MARKER = new Object(); //Object that is assigned to a MOP's hitInfo when a player hovers over the center part of a tube.

    public BlockPressureTube(Material par2Material, float dangerPressure, float criticalPressure, int volume){
        super(par2Material);

        double width = (BBConstants.PRESSURE_PIPE_MAX_POS - BBConstants.PRESSURE_PIPE_MIN_POS) / 2;
        double height = BBConstants.PRESSURE_PIPE_MIN_POS;

        boundingBoxes[0] = new AxisAlignedBB(0.5 - width, BBConstants.PRESSURE_PIPE_MIN_POS - height, 0.5 - width, 0.5 + width, BBConstants.PRESSURE_PIPE_MIN_POS, 0.5 + width);
        boundingBoxes[1] = new AxisAlignedBB(0.5 - width, BBConstants.PRESSURE_PIPE_MAX_POS, 0.5 - width, 0.5 + width, BBConstants.PRESSURE_PIPE_MAX_POS + height, 0.5 + width);
        boundingBoxes[2] = new AxisAlignedBB(0.5 - width, 0.5 - width, BBConstants.PRESSURE_PIPE_MIN_POS - height, 0.5 + width, 0.5 + width, BBConstants.PRESSURE_PIPE_MIN_POS);
        boundingBoxes[3] = new AxisAlignedBB(0.5 - width, 0.5 - width, BBConstants.PRESSURE_PIPE_MAX_POS, 0.5 + width, 0.5 + width, BBConstants.PRESSURE_PIPE_MAX_POS + height);
        boundingBoxes[4] = new AxisAlignedBB(BBConstants.PRESSURE_PIPE_MIN_POS - height, 0.5 - width, 0.5 - width, BBConstants.PRESSURE_PIPE_MIN_POS, 0.5 + width, 0.5 + width);
        boundingBoxes[5] = new AxisAlignedBB(BBConstants.PRESSURE_PIPE_MAX_POS, 0.5 - width, 0.5 - width, BBConstants.PRESSURE_PIPE_MAX_POS + height, 0.5 + width, 0.5 + width);

        this.dangerPressure = dangerPressure;
        this.criticalPressure = criticalPressure;
        this.volume = volume;
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityPressureTube.class;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata){
        return new TileEntityPressureTube(dangerPressure, criticalPressure, volume);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float par7, float par8, float par9){
        if(!world.isRemote) {
            if(tryPlaceModule(player, world, pos, side, false)) return true;
        }
        if(!player.isSneaking()) {
            TubeModule module = getLookedModule(world, pos, player);
            if(module != null) {
                return module.onActivated(player);
            }
        }
        return false;
    }

    public boolean tryPlaceModule(EntityPlayer player, World world, BlockPos pos, EnumFacing side, boolean simulate){
        if(player.getCurrentEquippedItem() != null) {
            if(player.getCurrentEquippedItem().getItem() instanceof ItemTubeModule) {
                TileEntityPressureTube pressureTube = ModInteractionUtils.getInstance().getTube(world.getTileEntity(pos));
                if(pressureTube.modules[side.ordinal()] == null && ModInteractionUtils.getInstance().occlusionTest(boundingBoxes[side.ordinal()], world.getTileEntity(pos))) {
                    TubeModule module = ModuleRegistrator.getModule(((ItemTubeModule)player.getCurrentEquippedItem().getItem()).moduleName);
                    if(simulate) module.markFake();
                    pressureTube.setModule(module, side);
                    if(!simulate) {
                        onNeighborBlockChange(world, pos, world.getBlockState(pos), this);
                        world.notifyNeighborsOfStateChange(pos, this);
                        if(!player.capabilities.isCreativeMode) player.getCurrentEquippedItem().stackSize--;
                        world.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, Block.soundTypeGlass.getStepSound(), Block.soundTypeGlass.getVolume() * 5.0F, Block.soundTypeGlass.getFrequency() * .9F);
                    }
                    return true;
                }
            } else if(player.getCurrentEquippedItem().getItem() == Itemss.advancedPCB && !simulate) {
                TubeModule module = BlockPressureTube.getLookedModule(world, pos, player);
                if(module != null && !module.isUpgraded() && module.canUpgrade()) {
                    if(!world.isRemote) {
                        module.upgrade();
                        if(!player.capabilities.isCreativeMode) player.getCurrentEquippedItem().stackSize--;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static TubeModule getLookedModule(World world, BlockPos pos, EntityPlayer player){
        Pair<Vec3, Vec3> vecs = PneumaticCraftUtils.getStartAndEndLookVec(player);
        MovingObjectPosition mop = Blockss.pressureTube.collisionRayTrace(world, pos, vecs.getLeft(), vecs.getRight());
        if(mop != null && mop.hitInfo instanceof EnumFacing) {
            TileEntityPressureTube tube = ModInteractionUtils.getInstance().getTube(world.getTileEntity(pos));
            return tube.modules[((EnumFacing)mop.hitInfo).ordinal()];
        }
        return null;
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 origin, Vec3 direction){
        MovingObjectPosition bestMOP = null;
        AxisAlignedBB bestAABB = null;

        setBlockBounds(BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MAX_POS, BBConstants.PRESSURE_PIPE_MAX_POS, BBConstants.PRESSURE_PIPE_MAX_POS);
        MovingObjectPosition mop = super.collisionRayTrace(world, pos, origin, direction);
        if(isCloserMOP(origin, bestMOP, mop)) {
            bestMOP = mop;
            bestAABB = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
        }

        TileEntityPressureTube tube = ModInteractionUtils.getInstance().getTube(world.getTileEntity(pos));
        for(int i = 0; i < 6; i++) {
            if(tube.sidesConnected[i]) {
                setBlockBounds(boundingBoxes[i]);
                mop = super.collisionRayTrace(world, pos, origin, direction);
                if(isCloserMOP(origin, bestMOP, mop)) {
                    bestMOP = mop;
                    bestAABB = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
                }
            }
        }

        if(bestMOP != null) bestMOP.hitInfo = CENTER_TUBE_HIT_MARKER;

        TubeModule[] modules = tube.modules;
        for(EnumFacing dir : EnumFacing.VALUES) {
            if(modules[dir.ordinal()] != null) {
                setBlockBounds(modules[dir.ordinal()].boundingBoxes[dir.ordinal()]);
                mop = super.collisionRayTrace(world, pos, origin, direction);
                if(isCloserMOP(origin, bestMOP, mop)) {
                    mop.hitInfo = dir;
                    bestMOP = mop;
                    bestAABB = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
                }
            }
        }
        if(bestAABB != null) setBlockBounds(bestAABB);
        return bestMOP;
    }

    private boolean isCloserMOP(Vec3 origin, MovingObjectPosition originalMOP, MovingObjectPosition newMOP){
        if(newMOP == null) return false;
        if(originalMOP == null) return true;
        return PneumaticCraftUtils.distBetween(origin, newMOP.hitVec) < PneumaticCraftUtils.distBetween(origin, originalMOP.hitVec);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player){
        if(target.hitInfo == CENTER_TUBE_HIT_MARKER) {
            return super.getPickBlock(target, world, pos, player);
        } else {
            TileEntityPressureTube tube = (TileEntityPressureTube)world.getTileEntity(pos);
            return new ItemStack(ModuleRegistrator.getModuleItem(tube.modules[((EnumFacing)target.hitInfo).ordinal()].getType()));
        }
    }

    private void setBlockBounds(AxisAlignedBB aabb){
        this.setBlockBounds((float)aabb.minX, (float)aabb.minY, (float)aabb.minZ, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.maxZ);
    }

    @Override
    public boolean rotateBlock(World world, EntityPlayer player, BlockPos pos, EnumFacing side){
        TileEntityPressureTube tube = ModInteractionUtils.getInstance().getTube(world.getTileEntity(pos));
        if(player.isSneaking()) {
            TubeModule module = getLookedModule(world, pos, player);
            if(module != null) {
                if(!player.capabilities.isCreativeMode) {
                    List<ItemStack> drops = module.getDrops();
                    for(ItemStack drop : drops) {
                        EntityItem entity = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                        entity.setEntityItemStack(drop);
                        world.spawnEntityInWorld(entity);
                        entity.onCollideWithPlayer(player);
                    }
                }
                tube.setModule(null, module.getDirection());
                onNeighborBlockChange(world, pos, world.getBlockState(pos), this);
                world.notifyNeighborsOfStateChange(pos, this);
                return true;
            }
            if(!player.capabilities.isCreativeMode) {
                EntityItem entity = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(tube.criticalPressure <= PneumaticValues.MAX_PRESSURE_PRESSURE_TUBE ? Blockss.pressureTube : Blockss.advancedPressureTube));
                world.spawnEntityInWorld(entity);
                entity.onCollideWithPlayer(player);
            }
            ModInteractionUtils.getInstance().removeTube(world.getTileEntity(pos));
            return true;
        } else {
            return super.rotateBlock(world, player, pos, side);
        }

    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state){
        List<ItemStack> drops = getModuleDrops((TileEntityPressureTube)world.getTileEntity(pos));
        for(ItemStack drop : drops) {
            EntityItem entity = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            entity.setEntityItemStack(drop);
            world.spawnEntityInWorld(entity);
        }
        super.breakBlock(world, pos, state);
    }

    public static List<ItemStack> getModuleDrops(TileEntityPressureTube tube){
        List<ItemStack> drops = new ArrayList<ItemStack>();
        for(TubeModule module : tube.modules) {
            if(module != null) {
                drops.addAll(module.getDrops());
            }
        }
        return drops;
    }

    @Override
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB axisalignedbb, List arraylist, Entity par7Entity){
        setBlockBounds(BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MAX_POS, BBConstants.PRESSURE_PIPE_MAX_POS, BBConstants.PRESSURE_PIPE_MAX_POS);
        super.addCollisionBoxesToList(world, pos, state, axisalignedbb, arraylist, par7Entity);

        TileEntity te = world.getTileEntity(pos);
        TileEntityPressureTube tePt = (TileEntityPressureTube)te;

        for(int i = 0; i < 6; i++) {
            if(tePt.sidesConnected[i]) {
                setBlockBounds(boundingBoxes[i]);
                super.addCollisionBoxesToList(world, pos, state, axisalignedbb, arraylist, par7Entity);
            } else if(tePt.modules[i] != null) {
                setBlockBounds(tePt.modules[i].boundingBoxes[i]);
                super.addCollisionBoxesToList(world, pos, state, axisalignedbb, arraylist, par7Entity);
            }
        }
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    //TODO 1.8 verify not necessary
    /* @Override
     public void onBlockAdded(World world, BlockPos pos, IBlockState state){
         super.onBlockAdded(world, pos, state);
         TileEntity te = world.getTileEntity(pos);
         if(te != null && te instanceof TileEntityPressureTube) {
             TileEntityPressureTube tePt = (TileEntityPressureTube)te;
             tePt.updateConnections(world, x, y, z);
         }
     }*/

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, BlockPos pos, IBlockState state, Random par5Random){
        TileEntity te = par1World.getTileEntity(pos);
        if(te instanceof TileEntityPressureTube) {
            TileEntityPressureTube tePt = (TileEntityPressureTube)te;
            int l = 0;
            for(TubeModule module : tePt.modules)
                if(module != null) l = Math.max(l, module.getRedstoneLevel());
            if(l > 0) {
                // for(int i = 0; i < 4; i++){
                double d0 = pos.getX() + 0.5D + (par5Random.nextFloat() - 0.5D) * 0.5D;
                double d1 = pos.getY() + 0.5D + (par5Random.nextFloat() - 0.5D) * 0.5D;
                double d2 = pos.getZ() + 0.5D + (par5Random.nextFloat() - 0.5D) * 0.5D;
                float f = l / 15.0F;
                float f1 = f * 0.6F + 0.4F;
                float f2 = f * f * 0.7F - 0.5F;
                float f3 = f * f * 0.6F - 0.7F;
                if(f2 < 0.0F) {
                    f2 = 0.0F;
                }

                if(f3 < 0.0F) {
                    f3 = 0.0F;
                }
                // PacketDispatcher.sendPacketToAllPlayers(PacketHandlerPneumaticCraft.spawnParticle("reddust",
                // d0, d1, d2, (double)f1, (double)f2, (double)f3));
                par1World.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1, d2, f1, f2, f3);
                // }
            }
        }

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
        if(te instanceof TileEntityPressureTube) {
            TileEntityPressureTube tePt = (TileEntityPressureTube)te;
            int redstoneLevel = 0;
            for(EnumFacing face : EnumFacing.VALUES) {
                if(tePt.modules[face.ordinal()] != null) {
                    if(side.getOpposite() == face || face != side && tePt.modules[face.ordinal()].isInline()) {//if we are on the same side, or when we have an 'in line' module that is not on the opposite side.
                        redstoneLevel = Math.max(redstoneLevel, tePt.modules[face.ordinal()].getRedstoneLevel());
                    }
                }
            }
            return redstoneLevel;
        }
        return 0;
    }

    /**
     * Determine if this block can make a redstone connection on the side provided,
     * Useful to control which sides are inputs and outputs for redstone wires.
     *
     * Side:
     *  -1: UP
     *   0: NORTH
     *   1: EAST
     *   2: SOUTH
     *   3: WEST
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param side The side that is trying to make the connection
     * @return True to make the connection
     */
    /* @Override
     * TODO 1.8
     public boolean canConnectRedstone(IBlockAccess world, BlockPos pos, EnumFacing side){
         if(side < 0 || side > 3) return false;
         TileEntityPressureTube tube = (TileEntityPressureTube)world.getTileEntity(x, y, z);
         EnumFacing d = EnumFacing.NORTH;
         for(int i = 0; i < side; i++) {
             d = d.getRotation(EnumFacing.UP);
         }
         side = d.ordinal();
         for(int i = 0; i < 6; i++) {
             if(tube.modules[i] != null) {
                 if((side ^ 1) == i || i != side && tube.modules[i].isInline()) {//if we are on the same side, or when we have an 'in line' module that is not on the opposite side.
                     if(tube.modules[i] instanceof TubeModuleRedstoneEmitting) return true;
                 }
             }
         }
         return false;
     }*/

}
