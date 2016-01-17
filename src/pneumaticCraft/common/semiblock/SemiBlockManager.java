package pneumaticCraft.common.semiblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import pneumaticCraft.PneumaticCraft;
import pneumaticCraft.common.NBTUtil;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.network.NetworkHandler;
import pneumaticCraft.common.network.PacketDescription;
import pneumaticCraft.common.network.PacketSetSemiBlock;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.lib.Log;

import com.google.common.collect.HashBiMap;

public class SemiBlockManager{
    private final Map<Chunk, Map<BlockPos, ISemiBlock>> semiBlocks = new HashMap<Chunk, Map<BlockPos, ISemiBlock>>();
    private final List<ISemiBlock> addingBlocks = new ArrayList<ISemiBlock>();
    private final Map<Chunk, Set<EntityPlayer>> syncList = new HashMap<Chunk, Set<EntityPlayer>>();
    private final Set<Chunk> chunksMarkedForRemoval = new HashSet<Chunk>();
    public static final int SYNC_DISTANCE = 64;
    private static final HashBiMap<String, Class<? extends ISemiBlock>> registeredTypes = HashBiMap.create();
    private static final HashBiMap<Class<? extends ISemiBlock>, Item> semiBlockToItems = HashBiMap.create();
    private static final SemiBlockManager INSTANCE = new SemiBlockManager();
    private static final SemiBlockManager CLIENT_INSTANCE = new SemiBlockManager();

    public static SemiBlockManager getServerInstance(){
        return INSTANCE;
    }

    public static SemiBlockManager getClientOldInstance(){
        return CLIENT_INSTANCE;
    }

    public static SemiBlockManager getInstance(World world){
        return world.isRemote ? CLIENT_INSTANCE : INSTANCE;
    }

    public static Item registerSemiBlock(String key, Class<? extends ISemiBlock> semiBlock, boolean addItem){
        if(registeredTypes.containsKey(key)) throw new IllegalArgumentException("Duplicate registration key: " + key);
        registeredTypes.put(key, semiBlock);

        if(addItem) {
            ItemSemiBlockBase item = new ItemSemiBlockBase(key);
            Itemss.registerItem(item, key);
            PneumaticCraft.proxy.registerSemiBlockRenderer(item);
            registerSemiBlockToItemMapping(semiBlock, item);
            return item;
        } else {
            return null;
        }
    }

    public static void registerSemiBlockToItemMapping(Class<? extends ISemiBlock> semiBlock, Item item){
        semiBlockToItems.put(semiBlock, item);
    }

    public static Item getItemForSemiBlock(ISemiBlock semiBlock){
        return getItemForSemiBlock(semiBlock.getClass());
    }

    public static Item getItemForSemiBlock(Class<? extends ISemiBlock> semiBlock){
        return semiBlockToItems.get(semiBlock);
    }

    public static Class<? extends ISemiBlock> getSemiBlockForItem(Item item){
        return semiBlockToItems.inverse().get(item);
    }

    public static String getKeyForSemiBlock(ISemiBlock semiBlock){
        return getKeyForSemiBlock(semiBlock.getClass());
    }

    public static String getKeyForSemiBlock(Class<? extends ISemiBlock> semiBlock){
        return registeredTypes.inverse().get(semiBlock);
    }

