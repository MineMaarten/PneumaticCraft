package pneumaticCraft.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import pneumaticCraft.api.client.pneumaticHelmet.IHackableBlock;
import pneumaticCraft.api.client.pneumaticHelmet.IHackableEntity;
import pneumaticCraft.client.render.pneumaticArmor.PneumaticHelmetRegistry;
import pneumaticCraft.client.render.pneumaticArmor.hacking.HackableHandler.HackingEntityProperties;
import pneumaticCraft.common.util.WorldAndCoord;
import pneumaticCraft.lib.Log;

public class HackTickHandler{
    private final Map<WorldAndCoord, IHackableBlock> hackedBlocks = new HashMap<WorldAndCoord, IHackableBlock>();

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase == TickEvent.Phase.END) {
            Iterator<Map.Entry<WorldAndCoord, IHackableBlock>> blockIterator = hackedBlocks.entrySet().iterator();
            while(blockIterator.hasNext()) {
                Map.Entry<WorldAndCoord, IHackableBlock> entry = blockIterator.next();
                IHackableBlock hackableBlock = entry.getValue();
                WorldAndCoord hackedBlock = entry.getKey();

                boolean found = false;
                for(Map.Entry<Block, Class<? extends IHackableBlock>> registeredEntry : PneumaticHelmetRegistry.getInstance().hackableBlocks.entrySet()) {
                    if(hackableBlock.getClass() == registeredEntry.getValue()) {
                        if(hackedBlock.getBlock() == registeredEntry.getKey()) {
                            if(!hackableBlock.afterHackTick((World)hackedBlock.world, hackedBlock.pos)) {
                                blockIterator.remove();
                            }
                            found = true;
                            break;
                        }
                    }
                }
                if(!found) blockIterator.remove();
            }
        }
    }

    @SubscribeEvent
    public void worldTick(TickEvent.WorldTickEvent event){
        if(event.phase == TickEvent.Phase.END) {
            try {
                for(Entity entity : event.world.loadedEntityList) {
                    HackingEntityProperties hackingProps = (HackingEntityProperties)entity.getExtendedProperties("PneumaticCraftHacking");
                    if(hackingProps != null) {
                        hackingProps.update(entity);
                    } else {
                        Log.warning("Extended entity props HackingEntityProperties couldn't be found in the entity " + entity.getName());
                    }
                }
            } catch(Throwable e) {
                //Catching a CME which I have no clue on what might cause it.
            }
        }
    }

    public void trackBlock(WorldAndCoord coord, IHackableBlock iHackable){
        hackedBlocks.put(coord, iHackable);
    }

    public void trackEntity(Entity entity, IHackableEntity iHackable){
        if(iHackable.getId() != null) {
            HackingEntityProperties hackingProps = (HackingEntityProperties)entity.getExtendedProperties("PneumaticCraftHacking");
            if(hackingProps != null) {
                hackingProps.addHackable(iHackable);
            } else {
                Log.warning("Extended entity props HackingEntityProperties couldn't be found in the entity " + entity.getName());
            }
        }
    }
}
