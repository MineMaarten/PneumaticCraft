package pneumaticCraft.common.thirdparty.mcmultipart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import mcmultipart.MCMultiPartMod;
import mcmultipart.microblock.ISideHollowConnect;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IRedstonePart;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.Multipart;
import mcmultipart.multipart.OcclusionHelper;
import mcmultipart.multipart.PartSlot;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import pneumaticCraft.api.block.IPneumaticWrenchable;
import pneumaticCraft.api.tileentity.IAirHandler;
import pneumaticCraft.api.tileentity.IPneumaticMachine;
import pneumaticCraft.common.block.BlockPressureTube;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.block.tubes.TubeModule;
import pneumaticCraft.common.block.tubes.TubeModuleRedstoneEmitting;
import pneumaticCraft.common.tileentity.TileEntityPressureTube;
import pneumaticCraft.lib.BBConstants;
import pneumaticCraft.lib.Names;

public class PartPressureTube extends Multipart implements ITickable, IPneumaticMachine, IPneumaticWrenchable,
        ISlottedPart, ISideHollowConnect, IRedstonePart{

    private static final AxisAlignedBB[] BOUNDING_BOXES = new AxisAlignedBB[7];
    static {
        BOUNDING_BOXES[0] = new AxisAlignedBB(BBConstants.PRESSURE_PIPE_MIN_POS, 0.0F, BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MAX_POS, BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MAX_POS);
        BOUNDING_BOXES[1] = new AxisAlignedBB(BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MAX_POS, BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MAX_POS, 1.0F, BBConstants.PRESSURE_PIPE_MAX_POS);
        BOUNDING_BOXES[2] = new AxisAlignedBB(BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MIN_POS, 0.0F, BBConstants.PRESSURE_PIPE_MAX_POS, BBConstants.PRESSURE_PIPE_MAX_POS, BBConstants.PRESSURE_PIPE_MIN_POS);
        BOUNDING_BOXES[3] = new AxisAlignedBB(BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MAX_POS, BBConstants.PRESSURE_PIPE_MAX_POS, BBConstants.PRESSURE_PIPE_MAX_POS, 1.0F);
        BOUNDING_BOXES[4] = new AxisAlignedBB(0.0F, BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MAX_POS, BBConstants.PRESSURE_PIPE_MAX_POS);
        BOUNDING_BOXES[5] = new AxisAlignedBB(BBConstants.PRESSURE_PIPE_MAX_POS, BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MIN_POS, 1.0F, BBConstants.PRESSURE_PIPE_MAX_POS, BBConstants.PRESSURE_PIPE_MAX_POS);
        BOUNDING_BOXES[6] = new AxisAlignedBB(BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MIN_POS, BBConstants.PRESSURE_PIPE_MAX_POS, BBConstants.PRESSURE_PIPE_MAX_POS, BBConstants.PRESSURE_PIPE_MAX_POS);
    }

    private final TileEntityPressureTube tube = getNewTube().setPart(this);
    private boolean validated;

    protected TileEntityPressureTube getNewTube(){
        return new TileEntityPressureTube();
    }

    public TileEntityPressureTube getTube(){
        if(!validated) {
            validated = true;
            tube.setWorldObj(getWorld());
            tube.setPos(getPos());
            tube.validate();
        }
        return tube;
    }

    @Override
    public IAirHandler getAirHandler(EnumFacing side){
        return getTube().getAirHandler(side);
    }

    @Override
    public void update(){
        getTube().update();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        tube.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        tube.readFromNBT(tag);
    }

    @Override
    public void onPartChanged(IMultipart part){
        super.onPartChanged(part);
        getTube().onNeighborTileUpdate();
    }

    @Override
    public void onNeighborBlockChange(Block block){
        super.onNeighborBlockChange(block);
        getTube().onNeighborTileUpdate();
    }

    @Override
    public void onNeighborTileChange(EnumFacing facing){
        super.onNeighborTileChange(facing);
        getTube().onNeighborTileUpdate();
    }

    @Override
    public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> boxes, Entity collidingEntity){
        super.addCollisionBoxes(mask, boxes, collidingEntity);
        if(mask.intersectsWith(BOUNDING_BOXES[6])) boxes.add(BOUNDING_BOXES[6]);
        for(int i = 0; i < 6; i++) {
            if(tube.sidesConnected[i]) if(mask.intersectsWith(BOUNDING_BOXES[i])) boxes.add(BOUNDING_BOXES[i]);
            if(tube.modules[i] != null) if(mask.intersectsWith(tube.modules[i].boundingBoxes[i])) boxes.add(tube.modules[i].boundingBoxes[i]);
        }
    }

    @Override
    public void addSelectionBoxes(List<AxisAlignedBB> list){
        addCollisionBoxes(new AxisAlignedBB(0, 0, 0, 1, 1, 1), list, null);
    }

    public boolean passesOcclusionTest(EnumFacing side){
        TubeModule[] modules = tube.modules;
        tube.modules = new TubeModule[6];
        boolean result = getContainer() != null && OcclusionHelper.occlusionTest(getContainer().getParts(), BOUNDING_BOXES[side.ordinal()]);
        tube.modules = modules;
        return result;
    }

    @Override
    public boolean canConnectRedstone(EnumFacing side){
        side = side.getOpposite();
        for(EnumFacing d : EnumFacing.VALUES) {
            if(tube.modules[d.ordinal()] != null) {
                if(side.getOpposite() == d || d != side && tube.modules[d.ordinal()].isInline()) {//if we are on the same side, or when we have an 'in line' module that is not on the opposite side.
                    if(tube.modules[d.ordinal()] instanceof TubeModuleRedstoneEmitting) return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getWeakSignal(EnumFacing side){
        side = side.getOpposite();
        int redstoneLevel = 0;
        for(EnumFacing d : EnumFacing.VALUES) {
            if(tube.modules[d.ordinal()] != null) {
                if(side.getOpposite() == d || d != side && tube.modules[d.ordinal()].isInline()) {//if we are on the same side, or when we have an 'in line' module that is not on the opposite side.
                    redstoneLevel = Math.max(redstoneLevel, tube.modules[d.ordinal()].getRedstoneLevel());
                }
            }
        }
        return redstoneLevel;
    }

    @Override
    public int getStrongSignal(EnumFacing side){
        return 0;
    }

    @Override
    public int getHollowSize(EnumFacing side){
        if(tube.modules[side.ordinal()] != null) {
            return Math.min(12, (int)(tube.modules[side.ordinal()].getWidth() * 16));
        }
        return 4;
    }

    @Override
    public EnumSet<PartSlot> getSlotMask(){
        return EnumSet.of(PartSlot.CENTER);
    }

    @Override
    public boolean rotateBlock(World world, EntityPlayer player, BlockPos pos, EnumFacing side){
        return ((IPneumaticWrenchable)Blockss.pressureTube).rotateBlock(world, player, pos, side);
    }

    @Override
    public void writeUpdatePacket(PacketBuffer buf){
        super.writeUpdatePacket(buf);
        for(int i = 0; i < 6; i++) {
            buf.writeBoolean(tube.sidesConnected[i]);
        }
        NBTTagCompound tag = new NBTTagCompound();
        tube.writeToNBT(tag);
        buf.writeNBTTagCompoundToBuffer(tag);
    }

    @Override
    public void readUpdatePacket(PacketBuffer buf){
        for(int i = 0; i < 6; i++) {
            tube.sidesConnected[i] = buf.readBoolean();
        }
        try {
            tube.readFromNBT(buf.readNBTTagCompoundFromBuffer());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBlockState getExtendedState(IBlockState state){
        state = super.getExtendedState(state);
        for(int i = 0; i < 6; i++) {
            state = state.withProperty(BlockPressureTube.CONNECTION_PROPERTIES[i], tube.sidesConnected[i]);
        }

        return state;
    }

    @Override
    public BlockState createBlockState(){
        return new BlockState(MCMultiPartMod.multipart, Arrays.copyOf(BlockPressureTube.CONNECTION_PROPERTIES, BlockPressureTube.CONNECTION_PROPERTIES.length));
    }

    @Override
    public String getModelPath(){
        return Names.MOD_ID + ":" + getType();
    }

    @Override
    public ItemStack getPickBlock(EntityPlayer player, PartMOP hit){
        return new ItemStack(Blockss.pressureTube);
    }

    @Override
    public List<ItemStack> getDrops(){
        List<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(new ItemStack(Blockss.pressureTube));
        return drops;
    }

    @Override
    public float getHardness(PartMOP hit){
        return Blockss.pressureTube.getBlockHardness(null, null);
    }
}
