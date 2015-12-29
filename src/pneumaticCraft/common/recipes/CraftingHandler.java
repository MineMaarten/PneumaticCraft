package pneumaticCraft.common.recipes;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import pneumaticCraft.common.AchievementHandler;

public class CraftingHandler{

    @SubscribeEvent
    public void onCrafting(PlayerEvent.ItemCraftedEvent event){
        if(event.player != null) {
            AchievementHandler.giveAchievement(event.player, event.crafting);
        }
    }

}
