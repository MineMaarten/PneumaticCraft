package pneumaticCraft.common.thirdparty.mcmultipart;

import pneumaticCraft.common.tileentity.TileEntityPressureTube;
import pneumaticCraft.lib.PneumaticValues;

public class PartAdvancedPressureTube extends PartPressureTube{

    @Override
    protected TileEntityPressureTube getNewTube(){
        return new TileEntityPressureTube(PneumaticValues.DANGER_PRESSURE_ADVANCED_PRESSURE_TUBE, PneumaticValues.MAX_PRESSURE_ADVANCED_PRESSURE_TUBE, PneumaticValues.VOLUME_ADVANCED_PRESSURE_TUBE);
    }
}
