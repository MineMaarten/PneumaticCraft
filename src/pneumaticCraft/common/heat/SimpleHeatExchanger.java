package pneumaticCraft.common.heat;

import net.minecraft.util.EnumFacing;
import pneumaticCraft.api.heat.IHeatExchangerLogic;
import pneumaticCraft.api.tileentity.IHeatExchanger;

public class SimpleHeatExchanger implements IHeatExchanger{
    private final IHeatExchangerLogic logic;

    public SimpleHeatExchanger(IHeatExchangerLogic logic){
        this.logic = logic;
    }

    @Override
    public IHeatExchangerLogic getHeatExchangerLogic(EnumFacing side){
        return logic;
    }

}
