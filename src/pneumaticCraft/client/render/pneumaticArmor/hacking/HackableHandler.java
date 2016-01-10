package pneumaticCraft.client.render.pneumaticArmor.hacking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.fml.relauncher.Side;
import pneumaticCraft.PneumaticCraft;
import pneumaticCraft.api.PneumaticRegistry;
import pneumaticCraft.api.client.pneumaticHelmet.IHackableBlock;
import pneumaticCraft.api.client.pneumaticHelmet.IHackableEntity;
import pneumaticCraft.api.client.pneumaticHelmet.IPneumaticHelmetRegistry;
import pneumaticCraft.client.render.pneumaticArmor.BlockTrackUpgradeHandler;
import pneumaticCraft.client.render.pneumaticArmor.EntityTrackUpgradeHandler;
import pneumaticCraft.client.render.pneumaticArmor.HUDHandler;
import pneumaticCraft.client.render.pneumaticArmor.PneumaticHelmetRegistry;
import pneumaticCraft.client.render.pneumaticArmor.RenderBlockTarget;
import pneumaticCraft.client.render.pneumaticArmor.RenderTarget;
import pneumaticCraft.client.render.pneumaticArmor.hacking.block.HackableButton;
import pneumaticCraft.client.render.pneumaticArmor.hacking.block.HackableDispenser;
import pneumaticCraft.client.render.pneumaticArmor.hacking.block.HackableDoor;
import pneumaticCraft.client.render.pneumaticArmor.hacking.block.HackableJukebox;
import pneumaticCraft.client.render.pneumaticArmor.hacking.block.HackableLever;
import pneumaticCraft.client.render.pneumaticArmor.hacking.block.HackableMobSpawner;
import pneumaticCraft.client.render.pneumaticArmor.hacking.block.HackableNoteblock;
import pneumaticCraft.client.render.pneumaticArmor.hacking.block.HackableSecurityStation;
import pneumaticCraft.client.render.pneumaticArmor.hacking.block.HackableTNT;
import pneumaticCraft.client.render.pneumaticArmor.hacking.block.HackableTripwire;
import pneumaticCraft.client.render.pneumaticArmor.hacking.entity.HackableBat;
import pneumaticCraft.client.render.pneumaticArmor.hacking.entity.HackableBlaze;
import pneumaticCraft.client.render.pneumaticArmor.hacking.entity.HackableCaveSpider;
import pneumaticCraft.client.render.pneumaticArmor.hacking.entity.HackableCow;
import pneumaticCraft.client.render.pneumaticArmor.hacking.entity.HackableCreeper;
import pneumaticCraft.client.render.pneumaticArmor.hacking.entity.HackableEnderman;
import pneumaticCraft.client.render.pneumaticArmor.hacking.entity.HackableGhast;
import pneumaticCraft.client.render.pneumaticArmor.hacking.entity.HackableLivingDisarm;
import pneumaticCraft.client.render.pneumaticArmor.hacking.entity.HackableTameable;
import pneumaticCraft.client.render.pneumaticArmor.hacking.entity.HackableWitch;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.util.WorldAndCoord;
import pneumaticCraft.lib.Log;

public class HackableHandler{
    private final Map<Entity, IHackableEntity> trackedHackableEntities = new HashMap<Entity, IHackableEntity>();
    private final Map<WorldAndCoord, IHackableBlock> trackedHackableBlocks = new HashMap<WorldAndCoord, IHackableBlock>();
    private static HackableHandler clientInstance, serverInstance;

    private static HackableHandler getInstance(){
        if(PneumaticCraft.proxy.getSide() == Side.CLIENT) {
            if(clientInstance == null) clientInstance = new HackableHandler();
            return clientInstance;
        } else {
            if(serverInstance == null) serverInstance = new HackableHandler();
            return serverInstance;
        }
    }

