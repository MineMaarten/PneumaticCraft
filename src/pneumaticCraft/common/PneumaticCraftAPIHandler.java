package pneumaticCraft.common;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import pneumaticCraft.api.PneumaticRegistry.IPneumaticCraftInterface;
import pneumaticCraft.api.client.IClientRegistry;
import pneumaticCraft.api.client.pneumaticHelmet.IPneumaticHelmetRegistry;
import pneumaticCraft.api.drone.IDroneRegistry;
import pneumaticCraft.api.item.IItemRegistry;
import pneumaticCraft.api.recipe.IPneumaticRecipeRegistry;
import pneumaticCraft.api.tileentity.IAirHandlerSupplier;
import pneumaticCraft.api.tileentity.IHeatRegistry;
import pneumaticCraft.api.universalSensor.ISensorRegistry;
import pneumaticCraft.client.GuiRegistry;
import pneumaticCraft.client.render.pneumaticArmor.PneumaticHelmetRegistry;
import pneumaticCraft.common.heat.HeatExchangerManager;
import pneumaticCraft.common.item.ItemRegistry;
import pneumaticCraft.common.pressure.AirHandlerSupplier;
import pneumaticCraft.common.recipes.PneumaticRecipeRegistry;
import pneumaticCraft.common.sensor.SensorHandler;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.lib.Log;

/**
 * With this class you can register your entities to give more info in the tooltip of the Entity Tracker.
 */
public class PneumaticCraftAPIHandler implements IPneumaticCraftInterface{
    private final static PneumaticCraftAPIHandler INSTANCE = new PneumaticCraftAPIHandler();
    public final Map<Fluid, Integer> liquidXPs = new HashMap<Fluid, Integer>();
    public final Map<String, Integer> liquidFuels = new HashMap<String, Integer>();

    public static PneumaticCraftAPIHandler getInstance(){
        return INSTANCE;
    }

    @Override
    public IPneumaticRecipeRegistry getRecipeRegistry(){
        return PneumaticRecipeRegistry.getInstance();
    }

    @Override
    public IAirHandlerSupplier getAirHandlerSupplier(){
        return AirHandlerSupplier.getInstance();
    }

    @Override
    public IPneumaticHelmetRegistry getHelmetRegistry(){
        return PneumaticHelmetRegistry.getInstance();
    }

    @Override
    public IDroneRegistry getDroneRegistry(){
        return DroneRegistry.getInstance();
    }

    @Override
    public IHeatRegistry getHeatRegistry(){
        return HeatExchangerManager.getInstance();
    }

    @Override
    public int getProtectingSecurityStations(World world, BlockPos pos, EntityPlayer player, boolean showRangeLines){
        if(world.isRemote) throw new IllegalArgumentException("This method can only be called from the server side!");
        return PneumaticCraftUtils.getProtectingSecurityStations(world, pos, player, showRangeLines, false);
    }

    @Override
    public void registerXPLiquid(Fluid fluid, int liquidToPointRatio){
        if(fluid == null) throw new NullPointerException("Fluid can't be null!");
        if(liquidToPointRatio <= 0) throw new IllegalArgumentException("liquidToPointRatio can't be <= 0");
        liquidXPs.put(fluid, liquidToPointRatio);
    }

    @Override
    public void registerFuel(Fluid fluid, int mLPerBucket){
        if(fluid == null) throw new NullPointerException("Fluid can't be null!");
        if(mLPerBucket < 0) throw new IllegalArgumentException("mLPerBucket can't be < 0");
        if(liquidFuels.containsKey(fluid.getName())) {
            Log.info("Overriding liquid fuel entry " + fluid.getLocalizedName(new FluidStack(fluid, 1)) + " (" + fluid.getName() + ") with a fuel value of " + mLPerBucket + " (previously " + liquidFuels.get(fluid.getName()) + ")");
            if(mLPerBucket == 0) liquidFuels.remove(fluid.getName());
        }
        if(mLPerBucket > 0) liquidFuels.put(fluid.getName(), mLPerBucket);
    }

    @Override
    public IClientRegistry getGuiRegistry(){
        return GuiRegistry.getInstance();
    }

    @Override
    public ISensorRegistry getSensorRegistry(){
        return SensorHandler.getInstance();
    }

    @Override
    public IItemRegistry getItemRegistry(){
        return ItemRegistry.getInstance();
    }
}
