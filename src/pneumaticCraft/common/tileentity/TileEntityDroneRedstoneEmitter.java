package pneumaticCraft.common.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import pneumaticCraft.common.block.Blockss;

public class TileEntityDroneRedstoneEmitter extends TileEntity implements ITickable{
    @Override
    public void update(){
        for(EnumFacing d : EnumFacing.VALUES) {
            if(Blockss.droneRedstoneEmitter.getWeakPower(worldObj, getPos(), worldObj.getBlockState(getPos()), d) > 0) {
                return;
            }
        }
        worldObj.setBlockToAir(getPos());
    }
}
