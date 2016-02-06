package pneumaticCraft.common.thirdparty.computercraft;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.api.client.pneumaticHelmet.IBlockTrackEntry;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

/**
 * Created by Maarten on 25-Jul-14.
 */
public class BlockTrackEntryPeripheral implements IBlockTrackEntry{
    @Override
    public boolean shouldTrackWithThisEntry(IBlockAccess world, BlockPos pos, IBlockState state, TileEntity te){
        if(state.getBlock() instanceof IPeripheralProvider) {
            IPeripheral peripheral = ((IPeripheralProvider)state.getBlock()).getPeripheral(te.getWorld(), pos, EnumFacing.DOWN);
            return peripheral != null && peripheral.getMethodNames().length != 0;
        } else {
            return false;
        }
    }

    @Override
    public boolean shouldBeUpdatedFromServer(TileEntity te){
        return false;
    }

    @Override
    public int spamThreshold(){
        return 8;
    }

    @Override
    public void addInformation(World world, BlockPos pos, TileEntity te, List<String> infoList){
        infoList.add("blockTracker.info.peripheral.title");
        infoList.add("blockTracker.info.peripheral.availableMethods");
        IPeripheral peripheral = ((IPeripheralProvider)world.getBlockState(pos).getBlock()).getPeripheral(world, pos, EnumFacing.DOWN);
        if(peripheral != null) {
            for(String method : peripheral.getMethodNames()) {
                infoList.add("-" + method);
            }
        }
    }

    @Override
    public String getEntryName(){
        return "blockTracker.module.peripheral";
    }
}
