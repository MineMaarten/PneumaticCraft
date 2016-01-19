package pneumaticCraft.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import pneumaticCraft.common.ai.DroneClaimManager;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.config.AmadronOfferPeriodicConfig;
import pneumaticCraft.common.network.DescPacketHandler;
import pneumaticCraft.common.network.NetworkHandler;
import pneumaticCraft.common.network.PacketServerTickTime;
import pneumaticCraft.common.recipes.AmadronOfferManager;
import pneumaticCraft.common.tileentity.TileEntityElectrostaticCompressor;
import pneumaticCraft.lib.PneumaticValues;

public class TickHandlerPneumaticCraft{

    @SubscribeEvent
    public void tickEnd(TickEvent.WorldTickEvent event){
        if(event.phase == TickEvent.Phase.END) {
            World world = event.world;
            checkLightning(world);
            DroneClaimManager.getInstance(world).update();
            if(!event.world.isRemote && event.world.provider.getDimensionId() == 0 && event.world.getWorldTime() % (24000 / AmadronOfferPeriodicConfig.timesPerDay) == 1) {
                AmadronOfferManager.getInstance().shufflePeriodicOffers();
            }
            if(!event.world.isRemote && event.world.getTotalWorldTime() % 100 == 0) {
                double tickTime = net.minecraft.util.MathHelper.average(MinecraftServer.getServer().tickTimeArray) * 1.0E-6D;//In case world are going to get their own thread: MinecraftServer.getServer().worldTickTimes.get(event.world.provider.getDimensionId())
                NetworkHandler.sendToDimension(new PacketServerTickTime(tickTime), event.world.provider.getDimensionId());
                if(event.world.getTotalWorldTime() % 600 == 0) AmadronOfferManager.getInstance().tryRestockCustomOffers();
            }
        }
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ClientTickEvent event){
        if(event.phase == TickEvent.Phase.END) DescPacketHandler.processPackets();
    }

    private void checkLightning(World world){
        if(world.isRemote) return;

        for(int i = 0; i < world.weatherEffects.size(); i++) {
            Entity entity = world.weatherEffects.get(i);
            if(entity.ticksExisted == 1 && entity instanceof EntityLightningBolt) {
                handleElectrostaticGeneration(world, entity);
            }
        }
    }

    //TODO 1.8 test Electrostatic compressor
    private void handleElectrostaticGeneration(World world, Entity entity){
        Set<BlockPos> posList = new HashSet<BlockPos>();
        getElectrostaticGrid(posList, world, new BlockPos(Math.round(entity.posX), Math.round(entity.posY), Math.round(entity.posZ)));
        List<TileEntityElectrostaticCompressor> compressors = new ArrayList<TileEntityElectrostaticCompressor>();
        for(BlockPos pos : posList) {
            if(world.getBlockState(pos).getBlock() == Blockss.electrostaticCompressor) {
                TileEntity te = world.getTileEntity(pos);
                if(te instanceof TileEntityElectrostaticCompressor) {
                    compressors.add((TileEntityElectrostaticCompressor)te);
                }
            }
        }
        for(TileEntityElectrostaticCompressor compressor : compressors) {
            compressor.addAir(PneumaticValues.PRODUCTION_ELECTROSTATIC_COMPRESSOR / compressors.size());
            compressor.onStruckByLightning();
        }
    }

    /**
     * All Iron bar blocks blocks will be added to the given arraylist of coordinates. This method
     * will be recursively called until the whole grid of iron bars is on the list.
     * @param set
     * @param world
     * @param x
     * @param y
     * @param z
     */
    public static void getElectrostaticGrid(Set<BlockPos> set, World world, BlockPos pos){
        for(EnumFacing d : EnumFacing.VALUES) {
            BlockPos newPos = pos.offset(d);
            Block block = world.getBlockState(newPos).getBlock();
            if((block == net.minecraft.init.Blocks.iron_bars || block == Blockss.electrostaticCompressor) && set.add(newPos)) {
                getElectrostaticGrid(set, world, newPos);
            }
        }
    }
}
