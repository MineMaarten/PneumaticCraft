package pneumaticCraft.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Loader;
import pneumaticCraft.api.client.IClientRegistry;
import pneumaticCraft.api.client.pneumaticHelmet.IPneumaticHelmetRegistry;
import pneumaticCraft.api.drone.IDroneRegistry;
import pneumaticCraft.api.item.IInventoryItem;
import pneumaticCraft.api.recipe.IPneumaticRecipeRegistry;
import pneumaticCraft.api.tileentity.IAirHandlerSupplier;
import pneumaticCraft.api.tileentity.IHeatRegistry;
import pneumaticCraft.api.universalSensor.ISensorRegistry;

/**
 * This class can be used to register and access various things to and from the mod.
 */
public final class PneumaticRegistry{
    private static IPneumaticCraftInterface instance;

    public static IPneumaticCraftInterface getInstance(){
        return instance;
    }

    public static void init(IPneumaticCraftInterface inter){
        if(instance == null && Loader.instance().activeModContainer().getModId().equals("PneumaticCraft")) instance = inter;//only allow initialization once; by PneumaticCraft
        else throw new IllegalStateException("Only PneumaticCraft is allowed to call this method!");
    }

    public interface IPneumaticCraftInterface{

        public IPneumaticRecipeRegistry getRecipeRegistry();

        public IAirHandlerSupplier getAirHandlerSupplier();

        public IPneumaticHelmetRegistry getHelmetRegistry();

        public IDroneRegistry getDroneRegistry();

        public IHeatRegistry getHeatRegistry();

        public IClientRegistry getGuiRegistry();

        public ISensorRegistry getSensorRegistry();

        /*
         * --------------- Items -------------------
         */
        /**
         * See {@link pneumaticCraft.api.item.IInventoryItem}
         * @param handler
         */
        public void registerInventoryItem(IInventoryItem handler);

        /*
         * ---------------- Power Generation -----------
         */

        /**
         * Adds a burnable liquid to the Liquid Compressor's available burnable fuels.
         * @param fluid
         * @param mLPerBucket the amount of mL generated for 1000mB of the fuel. As comparison, one piece of coal generates 16000mL in an Air Compressor.
         */
        public void registerFuel(Fluid fluid, int mLPerBucket);

        /*
         * --------------- Misc -------------------
         */

        /**
         * Returns the amount of Security Stations that disallow interaction with the given coordinate for the given player.
         * Usually you'd disallow interaction when this returns > 0.
         * @param world
         * @param pos
         * @param player
         * @param showRangeLines When true, any Security Station that prevents interaction will show the line grid (server --> client update is handled internally).
         * @return The amount of Security Stations that disallow interaction for the given player.
         * This method throws an IllegalArgumentException when tried to be called from the client side!
         */
        public int getProtectingSecurityStations(World world, BlockPos pos, EntityPlayer player, boolean showRangeLines);

        /**
         * Used to register a liquid that represents liquid XP (like MFR mob essence, OpenBlocks liquid XP).
         * This is used in the Aerial Interface to pump XP in/out of the player.
         * @param fluid
         * @param liquidToPointRatio The amount of liquid (in mB) used to get one XP point. In OpenBlocks this is 20 (mB/point).
         */
        public void registerXPLiquid(Fluid fluid, int liquidToPointRatio);

    }
}
