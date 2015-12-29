package pneumaticCraft.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import pneumaticCraft.common.item.ItemGPSTool;
import pneumaticCraft.common.item.Itemss;

public class PacketChangeGPSToolCoordinate extends LocationIntPacket<PacketChangeGPSToolCoordinate>{
    private String variable;

    public PacketChangeGPSToolCoordinate(){}

    public PacketChangeGPSToolCoordinate(BlockPos pos, String variable){
        super(pos);
        this.variable = variable;
    }

    @Override
    public void toBytes(ByteBuf buf){
        super.toBytes(buf);
        ByteBufUtils.writeUTF8String(buf, variable);
    }

    @Override
    public void fromBytes(ByteBuf buf){
        super.fromBytes(buf);
        variable = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void handleClientSide(PacketChangeGPSToolCoordinate message, EntityPlayer player){}

    @Override
    public void handleServerSide(PacketChangeGPSToolCoordinate message, EntityPlayer player){
        ItemStack playerStack = player.getCurrentEquippedItem();
        if(playerStack != null && playerStack.getItem() == Itemss.GPSTool) {
            ItemGPSTool.setVariable(playerStack, message.variable);
            if(message.pos.getY() >= 0) {
                playerStack.getItem().onItemUse(playerStack, player, player.worldObj, message.pos, null, 0, 0, 0);
            }
        }
    }
}
