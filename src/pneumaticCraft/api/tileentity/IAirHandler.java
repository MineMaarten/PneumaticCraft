package pneumaticCraft.api.tileentity;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;

/**
 * A way for you to access about everything you need from a pneumatic machine.
 * DO NOT IMPLEMENT THIS YOURSELF! Use AirHandlerSupplier to get an instance for your TileEntity, and implement IPneumaticMachine instead.
 */

public interface IAirHandler extends IManoMeasurable{

    /**
     * -----------Needs to be forwarded by the implementing TileEntity's update() method.
     * Updates the pneumatic machine's logic like air dispersion and checking if it needs to explode.
     */
    public void update();

    /**
     * -----------Needs to be forwarded by the implementing TileEntity.
     * @param tag
     */
    public void readFromNBT(NBTTagCompound tag);

    /**
     * -----------Needs to be forwarded by the implementing TileEntity.
     * @param tag
     */
    public void writeToNBT(NBTTagCompound tag);

    /**
     * -----------Needs to be forwarded by the implementing TileEntity with itself as parameter.
     * @param parent TileEntity that is referencing this air handler.
     */
    public void validate(TileEntity parent);

    /**
     * -----------Needs to be forwarded from the implementing _Block_! Forward the Block's "onNeighborChange" method to this handler.
     */
    public void onNeighborChange();

    /**
     * Method to release air in the air. It takes air from a specific side, plays a sound effect, and spawns smoke particles.
     * It automatically detects if it needs to release air (when under pressure), suck air (when in vacuum) or do nothing.
     * @param side this only affects the direction the steam is pointing.
     */
    public void airLeak(EnumFacing side);

    /**
     * Returns a list of all the connecting pneumatics. It takes sides in account.
     */
    public List<Pair<EnumFacing, IAirHandler>> getConnectedPneumatics();

    /**
     * Adds air to the tank of the given side of this TE.
     */
    public void addAir(int amount);

    /**
     * Sets the volume of this TE's air tank. When the volume decreases the pressure will remain the same, meaning air will
     * be lost. When the volume increases, the air remains the same, meaning the pressure will drop.
     * Used in the Volume Upgrade calculations.
     * By default volume we mean the base volume. Volume added due to Volume Upgrades are added to this.
     * @param defaultVolume
     */
    public void setDefaultVolume(int defaultVolume);

    public int getVolume();

    /**
     * Returns the pressure at which this TE will explode.
     * @return
     */
    public float getMaxPressure();

    public float getPressure();

    /**
     * Returns the amount of air (that has a relation to the pressure: air = pressure * volume)
     * @return
     */
    public int getAir();

    /**
     * When your TileEntity is implementing IInventory and has slots that accept PneumaticCraft upgrades, register these slots
     * to the air handler by calling this method once on initialization of the TileEntity. Then they'll automatically be used to get Volume/Security upgrades.
     * @param upgradeSlots all upgrade slots stored in an array.
     */
    public void setUpgradeSlots(int... upgradeSlots);

    public int[] getUpgradeSlots();

    public World getWorld();

    public BlockPos getPos();

    /**
     * Not necessary if you use validate().
     * @param world
     */
    public void setWorld(World world);

    /**
     * Not necessary if you use validate().
     * @param pos
     */
    public void setPos(BlockPos pos);

    /**
     * Not necessary if you use validate()
     * @param pos
     */
    public void setPneumaticMachine(IPneumaticMachine machine);

    /**
     * Not necessary if you use validate(), or when the parent's inventory isn't used to handle like volume upgrades.
     * @param pos
     */
    public void setParentInventory(IInventory inv);

    /**
     * Not necessary if you use validate(), or when the parent doesn't implement IAirListener.
     * @param pos
     */
    public void setAirListener(IAirListener airListener);

    /**
     * Creates an air connection with another handler. Can be used to connect up pneumatic machines that aren't neighboring, like AE2's P2P tunnels.
     * This is a custom method that isn't necessary 99% of the cases. Only when you want to connect two IAirHandler's that aren't adjacent to eachother in the world you should need this.
     * @param otherHandler
     */
    public void createConnection(IAirHandler otherHandler);

    /**
     * Remove a connection created with createConnection. You need to call this when one of the hosts of this IAirHandlers is invalidated.
     * @param otherHandler
     */
    public void removeConnection(IAirHandler otherHandler);

}
