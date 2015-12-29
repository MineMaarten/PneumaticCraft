package pneumaticCraft.common.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pneumaticCraft.api.drone.IDrone;
import pneumaticCraft.api.drone.SpecialVariableRetrievalEvent;
import pneumaticCraft.common.entity.living.EntityDrone;

public class DroneSpecialVariableHandler{

    @SubscribeEvent
    public void onSpecialVariableRetrieving(SpecialVariableRetrievalEvent.CoordinateVariable.Drone event){
        if(event.specialVarName.equalsIgnoreCase("owner")) {
            EntityDrone drone = (EntityDrone)event.drone;
            EntityPlayer player = drone.getOwner();
            if(player != null) event.coordinate = getPosForEntity(player);
        } else if(event.specialVarName.equalsIgnoreCase("drone")) {
            event.coordinate = getPosForEntity(event.drone);
        } else if(event.specialVarName.toLowerCase().startsWith("player=")) {
            EntityPlayer player = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(event.specialVarName.substring("player=".length()));
            if(player != null) event.coordinate = getPosForEntity(player);
        }
    }

    private BlockPos getPosForEntity(Entity entity){
        return new BlockPos(entity).offset(EnumFacing.UP); //TODO 1.8 check what's with the offset
    }

    private BlockPos getPosForEntity(IDrone entity){
        return new BlockPos(entity.getDronePos()).offset(EnumFacing.UP);//TODO 1.8 check what's with the offset
    }
}
