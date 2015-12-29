package pneumaticCraft.common.thirdparty.buildcraft;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.thirdparty.IThirdParty;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.common.util.PneumaticCraftUtils.EnumBuildcraftModule;
import pneumaticCraft.lib.Names;

public class BuildCraft implements IThirdParty{

    @Override
    public void preInit(){}

    @Override
    public void init(){
        ItemStack stoneGear = PneumaticCraftUtils.getBuildcraftItemStack(EnumBuildcraftModule.CORE, "stoneGearItem");

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Itemss.compressedIronGear), " i ", "isi", " i ", 'i', Names.INGOT_IRON_COMPRESSED, 's', stoneGear));

        //PneumaticRegistry.getInstance().registerFuel(FluidRegistry.getFluid("fuel"), 1500000);
    }

    @Override
    public void postInit(){}

    @Override
    public void clientSide(){}

    @Override
    public void clientInit(){}

}
