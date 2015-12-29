package pneumaticCraft.client.render.pneumaticArmor.hacking.block;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.api.client.pneumaticHelmet.IHackableBlock;

public class HackableNoteblock implements IHackableBlock{
    @Override
    public String getId(){
        return "noteBlock";
    }

    @Override
    public boolean canHack(IBlockAccess world, BlockPos pos, EntityPlayer player){
        return true;
    }

    @Override
    public void addInfo(World world, BlockPos pos, List<String> curInfo, EntityPlayer player){
        curInfo.add("pneumaticHelmet.hacking.result.makeSound");
    }

    @Override
    public void addPostHackInfo(World world, BlockPos pos, List<String> curInfo, EntityPlayer player){
        curInfo.add("pneumaticHelmet.hacking.finished.makingSound");
    }

    @Override
    public int getHackTime(IBlockAccess world, BlockPos pos, EntityPlayer player){
        return 60;
    }

    @Override
    public void onHackFinished(World world, BlockPos pos, EntityPlayer player){}

    @Override
    public boolean afterHackTick(World world, BlockPos pos){
        IBlockState state = world.getBlockState(pos);
        state.getBlock().onBlockActivated(world, pos, state, null, EnumFacing.UP, 0, 0, 0);
        return true;
    }

}
