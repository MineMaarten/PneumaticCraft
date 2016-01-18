package pneumaticCraft.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import pneumaticCraft.common.config.Config;
import pneumaticCraft.lib.Names;

public class GuiConfigHandler implements IModGuiFactory{

    @Override
    public void initialize(Minecraft minecraftInstance){

    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass(){
        return ConfigGui.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories(){

        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element){

        return null;
    }

    public static class ConfigGui extends GuiConfig{

        public ConfigGui(GuiScreen parent){

            super(parent, getConfigElements(), Names.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(Config.config.toString()));
        }

        private static List<IConfigElement> getConfigElements(){
            List<IConfigElement> list = new ArrayList<IConfigElement>();
            for(String category : Config.CATEGORIES) {
                list.add(new DummyCategoryElement(category, category, new ConfigElement(Config.config.getCategory(category).setRequiresMcRestart(!Config.NO_MC_RESTART_CATS.contains(category))).getChildElements()));
            }

            return list;
        }
    }

}
