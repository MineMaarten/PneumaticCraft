package pneumaticCraft.common.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import pneumaticCraft.common.entity.living.EntityDrone;

public class DroneGoToOwner extends EntityAIBase{
    private final EntityDrone drone;
    public boolean isExecuting;

    public DroneGoToOwner(EntityDrone drone){
        this.drone = drone;
    }

    @Override
    public boolean shouldExecute(){
        EntityPlayerMP owner = getOnlineOwner();
        return isExecuting = owner != null && drone.getNavigator().tryMoveToEntityLiving(owner, drone.getSpeed());
    }

    @Override
    public boolean continueExecuting(){
        return isExecuting = getOnlineOwner() != null && !drone.getNavigator().noPath();
    }

    private EntityPlayerMP getOnlineOwner(){
        for(EntityPlayerMP player : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            if(player.getGameProfile().equals(drone.getFakePlayer().getGameProfile())) return player;
        }
        return null;
    }
}
