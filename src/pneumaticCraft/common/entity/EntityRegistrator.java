package pneumaticCraft.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import pneumaticCraft.PneumaticCraft;
import pneumaticCraft.common.entity.living.EntityDrone;
import pneumaticCraft.common.entity.living.EntityLogisticsDrone;
import pneumaticCraft.common.entity.projectile.EntityVortex;

public class EntityRegistrator{
    public static void init(){
        // Entities
        // parms: entity class, mobname (for spawners), id, modclass, max player
        // distance for update, update frequency, boolean keep server updated
        // about velocities.
        EntityRegistry.registerModEntity(EntityVortex.class, "Vortex", 0, PneumaticCraft.instance, 80, 1, true);
        EntityRegistry.registerModEntity(EntityDrone.class, "Drone", 1, PneumaticCraft.instance, 80, 1, true);
        EntityRegistry.registerModEntity(EntityLogisticsDrone.class, "logisticDrone", 2, PneumaticCraft.instance, 80, 1, true);
        // Entity Eggs:
        // registerEntityEgg(EntityRook.class, 0xffffff, 0x000000);
    }

    public static int getUniqueEntityId(){
        int startEntityId = 0;
        do {
            startEntityId++;
        } while(EntityList.getStringFromID(startEntityId) != null);

        return startEntityId;
    }

    public static void registerEntityEgg(Class<? extends Entity> entity, int primaryColor, int secondaryColor){
        int id = getUniqueEntityId();
        EntityList.idToClassMapping.put(id, entity);
        EntityList.entityEggs.put(id, new EntityEggInfo(id, primaryColor, secondaryColor));
    }
}
