package pneumaticCraft.client.render.pneumaticArmor.blockTracker;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.api.client.pneumaticHelmet.IBlockTrackEntry;

public class BlockTrackEntrySimple implements IBlockTrackEntry{

    @Override
    public boolean shouldTrackWithThisEntry(IBlockAccess world, BlockPos pos, IBlockState state, TileEntity te){
        Block block = state.getBlock();
        return block == Blocks.tnt || block == Blocks.tripwire_hook || block == Blocks.monster_egg;
    }

    @Override
    public boolean shouldBeUpdatedFromServer(TileEntity te){
        return false;
    }

    @Override
    public int spamThreshold(){
        return 10;
    }

    @Override
    public void addInformation(World world, BlockPos pos, TileEntity te, List<String> infoList){}

    @Override
    public String getEntryName(){
        return "blockTracker.module.misc";
    }
}
