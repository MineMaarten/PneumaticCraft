package pneumaticCraft.common.tileentity;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import pneumaticCraft.api.tileentity.IManoMeasurable;
import pneumaticCraft.common.block.BlockPressureChamberWall;
import pneumaticCraft.common.block.Blockss;

public class TileEntityPressureChamberWall extends TileEntityBase implements IManoMeasurable{

    protected TileEntityPressureChamberValve teValve;
    private int valveX;
    private int valveY;
    private int valveZ;

    public TileEntityPressureChamberWall(){
        super();
    }

    public TileEntityPressureChamberWall(int... upgradeSlots){
        super(upgradeSlots);
    }

    public TileEntityPressureChamberValve getCore(){
        if(teValve == null && (valveX != 0 || valveY != 0 || valveZ != 0)) {// when the saved TE equals null, check if we can
            // retrieve the TE from the NBT saved coords.

            TileEntity te = worldObj.getTileEntity(new BlockPos(valveX, valveY, valveZ));
            setCore(te instanceof TileEntityPressureChamberValve ? (TileEntityPressureChamberValve)te : null);
        }
        return teValve;
    }

    public void onBlockBreak(){
        teValve = getCore();
        if(teValve != null) {
            teValve.onMultiBlockBreak();
        }
    }

    public void setCore(TileEntityPressureChamberValve te){

        if(!worldObj.isRemote) {
            if(te != null) {
                valveX = te.getPos().getX();
                valveY = te.getPos().getY();
                valveZ = te.getPos().getZ();
            } else {
                valveX = 0;
                valveY = 0;
                valveZ = 0;
            }
        }
        boolean hasChanged = teValve != te;
        teValve = te;
        if(hasChanged && !worldObj.isRemote) {
            IBlockState curState = worldObj.getBlockState(getPos());
            if(curState.getBlock() == Blockss.pressureChamberWall) {
                IBlockState newState = ((BlockPressureChamberWall)Blockss.pressureChamberWall).updateState(curState, worldObj, getPos());
                worldObj.setBlockState(getPos(), newState, 2);
            }
        }
    }

    @Override
    public void onDescUpdate(){
        super.onDescUpdate();
        teValve = null;
    }

    /**
     * Reads a tile entity from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        valveX = tag.getInteger("valveX");
        valveY = tag.getInteger("valveY");
        valveZ = tag.getInteger("valveZ");
        teValve = null;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        tag.setInteger("valveX", valveX);
        tag.setInteger("valveY", valveY);
        tag.setInteger("valveZ", valveZ);
    }

    @Override
    public void printManometerMessage(EntityPlayer player, List<String> curInfo){
        if(getCore() != null) {
            teValve.getAirHandler(null).printManometerMessage(player, curInfo);
        }
    }

    @Override
    protected boolean shouldRerenderChunkOnDescUpdate(){
        return true;
    }

}
