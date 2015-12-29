package pneumaticCraft.common.ai;

import java.util.Comparator;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import pneumaticCraft.common.util.PneumaticCraftUtils;

public class ChunkPositionSorter implements Comparator<BlockPos>{

    private final double x, y, z;

    public ChunkPositionSorter(IDroneBase entity){
        Vec3 vec = entity.getDronePos();
        x = vec.xCoord;
        y = vec.yCoord;
        z = vec.zCoord;
    }

    public ChunkPositionSorter(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int compare(BlockPos c1, BlockPos c2){
        return Double.compare(PneumaticCraftUtils.distBetween(c1.getX(), c1.getY(), c1.getZ(), x, y, z), PneumaticCraftUtils.distBetween(c2.getX(), c2.getY(), c2.getZ(), x, y, z));
    }
}
