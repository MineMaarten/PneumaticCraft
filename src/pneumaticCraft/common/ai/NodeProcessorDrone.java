package pneumaticCraft.common.ai;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.pathfinder.WalkNodeProcessor;
import pneumaticCraft.common.entity.living.EntityDrone;

public class NodeProcessorDrone extends WalkNodeProcessor{
    //TODO 1.8 test if it works
    @Override
    public int findPathOptions(PathPoint[] pathOptions, Entity entityIn, PathPoint currentPoint, PathPoint targetPoint, float maxDistance){
        EntityDrone drone = (EntityDrone)entityIn;
        int curIndex = 0;

        for(EnumFacing dir : EnumFacing.VALUES) {
            BlockPos pos = new BlockPos(currentPoint.xCoord + dir.getFrontOffsetX(), currentPoint.yCoord + dir.getFrontOffsetY(), currentPoint.zCoord + dir.getFrontOffsetZ());
            if(drone.isBlockValidPathfindBlock(pos)) {
                PathPoint pathpoint = openPoint(pos.getX(), pos.getY(), pos.getZ());
                if(!pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance) pathOptions[curIndex++] = pathpoint;
            }
        }
        return curIndex;
    }
}
