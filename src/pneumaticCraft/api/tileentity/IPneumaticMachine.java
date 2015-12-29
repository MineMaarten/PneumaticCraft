package pneumaticCraft.api.tileentity;

import net.minecraft.util.EnumFacing;

/**
 * Deprecated: Use ISidedPneumaticMachine in favor of this one. In Minecraft 1.8+ that ISidedPneumaticMachine will be renamed to IPneumaticMachine, and therefore will be sided by default.
 * TODO 1.8 remove
 */
@Deprecated
public interface IPneumaticMachine{

    /**
     * In your TileEntity class which is implementing this interface you should keep a reference of an IAirHandler.
     * You can retrieve one by calling {@link AirHandlerSupplier#getAirHandler(net.minecraft.tileentity.TileEntity, float, float, float, float)}.
     * Do this when your TileEntity is initialized, i.e. getPos().getX(),getPos().getY(),getPos().getZ() and worldObj have a value. 
     * In this method you need to return this reference.
     * 
     * IMPORTANT: You need to forward the {@link net.minecraft.tileentity.TileEntity#update()}, 
     * {@link net.minecraft.tileentity.TileEntity#writeToNBT(net.minecraft.nbt.NBTTagCompound)} , 
     * {@link net.minecraft.tileentity.TileEntity#readFromNBT(net.minecraft.nbt.NBTTagCompound)} and
     * {@link net.minecraft.tileentity.TileEntity#validate()} (with the implementing TileEntity as additional parameter)
     * to the IAirHandler.
     * Apart from that you'll need to forward {@link net.minecraft.block.Block#onNeighborChange(net.minecraft.world.IBlockAccess, int, int, int, int, int, int)}
     * from the implementing block to the IAirHandler.
     * @return
     */
    public IAirHandler getAirHandler();

    /**
     * Returns true if the pneumatic logic is connected to the given side.
     * @param side
     * @return
     */
    public boolean isConnectedTo(EnumFacing side);
}