    public static void addDefaultEntries(){
        IPneumaticHelmetRegistry manager = PneumaticRegistry.getInstance().getHelmetRegistry();
        manager.addHackable(Blocks.tnt, HackableTNT.class);
        manager.addHackable(Blocks.mob_spawner, HackableMobSpawner.class);
        manager.addHackable(Blocks.lever, HackableLever.class);
        manager.addHackable(Blocks.stone_button, HackableButton.class);
        manager.addHackable(Blocks.wooden_button, HackableButton.class);
        manager.addHackable(Blocks.oak_door, HackableDoor.class);
        manager.addHackable(Blocks.spruce_door, HackableDoor.class);
        manager.addHackable(Blocks.birch_door, HackableDoor.class);
        manager.addHackable(Blocks.jungle_door, HackableDoor.class);
        manager.addHackable(Blocks.acacia_door, HackableDoor.class);
        manager.addHackable(Blocks.dark_oak_door, HackableDoor.class);
        manager.addHackable(Blocks.tripwire_hook, HackableTripwire.class);
        manager.addHackable(Blocks.dispenser, HackableDispenser.class);
        manager.addHackable(Blocks.dropper, HackableDispenser.class);
        manager.addHackable(Blockss.securityStation, HackableSecurityStation.class);
        manager.addHackable(Blocks.monster_egg, HackableTripwire.class);
        manager.addHackable(Blocks.noteblock, HackableNoteblock.class);
        manager.addHackable(Blocks.jukebox, HackableJukebox.class);

        manager.addHackable(EntityCreeper.class, HackableCreeper.class);
        manager.addHackable(EntityTameable.class, HackableTameable.class);
        manager.addHackable(EntityCow.class, HackableCow.class);
        manager.addHackable(EntityCaveSpider.class, HackableCaveSpider.class);
        manager.addHackable(EntityBlaze.class, HackableBlaze.class);
        manager.addHackable(EntityGhast.class, HackableGhast.class);
        manager.addHackable(EntityWitch.class, HackableWitch.class);
        manager.addHackable(EntityLiving.class, HackableLivingDisarm.class);
        manager.addHackable(EntityEnderman.class, HackableEnderman.class);
        manager.addHackable(EntityBat.class, HackableBat.class);
    }

    public static IHackableEntity getHackableForEntity(Entity entity, EntityPlayer player){

        //clean up the map
        Iterator<Map.Entry<Entity, IHackableEntity>> iterator = getInstance().trackedHackableEntities.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<Entity, IHackableEntity> entry = iterator.next();
            if(entry.getKey().isDead || !entry.getValue().canHack(entry.getKey(), player) && !isInDisplayCooldown(entry.getValue(), entry.getKey())) iterator.remove();
        }

