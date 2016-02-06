package pneumaticCraft.common.thirdparty.computercraft;

import li.cil.oc.api.Driver;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import pneumaticCraft.api.item.IItemRegistry.EnumUpgrade;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.item.ItemRegistry;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.progwidgets.WidgetRegistrator;
import pneumaticCraft.common.thirdparty.IThirdParty;
import pneumaticCraft.lib.ModIds;
import pneumaticCraft.lib.Names;

public class OpenComputers implements IThirdParty{
    public Block droneInterface;

    @Override
    public void preInit(){
        droneInterface = new BlockDroneInterface(Material.iron).setUnlocalizedName("droneInterface");
        Blockss.registerBlock(droneInterface);
        GameRegistry.registerTileEntity(TileEntityDroneInterface.class, "droneInterface");
        WidgetRegistrator.register(new ProgWidgetCC());
    }

    @Override
    public void init(){
        if(!Loader.isModLoaded(ModIds.COMPUTERCRAFT)) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(droneInterface), true, " u ", "mp ", "iii", 'u', ItemRegistry.getInstance().getUpgrade(EnumUpgrade.RANGE), 'm', Items.ender_pearl, 'p', Itemss.printedCircuitBoard, 'i', Names.INGOT_IRON_COMPRESSED));
        }
        if(Loader.isModLoaded(ModIds.OPEN_COMPUTERS)) {
            initializeDrivers();
        }
    }

    @Optional.Method(modid = ModIds.OPEN_COMPUTERS)
    private void initializeDrivers(){
        Driver.add(new DriverPneumaticCraft());
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
