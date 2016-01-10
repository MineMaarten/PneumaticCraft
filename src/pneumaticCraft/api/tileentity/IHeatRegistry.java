package pneumaticCraft.api.tileentity;

import net.minecraft.block.Block;
import pneumaticCraft.api.heat.HeatBehaviour;
import pneumaticCraft.api.heat.IHeatExchangerLogic;

public interface IHeatRegistry{
    public IHeatExchangerLogic getHeatExchangerLogic();

    public void registerBlockExchanger(Block block, double temperature, double thermalResistance);

    public void registerHeatBehaviour(Class<? extends HeatBehaviour> heatBehaviour);
}
