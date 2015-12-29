package pneumaticCraft.client.render.pneumaticArmor.hacking.block;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.api.client.pneumaticHelmet.IHackableBlock;

public class HackableMobSpawner implements IHackableBlock{
    @Override
    public String getId(){
        return "mobSpawner";
    }

    @Override
    public boolean canHack(IBlockAccess world, BlockPos pos, EntityPlayer player){
        return !isHacked(world, pos);
    }

    public static boolean isHacked(IBlockAccess world, BlockPos pos){
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof TileEntityMobSpawner) {
            return ((TileEntityMobSpawner)te).getSpawnerBaseLogic().activatingRangeFromPlayer == 0;
        }
        return false;
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
        return 200;
    }

    @Override
    public void onHackFinished(World world, BlockPos pos, EntityPlayer player){
        if(!world.isRemote) {
            NBTTagCompound tag = new NBTTagCompound();
            TileEntity te = world.getTileEntity(pos);
            te.writeToNBT(tag);
            tag.setShort("RequiredPlayerRange", (short)0);
            te.readFromNBT(tag);
            world.markBlockForUpdate(pos);
        }

    }

    @Override
    public boolean afterHackTick(World world, BlockPos pos){
        MobSpawnerBaseLogic spawner = ((TileEntityMobSpawner)world.getTileEntity(pos)).getSpawnerBaseLogic();
        spawner.prevMobRotation = spawner.mobRotation;//oldRotation = rotation, to stop render glitching
        spawner.spawnDelay = 10;
        return false;
    }
}
