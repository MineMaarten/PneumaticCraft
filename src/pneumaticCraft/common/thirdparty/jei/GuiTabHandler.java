package pneumaticCraft.common.thirdparty.jei;

import java.awt.Rectangle;
import java.util.List;

import mezz.jei.api.gui.IAdvancedGuiHandler;
import pneumaticCraft.client.gui.GuiPneumaticContainerBase;

public class GuiTabHandler implements IAdvancedGuiHandler<GuiPneumaticContainerBase>{

    @Override
    public Class<GuiPneumaticContainerBase> getGuiContainerClass(){
        return GuiPneumaticContainerBase.class;
    }

    @Override
    public List<Rectangle> getGuiExtraAreas(GuiPneumaticContainerBase guiContainer){
        return guiContainer.getTabRectangles();
    }

}