    public static ISemiBlock getSemiBlockForKey(String key){
        try {
            Class<? extends ISemiBlock> clazz = registeredTypes.get(key);
            if(clazz != null) {
                return clazz.newInstance();
            } else {
                Log.warning("Semi Block with id \"" + key + "\" isn't registered!");
                return null;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SubscribeEvent
    public void onChunkUnLoad(ChunkEvent.Unload event){
        if(!event.world.isRemote) {
            chunksMarkedForRemoval.add(event.getChunk());
        }
    }

    @SubscribeEvent
    public void onChunkSave(ChunkDataEvent.Save event){
        Map<BlockPos, ISemiBlock> map = semiBlocks.get(event.getChunk());
        if(map != null && map.size() > 0) {
            NBTTagList tagList = new NBTTagList();
            for(Map.Entry<BlockPos, ISemiBlock> entry : map.entrySet()) {
                NBTTagCompound t = new NBTTagCompound();
                entry.getValue().writeToNBT(t);
                NBTUtil.setPos(t, entry.getKey());
                t.setString("type", getKeyForSemiBlock(entry.getValue()));
                tagList.appendTag(t);
            }
            event.getData().setTag("SemiBlocks", tagList);
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkDataEvent.Load event){
        try {
            if(!event.world.isRemote) {
                if(event.getData().hasKey("SemiBlocks")) {
                    Map<BlockPos, ISemiBlock> map = getOrCreateMap(event.getChunk());
                    map.clear();
                    NBTTagList tagList = event.getData().getTagList("SemiBlocks", 10);
                    for(int i = 0; i < tagList.tagCount(); i++) {
                        NBTTagCompound t = tagList.getCompoundTagAt(i);
                        ISemiBlock semiBlock = getSemiBlockForKey(t.getString("type"));
                        if(semiBlock != null) {
                            semiBlock.readFromNBT(t);
                            setSemiBlock(event.world, NBTUtil.getPos(t), semiBlock, event.getChunk());
                        }
                    }
                }
            }
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event){
        for(ISemiBlock semiBlock : addingBlocks) {
            Chunk chunk = semiBlock.getWorld().getChunkFromBlockCoords(semiBlock.getPos());
            getOrCreateMap(chunk).put(semiBlock.getPos(), semiBlock);
            chunk.setChunkModified();

            for(EntityPlayer player : syncList.get(chunk)) {
                NetworkHandler.sendTo(new PacketSetSemiBlock(semiBlock), (EntityPlayerMP)player);
                PacketDescription descPacket = semiBlock.getDescriptionPacket();
                if(descPacket != null) NetworkHandler.sendTo(descPacket, (EntityPlayerMP)player);
            }
        }
        addingBlocks.clear();

        for(Chunk removingChunk : chunksMarkedForRemoval) {
            if(!removingChunk.isLoaded()) {
                semiBlocks.remove(removingChunk);
                syncList.remove(removingChunk);
            }
        }
        chunksMarkedForRemoval.clear();

        for(Map<BlockPos, ISemiBlock> map : semiBlocks.values()) {
            for(ISemiBlock semiBlock : map.values()) {
                if(!semiBlock.isInvalid()) semiBlock.update();
            }
            Iterator<ISemiBlock> iterator = map.values().iterator();
            while(iterator.hasNext()) {
                if(iterator.next().isInvalid()) {
                    iterator.remove();
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event){
        if(this == getServerInstance()) getClientOldInstance().onClientTick(event);
        else {
            EntityPlayer player = PneumaticCraft.proxy.getPlayer();
            if(player != null) {
                for(ISemiBlock semiBlock : addingBlocks) {
                    Chunk chunk = semiBlock.getWorld().getChunkFromBlockCoords(semiBlock.getPos());
                    getOrCreateMap(chunk).put(semiBlock.getPos(), semiBlock);
                }
                addingBlocks.clear();

                Iterator<Map.Entry<Chunk, Map<BlockPos, ISemiBlock>>> iterator = semiBlocks.entrySet().iterator();
                while(iterator.hasNext()) {
                    Map.Entry<Chunk, Map<BlockPos, ISemiBlock>> entry = iterator.next();
                    if(PneumaticCraftUtils.distBetween(player.posX, 0, player.posZ, entry.getKey().xPosition * 16 - 8, 0, entry.getKey().zPosition * 16 - 8) > SYNC_DISTANCE + 10) {
                        iterator.remove();
                    } else {
                        for(ISemiBlock semiBlock : entry.getValue().values()) {
                            if(!semiBlock.isInvalid()) semiBlock.update();
                        }
                        Iterator<ISemiBlock> it = entry.getValue().values().iterator();
                        while(it.hasNext()) {
                            if(it.next().isInvalid()) {
                                it.remove();
                            }
                        }
                    }
                }
            } else {
                semiBlocks.clear();
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event){
        if(!event.world.isRemote) {
            syncWithPlayers(event.world);
        }
    }

    private void syncWithPlayers(World world){
        List<EntityPlayer> players = world.playerEntities;
        for(Map.Entry<Chunk, Set<EntityPlayer>> entry : syncList.entrySet()) {
            Chunk chunk = entry.getKey();
            Set<EntityPlayer> syncedPlayers = entry.getValue();
            int chunkX = chunk.xPosition * 16 - 8;
            int chunkZ = chunk.zPosition * 16 - 8;
            for(EntityPlayer player : players) {
                if(chunk.getWorld() == world) {
                    double dist = PneumaticCraftUtils.distBetween(player.posX, 0, player.posZ, chunkX, 0, chunkZ);
                    if(dist < SYNC_DISTANCE) {
                        if(syncedPlayers.add(player)) {
                            for(ISemiBlock semiBlock : semiBlocks.get(chunk).values()) {
                                if(!semiBlock.isInvalid()) {
                                    NetworkHandler.sendTo(new PacketSetSemiBlock(semiBlock), (EntityPlayerMP)player);
                                    PacketDescription descPacket = semiBlock.getDescriptionPacket();
                                    if(descPacket != null) NetworkHandler.sendTo(descPacket, (EntityPlayerMP)player);
                                }
                            }
                        }
                    } else if(dist > SYNC_DISTANCE + 5) {
                        syncedPlayers.remove(player);
                    }
                } else {
                    syncedPlayers.remove(player);
                }
            }
        }
    }

    @SubscribeEvent
    public void onInteraction(PlayerInteractEvent event){
        if(!event.world.isRemote && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            ItemStack curItem = event.entityPlayer.getCurrentEquippedItem();
            if(curItem != null && curItem.getItem() instanceof ISemiBlockItem) {
                if(getSemiBlock(event.world, event.pos) != null) {
                    if(event.entityPlayer.capabilities.isCreativeMode) {
                        setSemiBlock(event.world, event.pos, null);
                    } else {
                        breakSemiBlock(event.world, event.pos, event.entityPlayer);
                    }
                    event.setCanceled(true);
                } else {
                    ISemiBlock newBlock = ((ISemiBlockItem)curItem.getItem()).getSemiBlock(event.world, event.pos, curItem);
                    newBlock.initialize(event.world, event.pos);
                    if(newBlock.canPlace()) {
                        setSemiBlock(event.world, event.pos, newBlock);
                        newBlock.onPlaced(event.entityPlayer, curItem);
                        event.world.playSoundEffect(event.pos.getX() + 0.5, event.pos.getY() + 0.5, event.pos.getZ() + 0.5, Block.soundTypeGlass.getPlaceSound(), (Block.soundTypeGlass.getVolume() + 1.0F) / 2.0F, Block.soundTypeGlass.getFrequency() * 0.8F);
                        if(!event.entityPlayer.capabilities.isCreativeMode) {
                            curItem.stackSize--;
                            if(curItem.stackSize <= 0) event.entityPlayer.setCurrentItemOrArmor(0, null);
                        }
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    private Map<BlockPos, ISemiBlock> getOrCreateMap(Chunk chunk){
        Map<BlockPos, ISemiBlock> map = semiBlocks.get(chunk);
        if(map == null) {
            map = new HashMap<BlockPos, ISemiBlock>();
            semiBlocks.put(chunk, map);
            syncList.put(chunk, new HashSet<EntityPlayer>());
        }
        return map;
    }

    public void breakSemiBlock(World world, BlockPos pos){
        breakSemiBlock(world, pos, null);
    }

    public void breakSemiBlock(World world, BlockPos pos, EntityPlayer player){
        ISemiBlock semiBlock = getSemiBlock(world, pos);
        if(semiBlock != null) {
            List<ItemStack> drops = new ArrayList<ItemStack>();
            semiBlock.addDrops(drops);
            for(ItemStack stack : drops) {
                EntityItem item = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                world.spawnEntityInWorld(item);
                if(player != null) item.onCollideWithPlayer(player);
            }
            setSemiBlock(world, pos, null);
        }
    }

    public void setSemiBlock(World world, BlockPos pos, ISemiBlock semiBlock){
        setSemiBlock(world, pos, semiBlock, world.getChunkFromBlockCoords(pos));
    }

    private void setSemiBlock(World world, BlockPos pos, ISemiBlock semiBlock, Chunk chunk){
        if(semiBlock != null && !registeredTypes.containsValue(semiBlock.getClass())) throw new IllegalStateException("ISemiBlock \"" + semiBlock + "\" was not registered!");
        if(semiBlock != null) {
            semiBlock.initialize(world, pos);
            addingBlocks.add(semiBlock);
        } else {
            ISemiBlock removedBlock = getOrCreateMap(chunk).get(pos);
            if(removedBlock != null) {
                removedBlock.invalidate();
                for(EntityPlayer player : syncList.get(chunk)) {
                    NetworkHandler.sendTo(new PacketSetSemiBlock(pos, null), (EntityPlayerMP)player);
                }
            }
        }
        chunk.setChunkModified();
    }

    public ISemiBlock getSemiBlock(World world, BlockPos pos){
        for(ISemiBlock semiBlock : addingBlocks) {
            if(semiBlock.getWorld() == world && semiBlock.getPos().equals(pos)) return semiBlock;
        }

        Chunk chunk = world.getChunkFromBlockCoords(pos);
        Map<BlockPos, ISemiBlock> map = semiBlocks.get(chunk);
        if(map != null) {
            return map.get(pos);
        } else {
            return null;
        }
    }

    public Map<Chunk, Map<BlockPos, ISemiBlock>> getSemiBlocks(){
        return semiBlocks;
    }
}
