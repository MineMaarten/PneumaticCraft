package pneumaticCraft.common.thirdparty.forestry;

import net.minecraftforge.fluids.FluidRegistry;
import pneumaticCraft.api.PneumaticRegistry;
import pneumaticCraft.common.thirdparty.IThirdParty;

public class Forestry implements IThirdParty{

    @Override
    public void preInit(){

    }

    @Override
    public void init(){
        PneumaticRegistry.getInstance().registerFuel(FluidRegistry.getFluid("biomass"), 500000);
        PneumaticRegistry.getInstance().registerFuel(FluidRegistry.getFluid("bioethanol"), 500000);
    }

    @Override
    public void postInit(){

    }

    @Override
    public void clientSide(){}

    @Override
    public void clientInit(){}
}
