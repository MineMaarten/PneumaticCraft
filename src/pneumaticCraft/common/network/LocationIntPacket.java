package pneumaticCraft.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import pneumaticCraft.lib.TileEntityConstants;

/**
 * MineChess
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public abstract class LocationIntPacket<REQ extends AbstractPacket> extends AbstractPacket<REQ>{

    protected BlockPos pos;

    public LocationIntPacket(){}

    public LocationIntPacket(BlockPos pos){
        this.pos = pos;
    }

    @Override
    public void toBytes(ByteBuf buf){
        NetworkUtils.writeBlockPos(buf, pos);
    }

    @Override
    public void fromBytes(ByteBuf buf){
        pos = NetworkUtils.readBlockPos(buf);
    }

    public NetworkRegistry.TargetPoint getTargetPoint(World world){
        return getTargetPoint(world, TileEntityConstants.PACKET_UPDATE_DISTANCE);
    }

    public NetworkRegistry.TargetPoint getTargetPoint(World world, double updateDistance){
        return new NetworkRegistry.TargetPoint(world.provider.getDimensionId(), pos.getX(), pos.getY(), pos.getZ(), updateDistance);
    }

    protected Block getBlock(World world){
        return world.getBlockState(pos).getBlock();
    }

    protected TileEntity getTileEntity(World world){
        return world.getTileEntity(pos);
    }
}
