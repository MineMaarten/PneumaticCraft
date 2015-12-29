package pneumaticCraft.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import pneumaticCraft.PneumaticCraft;
import pneumaticCraft.api.client.pneumaticHelmet.IHackableBlock;
import pneumaticCraft.client.render.pneumaticArmor.hacking.HackableHandler;
import pneumaticCraft.common.CommonHUDHandler;
import pneumaticCraft.common.util.WorldAndCoord;

public class PacketHackingBlockFinish extends LocationIntPacket<PacketHackingBlockFinish>{

    public PacketHackingBlockFinish(){}

    public PacketHackingBlockFinish(BlockPos pos){
        super(pos);
    }

    public PacketHackingBlockFinish(WorldAndCoord coord){
        super(coord.pos);
    }

    @Override
    public void handleClientSide(PacketHackingBlockFinish message, EntityPlayer player){
        IHackableBlock hackableBlock = HackableHandler.getHackableForCoord(player.worldObj, message.pos, player);
        if(hackableBlock != null) {
            hackableBlock.onHackFinished(player.worldObj, message.pos, player);
            PneumaticCraft.proxy.getHackTickHandler().trackBlock(new WorldAndCoord(player.worldObj, message.pos), hackableBlock);
            CommonHUDHandler.getHandlerForPlayer(player).setHackedBlock(null);
            player.worldObj.playSound(message.pos.getX(), message.pos.getY(), message.pos.getZ(), "PneumaticCraft:helmetHackFinish", 1.0F, 1.0F, false);
        }
    }

    @Override
    public void handleServerSide(PacketHackingBlockFinish message, EntityPlayer player){}

}
