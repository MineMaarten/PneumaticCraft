package pneumaticCraft.client.render.pneumaticArmor.hacking.block;

import java.util.List;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.api.client.pneumaticHelmet.IHackableBlock;

public class HackableDoor implements IHackableBlock{
    @Override
    public String getId(){
        return null;
    }

    @Override
    public boolean canHack(IBlockAccess world, BlockPos pos, EntityPlayer player){
        return true;
    }

    @Override
    public void addInfo(World world, BlockPos pos, List<String> curInfo, EntityPlayer player){
        if(!world.getBlockState(pos).getValue(BlockDoor.OPEN)) {
            curInfo.add("pneumaticHelmet.hacking.result.open");
        } else {
            curInfo.add("pneumaticHelmet.hacking.result.close");
        }
    }

    @Override
    public void addPostHackInfo(World world, BlockPos pos, List<String> curInfo, EntityPlayer player){
        if(!world.getBlockState(pos).getValue(BlockDoor.OPEN)) {
            curInfo.add("pneumaticHelmet.hacking.finished.closed");
        } else {
            curInfo.add("pneumaticHelmet.hacking.finished.opened");
        }
    }

    @Override
    public int getHackTime(IBlockAccess world, BlockPos pos, EntityPlayer player){
        return 20;
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
