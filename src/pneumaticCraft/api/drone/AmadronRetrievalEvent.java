package pneumaticCraft.api.drone;

import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;
import pneumaticCraft.api.PneumaticRegistry;

/**
 * Fired (on the MinecraftForge.EVENT_BUS) when {@link PneumaticRegistry.IPneumaticCraftInterface#retrieveItemsAmazonStyle(net.minecraft.world.World, BlockPos, net.minecraft.item.ItemStack...) has successfully retrieved the items requested.
 * The same drone will be passed as the one returned in the retrieve method.
 */
public class AmadronRetrievalEvent extends Event{
    public final IDrone drone;

    public AmadronRetrievalEvent(IDrone drone){
        this.drone = drone;
    }
}
