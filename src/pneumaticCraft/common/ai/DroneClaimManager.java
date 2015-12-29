package pneumaticCraft.common.ai;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * Keeps track of the positions the drones are working on, and allows the drones to pick a coordinate in a smart way.
 */
public class DroneClaimManager{

    private static Map<Integer, DroneClaimManager> claimManagers = new HashMap<Integer, DroneClaimManager>();
    private final Map<BlockPos, Integer> currentPositions = new HashMap<BlockPos, Integer>();
    private static final int TIMEOUT = DroneAIManager.TICK_RATE + 1;

    public static DroneClaimManager getInstance(World world){
        DroneClaimManager manager = claimManagers.get(world.provider.getDimensionId());
        if(manager == null) {
            manager = new DroneClaimManager();
            claimManagers.put(world.provider.getDimensionId(), manager);
        }
        return manager;
    }

    /**
     * unclaim any positions that have been claimed too long. this prevents positions being claimed forever by died drones.
     */
    public void update(){
        Iterator<Map.Entry<BlockPos, Integer>> iterator = currentPositions.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<BlockPos, Integer> entry = iterator.next();
            if(entry.getValue() < TIMEOUT) {
                entry.setValue(entry.getValue() + 1);
            } else {
                iterator.remove();
            }
        }
    }

    public boolean isClaimed(BlockPos pos){
        return currentPositions.containsKey(pos);
    }

    public void claim(BlockPos pos){
        currentPositions.put(pos, 0);
    }
}
