package pneumaticCraft.common.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import org.apache.commons.lang3.tuple.Pair;

import pneumaticCraft.api.tileentity.IAirHandler;
import pneumaticCraft.api.tileentity.IAirListener;
import pneumaticCraft.common.network.DescSynced;
import pneumaticCraft.common.pressure.AirHandler;

public class TileEntityCreativeCompressor extends TileEntityPneumaticBase implements IAirListener{
    @DescSynced
    public float pressureSetpoint;

    public TileEntityCreativeCompressor(){
        super(30, 30, 50000);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);
        pressureSetpoint = nbt.getFloat("setpoint");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt){
        super.writeToNBT(nbt);
        nbt.setFloat("setpoint", pressureSetpoint);
    }

    @Override
    public void handleGUIButtonPress(int guiID, EntityPlayer player){
        switch(guiID){
            case 0:
                pressureSetpoint -= 1;
                break;
            case 1:
                pressureSetpoint -= 0.1F;
                break;
            case 2:
                pressureSetpoint += 0.1F;
                break;
            case 3:
                pressureSetpoint += 1.0F;
                break;
        }
        if(pressureSetpoint > 30) pressureSetpoint = 30;
        if(pressureSetpoint < -1) pressureSetpoint = -1;
        ((AirHandler)getAirHandler(null)).setPressure(pressureSetpoint);
    }

    @Override
    public void onAirDispersion(IAirHandler handler, EnumFacing dir, int airAdded){
        addAir(-airAdded); //Keep the pressure equal.
    }

    @Override
    public int getMaxDispersion(IAirHandler handler, EnumFacing dir){
        return Integer.MAX_VALUE;
    }

    @Override
    public void addConnectedPneumatics(List<Pair<EnumFacing, IAirHandler>> pneumatics){}
}
