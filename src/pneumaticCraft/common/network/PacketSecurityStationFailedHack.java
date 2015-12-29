package pneumaticCraft.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import pneumaticCraft.common.DamageSourcePneumaticCraft;
import pneumaticCraft.common.tileentity.TileEntitySecurityStation;

public class PacketSecurityStationFailedHack extends LocationIntPacket<PacketSecurityStationFailedHack>{

    public PacketSecurityStationFailedHack(){}

    public PacketSecurityStationFailedHack(BlockPos pos){
        super(pos);
    }

    @Override
    public void handleClientSide(PacketSecurityStationFailedHack message, EntityPlayer player){}

    @Override
    public void handleServerSide(PacketSecurityStationFailedHack message, EntityPlayer player){
        TileEntity te = message.getTileEntity(player.worldObj);
        if(te instanceof TileEntitySecurityStation) {
            TileEntitySecurityStation station = (TileEntitySecurityStation)te;
            if(!station.isPlayerOnWhiteList(player)) {
                player.attackEntityFrom(DamageSourcePneumaticCraft.securityStation, 19);
            }
        }
    }
}
