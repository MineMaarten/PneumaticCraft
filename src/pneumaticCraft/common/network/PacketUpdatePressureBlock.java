package pneumaticCraft.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import pneumaticCraft.common.pressure.AirHandler;
import pneumaticCraft.common.thirdparty.ModInteractionUtils;
import pneumaticCraft.common.tileentity.TileEntityPneumaticBase;
import pneumaticCraft.common.tileentity.TileEntityPressureTube;

public class PacketUpdatePressureBlock extends LocationIntPacket<PacketUpdatePressureBlock>{
    private int currentAir;

    public PacketUpdatePressureBlock(){}

    public PacketUpdatePressureBlock(TileEntityPneumaticBase te){
        super(te.getPos());
        currentAir = te.getAirHandler(null).getAir();
    }

    @Override
    public void toBytes(ByteBuf buf){
        super.toBytes(buf);
        buf.writeInt(currentAir);
    }

    @Override
    public void fromBytes(ByteBuf buf){
        super.fromBytes(buf);
        currentAir = buf.readInt();
    }

    @Override
    public void handleClientSide(PacketUpdatePressureBlock message, EntityPlayer player){
        TileEntity te = message.getTileEntity(player.worldObj);
        if(te instanceof TileEntityPneumaticBase) {
            ((AirHandler)((TileEntityPneumaticBase)te).getAirHandler(null)).setAir(message.currentAir);
        } else {
            TileEntityPressureTube tube = ModInteractionUtils.getInstance().getTube(te);
            if(tube != null) ((AirHandler)tube.getAirHandler(null)).setAir(message.currentAir);
        }
    }

    @Override
    public void handleServerSide(PacketUpdatePressureBlock message, EntityPlayer player){}

}
