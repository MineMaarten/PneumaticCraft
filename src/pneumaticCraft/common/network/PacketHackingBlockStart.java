package pneumaticCraft.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import pneumaticCraft.client.render.pneumaticArmor.BlockTrackUpgradeHandler;
import pneumaticCraft.client.render.pneumaticArmor.HUDHandler;
import pneumaticCraft.client.render.pneumaticArmor.RenderBlockTarget;
import pneumaticCraft.common.CommonHUDHandler;
import pneumaticCraft.common.util.WorldAndCoord;

public class PacketHackingBlockStart extends LocationIntPacket<PacketHackingBlockStart>{

    public PacketHackingBlockStart(){}

    public PacketHackingBlockStart(BlockPos pos){
        super(pos);
    }

    @Override
    public void fromBytes(ByteBuf buf){
        super.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf){
        super.toBytes(buf);
    }

    @Override
    public void handleClientSide(PacketHackingBlockStart message, EntityPlayer player){
        CommonHUDHandler.getHandlerForPlayer(player).setHackedBlock(new WorldAndCoord(player.worldObj, message.pos));
        RenderBlockTarget target = HUDHandler.instance().getSpecificRenderer(BlockTrackUpgradeHandler.class).getTargetForCoord(message.pos);
        if(target != null) target.onHackConfirmServer();
    }

    @Override
    public void handleServerSide(PacketHackingBlockStart message, EntityPlayer player){
        CommonHUDHandler.getHandlerForPlayer(player).setHackedBlock(new WorldAndCoord(player.worldObj, message.pos));
        NetworkHandler.sendToAllAround(message, player.worldObj);
    }

}
