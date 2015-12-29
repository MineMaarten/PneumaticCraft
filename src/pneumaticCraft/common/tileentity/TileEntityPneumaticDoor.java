package pneumaticCraft.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.common.network.DescSynced;
import pneumaticCraft.common.network.LazySynced;

public class TileEntityPneumaticDoor extends TileEntityBase{
    @DescSynced
    @LazySynced
    public float rotation;
    public float oldRotation;
    @DescSynced
    public boolean rightGoing;

    public void setRotation(float rotation){
        oldRotation = this.rotation;
        this.rotation = rotation;
        TileEntity te = null;
        if(getBlockMetadata() < 6) {
            te = worldObj.getTileEntity(getPos().offset(EnumFacing.UP));
        } else {
            te = worldObj.getTileEntity(getPos().offset(EnumFacing.DOWN));
        }
        if(te instanceof TileEntityPneumaticDoor) {
            TileEntityPneumaticDoor door = (TileEntityPneumaticDoor)te;
            door.rightGoing = rightGoing;
            if(rotation != door.rotation) {
                door.setRotation(rotation);
                //door.rotation = rotation;
                // door.oldRotation = oldRotation;
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        tag.setBoolean("rightGoing", rightGoing);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        rightGoing = tag.getBoolean("rightGoing");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox(){
        return new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 2, getPos().getZ() + 1);
    }
}
