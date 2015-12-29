package pneumaticCraft.client.render.pneumaticArmor.blockTracker;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.PneumaticCraft;
import pneumaticCraft.api.client.pneumaticHelmet.IBlockTrackEntry;
import pneumaticCraft.api.client.pneumaticHelmet.IHackableBlock;
import pneumaticCraft.client.render.pneumaticArmor.BlockTrackUpgradeHandler;
import pneumaticCraft.client.render.pneumaticArmor.HUDHandler;
import pneumaticCraft.client.render.pneumaticArmor.HackUpgradeRenderHandler;
import pneumaticCraft.client.render.pneumaticArmor.hacking.HackableHandler;

public class BlockTrackEntryHackable implements IBlockTrackEntry{

    @Override
    public boolean shouldTrackWithThisEntry(IBlockAccess world, BlockPos pos, IBlockState state, TileEntity te){
        return HackUpgradeRenderHandler.enabledForPlayer(PneumaticCraft.proxy.getPlayer()) && HackableHandler.getHackableForCoord(world, pos, PneumaticCraft.proxy.getPlayer()) != null;
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
        IHackableBlock hackableBlock = HackableHandler.getHackableForCoord(world, pos, PneumaticCraft.proxy.getPlayer());
        int hackTime = HUDHandler.instance().getSpecificRenderer(BlockTrackUpgradeHandler.class).getTargetForCoord(pos).getHackTime();
        if(hackTime == 0) {
            hackableBlock.addInfo(world, pos, infoList, PneumaticCraft.proxy.getPlayer());
        } else {
            int requiredHackTime = hackableBlock.getHackTime(world, pos, PneumaticCraft.proxy.getPlayer());
            int percentageComplete = hackTime * 100 / requiredHackTime;
            if(percentageComplete < 100) {
                infoList.add(I18n.format("pneumaticHelmet.hacking.hacking") + " (" + percentageComplete + "%%)");
            } else if(hackTime < requiredHackTime + 20) {
                hackableBlock.addPostHackInfo(world, pos, infoList, PneumaticCraft.proxy.getPlayer());
            } else {
                hackableBlock.addInfo(world, pos, infoList, PneumaticCraft.proxy.getPlayer());
            }
        }
    }

    @Override
    public String getEntryName(){
        return "blockTracker.module.hackables";
    }

}
