package pneumaticCraft.common.thirdparty.jei;

import java.util.List;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.lib.Textures;

public abstract class JEISpecialCraftingManager<T> extends PneumaticCraftPlugins<T>{

    private List<String> text;
    private final List<MultipleInputOutputRecipe> allRecipes = getAllRecipes();

    public JEISpecialCraftingManager(IModRegistry registry, IJeiHelpers jeiHelpers){
        super(jeiHelpers);
        registry.addRecipes(allRecipes);
    }

    protected void setText(String localizationKey){
        text = PneumaticCraftUtils.convertStringIntoList(I18n.format(localizationKey), 30);
    }

    @Override
    public ResourceDrawable getGuiTexture(){
        return new ResourceDrawable(Textures.GUI_NEI_MISC_RECIPES, 40, 79, 0, 0, 82, 18){
            @Override
            public int getWidth(){
                return 160;
            }
        };
    }

    @Override
    public void drawExtras(Minecraft minecraft){
        if(text != null) {
            for(int i = 0; i < text.size(); i++) {
                Minecraft.getMinecraft().fontRendererObj.drawString(text.get(i), 5, 20 + i * 10, 0xFF000000);
            }
        }

        drawProgressBar(63, 80, 82, 0, 38, 18, StartDirection.LEFT);
    }

    protected abstract List<MultipleInputOutputRecipe> getAllRecipes();
}
