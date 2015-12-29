package pneumaticCraft.client.render.pneumaticArmor.blockTracker;

import java.util.List;

import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.api.client.pneumaticHelmet.IBlockTrackEntry;

public class BlockTrackEntryEndPortalFrame implements IBlockTrackEntry{

    @Override
    public boolean shouldTrackWithThisEntry(IBlockAccess world, BlockPos pos, IBlockState state, TileEntity te){
        return state.getBlock() == Blocks.end_portal_frame;
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
    public void addInformation(World world, BlockPos pos, TileEntity te, List<String> infoList){
        if(world.getBlockState(pos).getValue(BlockEndPortalFrame.EYE)) {
            infoList.add("Eye inserted");
        } else {
            infoList.add("Eye not inserted");
        }
    }

    @Override
    public String getEntryName(){
        return Blocks.end_portal_frame.getUnlocalizedName() + ".name";
    }

}
