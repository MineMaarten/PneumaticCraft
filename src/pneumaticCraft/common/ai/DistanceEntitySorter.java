package pneumaticCraft.common.ai;

import java.util.Comparator;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class DistanceEntitySorter implements Comparator{
    private final IDroneBase drone;

    public DistanceEntitySorter(IDroneBase drone){
        this.drone = drone;
    }

    public int compare(Entity entity1, Entity entity2){
        Vec3 vec = drone.getDronePos();
        double d0 = vec.squareDistanceTo(entity1.getPositionVector());
        double d1 = vec.squareDistanceTo(entity2.getPositionVector());
        return d0 < d1 ? -1 : d0 > d1 ? 1 : 0;
    }

    @Override
    public int compare(Object p_compare_1_, Object p_compare_2_){
        return this.compare((Entity)p_compare_1_, (Entity)p_compare_2_);
    }
}
