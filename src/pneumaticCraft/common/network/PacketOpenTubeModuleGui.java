package pneumaticCraft.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import pneumaticCraft.PneumaticCraft;
import pneumaticCraft.common.block.BlockPressureTube;

public class PacketOpenTubeModuleGui extends LocationIntPacket<PacketOpenTubeModuleGui>{
    private int guiID;

    public PacketOpenTubeModuleGui(){}

    public PacketOpenTubeModuleGui(int guiID, BlockPos pos){
        super(pos);
        this.guiID = guiID;

    }

    @Override
    public void fromBytes(ByteBuf buf){
        super.fromBytes(buf);
        guiID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf){
        super.toBytes(buf);
        buf.writeInt(guiID);
    }

    @Override
    public void handleClientSide(PacketOpenTubeModuleGui message, EntityPlayer player){
        if(BlockPressureTube.getLookedModule(player.worldObj, message.pos, player) != null) {
            Object o = PneumaticCraft.proxy.getClientGuiElement(message.guiID, player, player.worldObj, message.pos.getX(), message.pos.getY(), message.pos.getZ());
            FMLCommonHandler.instance().showGuiScreen(o);
        }
    }

    @Override
    public void handleServerSide(PacketOpenTubeModuleGui message, EntityPlayer player){}

}
