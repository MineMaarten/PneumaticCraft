package pneumaticCraft.api.universalSensor;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class EntityPollSensor implements IPollSensorSetting{

    @Override
    public String getSensorPath(){
        return "entityTracker";
    }

    @Override
    public int getPollFrequency(TileEntity te){
        return 1;
    }

    @Override
    public int getRedstoneValue(World world, BlockPos pos, int sensorRange, String textBoxText){
        AxisAlignedBB aabb = new AxisAlignedBB(pos.add(-sensorRange, -sensorRange, -sensorRange), pos.add(1 + sensorRange, 1 + sensorRange, 1 + sensorRange));
        return getRedstoneValue(world.getEntitiesWithinAABB(getEntityTracked(), aabb), textBoxText);
    }

    public abstract Class getEntityTracked();

    public abstract int getRedstoneValue(List<Entity> entities, String textBoxText);

}
