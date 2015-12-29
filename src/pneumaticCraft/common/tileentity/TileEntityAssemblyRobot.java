package pneumaticCraft.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.common.network.DescSynced;
import pneumaticCraft.common.network.LazySynced;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.lib.TileEntityConstants;

public abstract class TileEntityAssemblyRobot extends TileEntityBase implements IAssemblyMachine, IResettable{
    public float[] oldAngles = new float[5];
    @DescSynced
    @LazySynced
    public float[] angles = new float[5];
    @DescSynced
    public float[] targetAngles = new float[5];
    public EnumFacing[] targetDirection = new EnumFacing[]{null, null};
    @DescSynced
    public boolean slowMode; //used for the drill when drilling, the slowmode moves the arm 10x as slow as normal.
    @DescSynced
    protected float speed = 1.0F;

    protected enum EnumAngles{
        TURN, BASE, MIDDLE, TAIL, HEAD
    }

    public TileEntityAssemblyRobot(){
        gotoHomePosition();
        for(int i = 0; i < 5; i++) {
            angles[i] = targetAngles[i];
            oldAngles[i] = targetAngles[i];
        }
    }

    @Override
    public void update(){
        super.update();
        //set the old angles to the last tick calculated angles (used in rendering)
        // while(isDone()) {
        // gotoNeighbour(ForgeDirection.SOUTH, ForgeDirection.EAST);
        //     if(!isDone()) break;
        //     gotoHomePosition();
        // 

        for(int i = 0; i < 5; i++) {
            oldAngles[i] = angles[i];
        }
        //move the arms and claw more to their destination
        for(int i = 0; i < 5; i++) {
            if(angles[i] > targetAngles[i]) {
                angles[i] = Math.max(angles[i] - TileEntityConstants.ASSEMBLY_IO_UNIT_ARM_SPEED * (slowMode ? 0.1F : 1) * speed, targetAngles[i]);
            } else if(angles[i] < targetAngles[i]) {
                angles[i] = Math.min(angles[i] + TileEntityConstants.ASSEMBLY_IO_UNIT_ARM_SPEED * (slowMode ? 0.1F : 1) * speed, targetAngles[i]);
            }
        }
    }

    public void gotoHomePosition(){
        targetAngles[EnumAngles.TURN.ordinal()] = 0F;
        targetAngles[EnumAngles.BASE.ordinal()] = 0F;
        targetAngles[EnumAngles.MIDDLE.ordinal()] = 55F;
        targetAngles[EnumAngles.TAIL.ordinal()] = 35F;
        targetAngles[EnumAngles.HEAD.ordinal()] = 0F;
    }

    public boolean gotoTarget(){
        if(targetDirection == null) return false;

        this.gotoNeighbour(targetDirection[0], targetDirection[1]);
        return isDoneMoving();
    }

    public void gotoNeighbour(EnumFacing direction){
        gotoNeighbour(direction, null);
    }

    /**
     * Goes to the neighbour, and returns true if the neighbour was diagonal to this arm.
     * @param primaryDir
     * @param secondaryDir
     * @return
     */
    @SuppressWarnings("incomplete-switch")
    public boolean gotoNeighbour(EnumFacing primaryDir, EnumFacing secondaryDir){
        targetDirection = new EnumFacing[]{primaryDir, secondaryDir};
        boolean diagonal = true;
        boolean diagonalAllowed = canMoveToDiagonalNeighbours();
        switch(primaryDir){
            case SOUTH:
                if(secondaryDir == EnumFacing.EAST && diagonalAllowed) {
                    targetAngles[EnumAngles.TURN.ordinal()] = -45F;
                    targetAngles[EnumAngles.HEAD.ordinal()] = 40F;
                } else if(secondaryDir == EnumFacing.WEST && diagonalAllowed) {
                    targetAngles[EnumAngles.TURN.ordinal()] = 45F;
                    targetAngles[EnumAngles.HEAD.ordinal()] = -40F;
                } else {
                    targetAngles[EnumAngles.TURN.ordinal()] = 0F;
                    targetAngles[EnumAngles.HEAD.ordinal()] = 90F;
                    diagonal = false;
                }
                break;
            case EAST:
                targetAngles[EnumAngles.TURN.ordinal()] = -90F;
                targetAngles[EnumAngles.HEAD.ordinal()] = 0F;
                diagonal = false;
                break;
            case NORTH:
                if(secondaryDir == EnumFacing.EAST && diagonalAllowed) {
                    targetAngles[EnumAngles.TURN.ordinal()] = -135F;
                    targetAngles[EnumAngles.HEAD.ordinal()] = -40F;
                } else if(secondaryDir == EnumFacing.WEST && diagonalAllowed) {
                    targetAngles[EnumAngles.TURN.ordinal()] = 135F;
                    targetAngles[EnumAngles.HEAD.ordinal()] = 40F;
                } else {
                    targetAngles[EnumAngles.TURN.ordinal()] = 180F;
                    targetAngles[EnumAngles.HEAD.ordinal()] = 90F;
                    diagonal = false;
                }
                break;
            case WEST:
                targetAngles[EnumAngles.TURN.ordinal()] = 90F;
                targetAngles[EnumAngles.HEAD.ordinal()] = 0F;
                diagonal = false;
                break;
        }
        if(diagonal) {
            targetAngles[EnumAngles.BASE.ordinal()] = 160F;
            targetAngles[EnumAngles.MIDDLE.ordinal()] = -85F;
            targetAngles[EnumAngles.TAIL.ordinal()] = -20F;
        } else {
            targetAngles[EnumAngles.BASE.ordinal()] = 100F;
            targetAngles[EnumAngles.MIDDLE.ordinal()] = -10F;
            targetAngles[EnumAngles.TAIL.ordinal()] = 0F;
        }
        return diagonal;
    }

