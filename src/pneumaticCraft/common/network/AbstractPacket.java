package pneumaticCraft.common.network;

import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import pneumaticCraft.PneumaticCraft;

public abstract class AbstractPacket<REQ extends AbstractPacket> implements IMessage, IMessageHandler<REQ, REQ>{

    private static final Queue<AbstractPacket> pending = new LinkedList<AbstractPacket>();
    private static int timeoutTimer;

    @Override
    public REQ onMessage(REQ message, MessageContext ctx){
        if(ctx.side == Side.SERVER) {
            handleServerSide(message, ctx.getServerHandler().playerEntity);
        } else {
            pending.add(message);
        }
        return null;
    }

    public static void processPackets(){
        synchronized(pending) {
            while(!pending.isEmpty()) {
                AbstractPacket packet = pending.peek();
                if(packet != null) {
                    if(packet.canHandlePacketAlready(packet, PneumaticCraft.proxy.getPlayer())) {
                        timeoutTimer = 0;
                        packet.handleClientSide(packet, PneumaticCraft.proxy.getPlayer());
                        pending.remove();
                    } else {
                        timeoutTimer++;
                        if(timeoutTimer > 40) {
                            pending.remove();
                        }
                    }
                }
            }
        }
    }

    /**
     * Handle a packet on the client side. Note this occurs after decoding has completed.
     * @param message
     * @param player the player reference
     */
    public abstract void handleClientSide(REQ message, EntityPlayer player);

    /**
     * Handle a packet on the server side. Note this occurs after decoding has completed.
     * @param message
     * @param player the player reference
     */
    public abstract void handleServerSide(REQ message, EntityPlayer player);

    public boolean canHandlePacketAlready(REQ message, EntityPlayer player){
        return player != null && player.worldObj != null;
    }
}
