package pneumaticCraft.common.thirdparty.mfr;

import net.minecraftforge.fluids.FluidRegistry;
import pneumaticCraft.api.PneumaticRegistry;
import pneumaticCraft.common.thirdparty.IThirdParty;

public class MFR implements IThirdParty{

    @Override
    public void preInit(){

    }

    @Override
    public void init(){
        PneumaticRegistry.getInstance().registerXPLiquid(FluidRegistry.getFluid("mobessence"), 77);
    }

    @Override
    public void postInit(){

    }

    @Override
    public void clientSide(){

    }

    @Override
    public void clientInit(){}

}