        if(entity instanceof IHackableEntity && ((IHackableEntity)entity).canHack(entity, player)) return (IHackableEntity)entity;
        IHackableEntity hackable = getInstance().trackedHackableEntities.get(entity);
        if(hackable == null) {
            for(Map.Entry<Class<? extends Entity>, Class<? extends IHackableEntity>> entry : PneumaticHelmetRegistry.getInstance().hackableEntities.entrySet()) {
                if(entry.getKey().isAssignableFrom(entity.getClass())) {
                    try {
                        hackable = entry.getValue().newInstance();
                        if(hackable.canHack(entity, player)) {
                            getInstance().trackedHackableEntities.put(entity, hackable);
                            break;
                        } else {
                            hackable = null;
                        }
                    } catch(Exception e) {
                        //shouldn't happen, checked earlier.
                        e.printStackTrace();
                    }
                }
            }
        }
        return hackable;
    }

    public static IHackableBlock getHackableForCoord(WorldAndCoord coord, EntityPlayer player){
        return getHackableForCoord(coord.world, coord.pos, player);
    }

    public static IHackableBlock getHackableForCoord(IBlockAccess world, BlockPos pos, EntityPlayer player){
        //clean up the map
        Iterator<Map.Entry<WorldAndCoord, IHackableBlock>> iterator = getInstance().trackedHackableBlocks.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<WorldAndCoord, IHackableBlock> entry = iterator.next();
            Class<? extends IHackableBlock> hackableBlockClazz = PneumaticHelmetRegistry.getInstance().hackableBlocks.get(entry.getKey().getBlock());
            if(hackableBlockClazz != entry.getValue().getClass() || !entry.getValue().canHack(entry.getKey().world, entry.getKey().pos, player) && !isInDisplayCooldown(entry.getValue(), entry.getKey().world, entry.getKey().pos, player)) iterator.remove();
        }

        Block block = world.getBlockState(pos).getBlock();
        if(block instanceof IHackableBlock && ((IHackableBlock)block).canHack(world, pos, player)) return (IHackableBlock)block;
        IHackableBlock hackable = getInstance().trackedHackableBlocks.get(new WorldAndCoord(world, pos));
        if(hackable == null) {
            if(!PneumaticHelmetRegistry.getInstance().hackableBlocks.containsKey(block)) return null;
            try {
                hackable = PneumaticHelmetRegistry.getInstance().hackableBlocks.get(block).newInstance();
                if(hackable.canHack(world, pos, player)) {
                    getInstance().trackedHackableBlocks.put(new WorldAndCoord(world, pos), hackable);
                } else {
                    hackable = null;
                }
            } catch(Exception e) {
                //shouldn't happen, checked earlier.
                e.printStackTrace();
            }
        }
        return hackable;
    }

    private static boolean isInDisplayCooldown(IHackableBlock hackableBlock, IBlockAccess world, BlockPos pos, EntityPlayer player){
        if(player.worldObj.isRemote) {
            RenderBlockTarget target = HUDHandler.instance().getSpecificRenderer(BlockTrackUpgradeHandler.class).getTargetForCoord(pos);
            int requiredHackTime = hackableBlock.getHackTime(world, pos, player);
            return target != null && target.getHackTime() >= requiredHackTime && target.getHackTime() <= requiredHackTime + 20;
        } else {
            return false;
        }
    }

    private static boolean isInDisplayCooldown(IHackableEntity hackableBlock, Entity entity){
        if(entity.worldObj.isRemote) {
            RenderTarget target = HUDHandler.instance().getSpecificRenderer(EntityTrackUpgradeHandler.class).getTargetForEntity(entity);
            int requiredHackTime = hackableBlock.getHackTime(entity, PneumaticCraft.proxy.getPlayer());
            return target != null && target.getHackTime() >= requiredHackTime && target.getHackTime() <= requiredHackTime + 20;
        } else {
            return false;
        }
    }

    public static void onEntityConstruction(Entity entity){
        entity.registerExtendedProperties("PneumaticCraftHacking", new HackingEntityProperties());
    }

    public static class HackingEntityProperties implements IExtendedEntityProperties{
        private List<IHackableEntity> hackables;

        @Override
        public void saveNBTData(NBTTagCompound compound){
            if(hackables != null && !hackables.isEmpty()) {
                NBTTagList tagList = new NBTTagList();
                for(IHackableEntity hackableEntity : hackables) {
                    if(hackableEntity.getId() != null) {
                        NBTTagCompound tag = new NBTTagCompound();
                        tag.setString("id", hackableEntity.getId());
                        tagList.appendTag(tag);
                    }
                }
                compound.setTag("hackables", tagList);
            }
        }

        @Override
        public void loadNBTData(NBTTagCompound compound){
            hackables = null;
            NBTTagList tagList = compound.getTagList("hackables", 10);
            for(int i = 0; i < tagList.tagCount(); i++) {
                String hackableId = tagList.getCompoundTagAt(i).getString("id");
                Class<? extends IHackableEntity> hackableClass = PneumaticHelmetRegistry.getInstance().stringToEntityHackables.get(hackableId);
                if(hackableClass != null) {
                    try {
                        if(hackables == null) hackables = new ArrayList<IHackableEntity>();
                        hackables.add(hackableClass.newInstance());
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.warning("hackable \"" + hackableId + "\" not found when constructing from nbt. Was it deleted?");
                }
            }
        }

        @Override
        public void init(Entity entity, World world){}

        public void update(Entity entity){
            if(hackables != null) {
                Iterator<IHackableEntity> iterator = hackables.iterator();
                while(iterator.hasNext()) {
                    IHackableEntity hackable = iterator.next();
                    if(!hackable.afterHackTick(entity)) {
                        iterator.remove();
                    }
                }
            }
        }

        public void addHackable(IHackableEntity hackable){
            if(hackables == null) hackables = new ArrayList<IHackableEntity>();
            hackables.add(hackable);
        }

        public List<IHackableEntity> getCurrentHacks(){
            return hackables;
        }

    }
}