    public boolean hoverOverTarget(){
        if(targetDirection == null) return false;

        return this.hoverOverNeighbour(targetDirection);
    }

    public boolean hoverOverNeighbour(EnumFacing[] directions){
        hoverOverNeighbour(directions[0], directions[1]);
        return isDoneMoving();
    }

    public void hoverOverNeighbour(EnumFacing primaryDir, EnumFacing secondaryDir){
        boolean diagonal = gotoNeighbour(primaryDir, secondaryDir);
        if(diagonal) {
            targetAngles[EnumAngles.BASE.ordinal()] = 160F;
            targetAngles[EnumAngles.MIDDLE.ordinal()] = -95F;
            targetAngles[EnumAngles.TAIL.ordinal()] = -10F;
        } else {
            targetAngles[EnumAngles.BASE.ordinal()] = 100F;
            targetAngles[EnumAngles.MIDDLE.ordinal()] = -20F;
            targetAngles[EnumAngles.TAIL.ordinal()] = 10F;
        }
    }

    public TileEntity getTileEntityForCurrentDirection(){
        return getTileEntityForDirection(targetDirection[0], targetDirection[1]);
    }

    public TileEntity getTileEntityForDirection(EnumFacing[] directions){
        return getTileEntityForDirection(directions[0], directions[1]);
    }

    public TileEntity getTileEntityForDirection(EnumFacing firstDir, EnumFacing secondDir){
        return worldObj.getTileEntity(getPos().offset(firstDir).offset(secondDir, secondDir != null ? 1 : 0));
    }

    protected boolean isDoneMoving(){
        for(int i = 0; i < 5; i++) {
            if(!PneumaticCraftUtils.areFloatsEqual(angles[i], targetAngles[i])) return false;
        }
        return true;
    }

    public boolean isDoneRotatingYaw(){
        return PneumaticCraftUtils.areFloatsEqual(angles[EnumAngles.TURN.ordinal()], targetAngles[EnumAngles.TURN.ordinal()]);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        for(int i = 0; i < 5; i++) {
            angles[i] = tag.getFloat("angle" + i);
            targetAngles[i] = tag.getFloat("targetAngle" + i);
        }
        slowMode = tag.getBoolean("slowMode");
        speed = tag.getFloat("speed");
        targetDirection[0] = EnumFacing.values()[tag.getInteger("targetDir1")];
        targetDirection[1] = EnumFacing.values()[tag.getInteger("targetDir2")];

    }

    @Override
    public void writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        for(int i = 0; i < 5; i++) {
            tag.setFloat("angle" + i, angles[i]);
            tag.setFloat("targetAngle" + i, targetAngles[i]);
        }
        tag.setBoolean("slowMode", slowMode);
        tag.setFloat("speed", speed);

        if(targetDirection != null) {
            if(targetDirection.length > 0) tag.setInteger("targetDir1", targetDirection[0].ordinal());

            if(targetDirection.length > 1) tag.setInteger("targetDir2", targetDirection[1].ordinal());
        }
    }

    public abstract boolean canMoveToDiagonalNeighbours();

    public EnumFacing[] getPlatformDirection(){
        for(EnumFacing dir : EnumFacing.HORIZONTALS) {
            if(worldObj.getTileEntity(getPos().offset(dir)) instanceof TileEntityAssemblyPlatform) return new EnumFacing[]{dir, null};
        }
        if(canMoveToDiagonalNeighbours()) {
            for(EnumFacing secDir : new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST}) {
                for(EnumFacing primDir : new EnumFacing[]{EnumFacing.NORTH, EnumFacing.SOUTH}) {
                    if(worldObj.getTileEntity(getPos().offset(primDir).offset(secDir)) instanceof TileEntityAssemblyPlatform) {
                        return new EnumFacing[]{primDir, secDir};
                    }
                }
            }
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox(){
        return new AxisAlignedBB(getPos().getX() - 1, getPos().getY() - 1, getPos().getZ() - 1, getPos().getX() + 2, getPos().getY() + 2, getPos().getZ() + 2);
    }

    @Override
    public void setSpeed(float speed){
        this.speed = speed;
    }

}
