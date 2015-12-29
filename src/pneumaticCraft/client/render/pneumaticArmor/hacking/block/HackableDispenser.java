package pneumaticCraft.client.render.pneumaticArmor.hacking.block;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.api.client.pneumaticHelmet.IHackableBlock;

public class HackableDispenser implements IHackableBlock{

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
        curInfo.add("pneumaticHelmet.hacking.result.dispense");
    }

    @Override
    public void addPostHackInfo(World world, BlockPos pos, List<String> curInfo, EntityPlayer player){
        curInfo.add("pneumaticHelmet.hacking.finished.dispensed");
    }

    @Override
    public int getHackTime(IBlockAccess world, BlockPos pos, EntityPlayer player){
        return 40;
    }

    @Override
    public void onHackFinished(World world, BlockPos pos, EntityPlayer player){
        IBlockState state = world.getBlockState(pos);
        state.getBlock().updateTick(world, pos, state, player.getRNG());
    }

    @Override
    public boolean afterHackTick(World world, BlockPos pos){
        return false;
    }

}
