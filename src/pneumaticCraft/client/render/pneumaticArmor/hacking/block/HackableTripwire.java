package pneumaticCraft.client.render.pneumaticArmor.hacking.block;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.api.client.pneumaticHelmet.IHackableBlock;

public class HackableTripwire implements IHackableBlock{

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
        curInfo.add("pneumaticHelmet.hacking.result.neutralize");
    }

    @Override
    public void addPostHackInfo(World world, BlockPos pos, List<String> curInfo, EntityPlayer player){
        curInfo.add("pneumaticHelmet.hacking.finished.neutralized");
    }

    @Override
    public int getHackTime(IBlockAccess world, BlockPos pos, EntityPlayer player){
        return 40;
    }

    @Override
    public void onHackFinished(World world, BlockPos pos, EntityPlayer player){
        world.destroyBlock(pos, true);//break block
    }

    @Override
    public boolean afterHackTick(World world, BlockPos pos){
        return false;
    }

}
