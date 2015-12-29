package pneumaticCraft.client.render.pneumaticArmor.hacking.block;

import java.util.List;

import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.api.client.pneumaticHelmet.IHackableBlock;

public class HackableJukebox implements IHackableBlock{
    @Override
    public String getId(){
        return null;
    }

    @Override
    public boolean canHack(IBlockAccess world, BlockPos pos, EntityPlayer player){
        return world.getBlockState(pos).getValue(BlockJukebox.HAS_RECORD);
    }

    @Override
    public void addInfo(World world, BlockPos pos, List<String> curInfo, EntityPlayer player){
        curInfo.add("pneumaticHelmet.hacking.result.silence");
    }

    @Override
    public void addPostHackInfo(World world, BlockPos pos, List<String> curInfo, EntityPlayer player){
        curInfo.add("pneumaticHelmet.hacking.finished.silenced");
    }

    @Override
    public int getHackTime(IBlockAccess world, BlockPos pos, EntityPlayer player){
        return 100;
    }

    @Override
    public void onHackFinished(World world, BlockPos pos, EntityPlayer player){
        IBlockState state = world.getBlockState(pos);
        state.getBlock().onBlockActivated(world, pos, state, player, EnumFacing.UP, 0, 0, 0);
    }

    @Override
    public boolean afterHackTick(World world, BlockPos pos){
        return false;
    }

}
