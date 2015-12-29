package pneumaticCraft.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import pneumaticCraft.common.inventory.ContainerLogistics;
import pneumaticCraft.common.semiblock.ISemiBlock;
import pneumaticCraft.common.semiblock.SemiBlockLogistics;
import pneumaticCraft.common.semiblock.SemiBlockManager;

public class PacketSetLogisticsFilterStack extends LocationIntPacket<PacketSetLogisticsFilterStack>{
    private ItemStack settingStack;
    private int settingIndex;

    public PacketSetLogisticsFilterStack(){}

    public PacketSetLogisticsFilterStack(SemiBlockLogistics logistics, ItemStack stack, int index){
        super(logistics.getPos());
        settingStack = stack;
        settingIndex = index;
    }

    @Override
    public void toBytes(ByteBuf buf){
        super.toBytes(buf);
        ByteBufUtils.writeItemStack(buf, settingStack);
        buf.writeInt(settingIndex);
    }

    @Override
    public void fromBytes(ByteBuf buf){
        super.fromBytes(buf);
        settingStack = ByteBufUtils.readItemStack(buf);
        settingIndex = buf.readInt();
    }

    @Override
    public void handleClientSide(PacketSetLogisticsFilterStack message, EntityPlayer player){

    }

    @Override
    public void handleServerSide(PacketSetLogisticsFilterStack message, EntityPlayer player){
        if(message.pos.equals(new BlockPos(0, 0, 0))) {
            if(player.openContainer instanceof ContainerLogistics) {
                ((ContainerLogistics)player.openContainer).logistics.getFilters().setInventorySlotContents(message.settingIndex, message.settingStack);
            }
        } else {
            ISemiBlock semiBlock = SemiBlockManager.getInstance(player.worldObj).getSemiBlock(player.worldObj, message.pos);
            if(semiBlock instanceof SemiBlockLogistics) {
                ((SemiBlockLogistics)semiBlock).getFilters().setInventorySlotContents(message.settingIndex, message.settingStack);
            }
        }
    }

}
