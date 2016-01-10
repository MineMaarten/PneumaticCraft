package pneumaticCraft.common.block.tubes;

import java.util.List;

import net.minecraft.util.EnumChatFormatting;
import pneumaticCraft.client.model.IBaseModel;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.lib.Names;

public class ModuleSafetyValve extends TubeModuleRedstoneReceiving{
    private final IBaseModel model = null;//TODO 1.8 new ModelSafetyValve();

    @Override
    public void update(){
        super.update();
        if(!pressureTube.world().isRemote) {
            if(pressureTube.getAirHandler(null).getPressure() > getThreshold()) {
                pressureTube.getAirHandler(null).airLeak(dir);
            }
        }
    }

    @Override
    public void addInfo(List<String> curInfo){
        super.addInfo(curInfo);
        curInfo.add("Threshold: " + EnumChatFormatting.WHITE + PneumaticCraftUtils.roundNumberTo(getThreshold(), 1) + " bar");
    }

    @Override
    public String getType(){
        return Names.MODULE_SAFETY_VALVE;
    }

    @Override
    public IBaseModel getModel(){
        return model;
    }

    @Override
    public void addItemDescription(List<String> curInfo){
        curInfo.add(EnumChatFormatting.BLUE + "Formula: Threshold(bar) = 7.5 - Redstone x 0.5");
        curInfo.add("This module will release high pressure gases");
        curInfo.add("when a certain threshold's reached. Though");
        curInfo.add("it prevents overpressure it can be counted");
        curInfo.add("as energy loss.");
    }
}
