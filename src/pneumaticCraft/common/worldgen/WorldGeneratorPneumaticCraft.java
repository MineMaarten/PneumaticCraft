package pneumaticCraft.common.worldgen;

import java.util.Random;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.IWorldGenerator;
import pneumaticCraft.common.config.Config;
import pneumaticCraft.common.fluid.Fluids;

public class WorldGeneratorPneumaticCraft implements IWorldGenerator{

    public WorldGeneratorPneumaticCraft(){}

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider){
        if(!(chunkGenerator instanceof ChunkProviderFlat)) { //don't generate on flatworlds
            switch(world.provider.getDimensionId()){
                case 0:
                    generateSurface(world, random, chunkX * 16, chunkZ * 16);
                    break;
                case -1:
                    generateNether(world, random, chunkX * 16, chunkZ * 16);
                    break;
                case 1:
                    generateEnd(world, random, chunkX * 16, chunkZ * 16);
                    break;
                default:
                    generateSurface(world, random, chunkX * 16, chunkZ * 16);
            }
        }
    }

    public void generateSurface(World world, Random rand, int chunkX, int chunkZ){
        if(rand.nextDouble() < Config.oilGenerationChance / 100D) {
            int y = rand.nextInt(rand.nextInt(128) + 8);
            new WorldGenLakes(FluidRegistry.getFluid(Fluids.oil.getName()).getBlock()).generate(world, rand, new BlockPos(chunkX + 8, y, chunkZ + 8));
        }

    }

    public void generateNether(World world, Random rand, int chunkX, int chunkZ){

    }

    public void generateEnd(World world, Random rand, int chunkX, int chunkZ){

    }

}
