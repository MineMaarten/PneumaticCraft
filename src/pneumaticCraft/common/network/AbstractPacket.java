package pneumaticCraft.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import pneumaticCraft.PneumaticCraft;

public abstract class AbstractPacket<REQ extends AbstractPacket> implements IMessage, IMessageHandler<REQ, REQ>{

    @Override
    public REQ onMessage(final REQ message, final MessageContext ctx){
        if(ctx.side == Side.SERVER) {
            PneumaticCraft.proxy.addScheduledTask(new Runnable(){
                @Override
                public void run(){
                    message.handleServerSide(message, ctx.getServerHandler().playerEntity);
                }
            }, ctx.side == Side.SERVER);
        } else {
            PneumaticCraft.proxy.addScheduledTask(new Runnable(){
                @Override
                public void run(){
                    message.handleClientSide(message, PneumaticCraft.proxy.getPlayer());
                }
            }, ctx.side == Side.SERVER);
        }
        return null;
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
