package pneumaticCraft.common.network;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.client.render.pneumaticArmor.EntityTrackUpgradeHandler;
import pneumaticCraft.client.render.pneumaticArmor.HUDHandler;
import pneumaticCraft.client.render.pneumaticArmor.RenderTarget;
import pneumaticCraft.common.entity.living.EntityDrone;

public class PacketShowWireframe extends LocationIntPacket<PacketShowWireframe>{

    private int entityId;

    public PacketShowWireframe(){}

    public PacketShowWireframe(EntityDrone entity, BlockPos pos){
        super(pos);
        entityId = entity.getEntityId();
    }

    @Override
    public void toBytes(ByteBuf buffer){
        super.toBytes(buffer);
        buffer.writeInt(entityId);
    }

    @Override
    public void fromBytes(ByteBuf buffer){
        super.fromBytes(buffer);
        entityId = buffer.readInt();
    }

    @Override
    public void handleClientSide(PacketShowWireframe message, EntityPlayer player){
        Entity ent = player.worldObj.getEntityByID(message.entityId);
        if(ent instanceof EntityDrone) {
            addToHudHandler((EntityDrone)ent, message.pos);
        }
    }

    @SideOnly(Side.CLIENT)
    private void addToHudHandler(EntityDrone drone, BlockPos pos){
        List<RenderTarget> targets = HUDHandler.instance().getSpecificRenderer(EntityTrackUpgradeHandler.class).getTargets();
        for(RenderTarget target : targets) {
            if(target.entity == drone) {
                target.getDroneAIRenderer().addBlackListEntry(drone.worldObj, pos);
            }
        }
    }

    @Override
    public void handleServerSide(PacketShowWireframe message, EntityPlayer player){}

}
