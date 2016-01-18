package pneumaticCraft.proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import pneumaticCraft.PneumaticCraft;
import pneumaticCraft.api.client.pneumaticHelmet.IUpgradeRenderHandler;
import pneumaticCraft.client.AreaShowManager;
import pneumaticCraft.client.ClientEventHandler;
import pneumaticCraft.client.ClientTickHandler;
import pneumaticCraft.client.KeyHandler;
import pneumaticCraft.client.gui.pneumaticHelmet.GuiHelmetMainScreen;
import pneumaticCraft.client.model.IBaseModel;
import pneumaticCraft.client.render.entity.RenderDrone;
import pneumaticCraft.client.render.entity.RenderEntityRing;
import pneumaticCraft.client.render.entity.RenderEntityVortex;
import pneumaticCraft.client.render.pneumaticArmor.CoordTrackUpgradeHandler;
import pneumaticCraft.client.render.pneumaticArmor.HUDHandler;
import pneumaticCraft.client.render.pneumaticArmor.UpgradeRenderHandlerList;
import pneumaticCraft.client.render.pneumaticArmor.entitytracker.EntityTrackHandler;
import pneumaticCraft.client.semiblock.ClientSemiBlockManager;
import pneumaticCraft.common.CommonHUDHandler;
import pneumaticCraft.common.HackTickHandler;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.config.Config;
import pneumaticCraft.common.entity.EntityProgrammableController;
import pneumaticCraft.common.entity.EntityRing;
import pneumaticCraft.common.entity.living.EntityDrone;
import pneumaticCraft.common.entity.living.EntityLogisticsDrone;
import pneumaticCraft.common.entity.projectile.EntityVortex;
import pneumaticCraft.common.fluid.Fluids;
import pneumaticCraft.common.item.ItemPneumatic;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.itemBlock.ItemBlockPneumaticCraft;
import pneumaticCraft.common.recipes.CraftingRegistrator;
import pneumaticCraft.common.semiblock.ItemSemiBlockBase;
import pneumaticCraft.common.thirdparty.ThirdPartyManager;
import pneumaticCraft.common.thirdparty.igwmod.IGWSupportNotifier;
import pneumaticCraft.common.tileentity.TileEntityPlasticMixer;
import pneumaticCraft.lib.Log;
import pneumaticCraft.lib.ModIds;
import pneumaticCraft.lib.Names;
import pneumaticCraft.lib.Textures;

public class ClientProxy extends CommonProxy{

    private final HackTickHandler clientHackTickHandler = new HackTickHandler();
    public final Map<String, Integer> keybindToKeyCodes = new HashMap<String, Integer>();

    @Override
    public void preInit(){
        OBJLoader.instance.addDomain(Names.MOD_ID);

        for(Fluid fluid : Fluids.fluids) {
            ModelLoader.setBucketModelDefinition(Fluids.getBucket(fluid));
        }

        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());

        MinecraftForge.EVENT_BUS.register(HUDHandler.instance());
        MinecraftForge.EVENT_BUS.register(ClientTickHandler.instance());
        MinecraftForge.EVENT_BUS.register(getHackTickHandler());
        MinecraftForge.EVENT_BUS.register(clientHudHandler = new CommonHUDHandler());
        MinecraftForge.EVENT_BUS.register(new ClientSemiBlockManager());

        MinecraftForge.EVENT_BUS.register(HUDHandler.instance().getSpecificRenderer(CoordTrackUpgradeHandler.class));
        MinecraftForge.EVENT_BUS.register(AreaShowManager.getInstance());

        if(!Loader.isModLoaded(ModIds.NOT_ENOUGH_KEYS) || !Config.config.get("Third_Party_Enabling", ModIds.NOT_ENOUGH_KEYS, true).getBoolean()) {
            MinecraftForge.EVENT_BUS.register(KeyHandler.getInstance());
        } else KeyHandler.getInstance();
        ThirdPartyManager.instance().clientSide();

        /*  if(Config.enableUpdateChecker) {
              UpdateChecker.instance().start();
              MinecraftForge.EVENT_BUS.register(UpdateChecker.instance());
          }*/
        EntityTrackHandler.registerDefaultEntries();
        getAllKeybindsFromOptionsFile();
        new IGWSupportNotifier();
    }

    @Override
    public void init(){
        for(Block block : Blockss.blocks) {
            Item item = Item.getItemFromBlock(block);
            if(item instanceof ItemBlockPneumaticCraft) ((ItemBlockPneumaticCraft)item).registerItemVariants();
        }
        for(Item item : Itemss.items) {
            if(item instanceof ItemPneumatic) ((ItemPneumatic)item).registerItemVariants();
            else if(!(item instanceof ItemBucket)) {
                List<ItemStack> stacks = new ArrayList<ItemStack>();
                item.getSubItems(item, null, stacks);
                for(ItemStack stack : stacks) {
                    ResourceLocation resLoc = new ResourceLocation(Names.MOD_ID, stack.getUnlocalizedName().substring(5));
                    ModelBakery.registerItemVariants(item, resLoc);
                    Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, stack.getItemDamage(), new ModelResourceLocation(resLoc, "inventory"));
                }
            }
        }

        for(int i = 0; i < 16; i++) { //Only register these recipes client side, so NEI compatibility works, but drones don't lose their program when dyed.
            ItemStack drone = new ItemStack(Itemss.drone);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("color", ItemDye.dyeColors[i]);
            drone.setTagCompound(tag);
            GameRegistry.addRecipe(new ShapelessOreRecipe(drone, Itemss.drone, TileEntityPlasticMixer.DYES[i]));
        }
        CraftingRegistrator.addShapelessRecipe(new ItemStack(Itemss.drone), new ItemStack(Itemss.logisticsDrone), Itemss.printedCircuitBoard);

        ThirdPartyManager.instance().clientInit();

        // RenderingRegistry.registerBlockHandler(new RendererSpecialBlock());
        /* registerBaseModelRenderer(Blockss.airCompressor, TileEntityAirCompressor.class, new ModelAirCompressor("airCompressor"));
         registerBaseModelRenderer(Blockss.advancedAirCompressor, TileEntityAdvancedAirCompressor.class, new ModelAirCompressor("advancedAirCompressor"));
         registerBaseModelRenderer(Blockss.assemblyController, TileEntityAssemblyController.class, new ModelAssemblyController());
         registerBaseModelRenderer(Blockss.assemblyDrill, TileEntityAssemblyDrill.class, new ModelAssemblyDrill());
         registerBaseModelRenderer(Blockss.assemblyIOUnit, TileEntityAssemblyIOUnit.class, new ModelAssemblyIOUnit());
         registerBaseModelRenderer(Blockss.assemblyLaser, TileEntityAssemblyLaser.class, new ModelAssemblyLaser());
         registerBaseModelRenderer(Blockss.assemblyPlatform, TileEntityAssemblyPlatform.class, new ModelAssemblyPlatform());
         registerBaseModelRenderer(Blockss.chargingStation, TileEntityChargingStation.class, new ModelChargingStation());
         registerBaseModelRenderer(Blockss.creativeCompressor, TileEntityCreativeCompressor.class, new BaseModel("creativeCompressor.obj"));
         registerBaseModelRenderer(Blockss.electrostaticCompressor, TileEntityElectrostaticCompressor.class, new BaseModel("electrostaticCompressor.obj"));
         registerBaseModelRenderer(Blockss.elevatorBase, TileEntityElevatorBase.class, new ModelElevatorBase());
         registerBaseModelRenderer(Blockss.pneumaticDoor, TileEntityPneumaticDoor.class, new ModelPneumaticDoor());
         registerBaseModelRenderer(Blockss.pneumaticDoorBase, TileEntityPneumaticDoorBase.class, new ModelPneumaticDoorBase());
         registerBaseModelRenderer(Blockss.pressureChamberInterface, TileEntityPressureChamberInterface.class, new ModelPressureChamberInterface());
         registerBaseModelRenderer(Blockss.securityStation, TileEntitySecurityStation.class, new ModelComputer(Textures.MODEL_SECURITY_STATION));
         registerBaseModelRenderer(Blockss.universalSensor, TileEntityUniversalSensor.class, new ModelUniversalSensor());
         registerBaseModelRenderer(Blockss.uvLightBox, TileEntityUVLightBox.class, new ModelUVLightBox());
         registerBaseModelRenderer(Blockss.vacuumPump, TileEntityVacuumPump.class, new ModelVacuumPump());
         registerBaseModelRenderer(Blockss.omnidirectionalHopper, TileEntityOmnidirectionalHopper.class, new ModelOmnidirectionalHopper(Textures.MODEL_OMNIDIRECTIONAL_HOPPER));
         registerBaseModelRenderer(Blockss.liquidHopper, TileEntityLiquidHopper.class, new ModelLiquidHopper());
         registerBaseModelRenderer(Blockss.programmer, TileEntityProgrammer.class, new ModelComputer(Textures.MODEL_PROGRAMMER));
         registerBaseModelRenderer(Blockss.plasticMixer, TileEntityPlasticMixer.class, new ModelPlasticMixer());
         registerBaseModelRenderer(Blockss.liquidCompressor, TileEntityLiquidCompressor.class, new BaseModel("liquidCompressor.obj"));
         registerBaseModelRenderer(Blockss.advancedLiquidCompressor, TileEntityAdvancedLiquidCompressor.class, new BaseModel("liquidCompressor.obj", "advancedLiquidCompressor.png"));
         registerBaseModelRenderer(Blockss.heatSink, TileEntityHeatSink.class, new ModelHeatSink());
         registerBaseModelRenderer(Blockss.vortexTube, TileEntityVortexTube.class, new ModelVortexTube());
         registerBaseModelRenderer(Blockss.thermopneumaticProcessingPlant, TileEntityThermopneumaticProcessingPlant.class, new ModelThermopneumaticProcessingPlant());
         registerBaseModelRenderer(Blockss.refinery, TileEntityRefinery.class, new ModelRefinery());
         registerBaseModelRenderer(Blockss.gasLift, TileEntityGasLift.class, new ModelGasLift());
         registerBaseModelRenderer(Blockss.keroseneLamp, TileEntityKeroseneLamp.class, new ModelKeroseneLamp());
         registerBaseModelRenderer(Blockss.sentryTurret, TileEntitySentryTurret.class, new ModelSentryTurret());

         ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPressureTube.class, new RenderPressureTube());
         ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAirCannon.class, new RenderAirCannon());
         // ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElevatorBase.class, new RenderElevatorBase());
         // ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAdvancedPressureTube.class, new RenderAdvancedPressureTube());
         ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAphorismTile.class, new RenderAphorismTile());
         ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElevatorCaller.class, new RenderElevatorCaller());
         // ClientRegistry.bindTileEntitySpecialRenderer(TileEntityProgrammableController.class, new RenderProgrammableController());
         // ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySentryTurret.class, new RenderSentryTurret());*/

        RenderingRegistry.registerEntityRenderingHandler(EntityVortex.class, new RenderEntityVortex());
        RenderingRegistry.registerEntityRenderingHandler(EntityDrone.class, new RenderDrone(false));
        RenderingRegistry.registerEntityRenderingHandler(EntityLogisticsDrone.class, new RenderDrone(true));
        RenderingRegistry.registerEntityRenderingHandler(EntityProgrammableController.class, new RenderDrone(false));

        RenderingRegistry.registerEntityRenderingHandler(EntityRing.class, new RenderEntityRing());
        EntityRegistry.registerModEntity(EntityRing.class, "Ring", 100, PneumaticCraft.instance, 80, 1, true);

        registerModuleRenderers();
        super.init();
    }

    public static void registerBaseModelRenderer(Block block, Class<? extends TileEntity> tileEntityClass, IBaseModel model){
        /*  if(model instanceof BaseModel) {
              ((BaseModel)model).rotatable = ((BlockPneumaticCraft)block).isRotatable();
          }*/
        registerBaseModelRenderer(Item.getItemFromBlock(block), tileEntityClass, model);
    }

    private static void registerBaseModelRenderer(Item item, Class<? extends TileEntity> tileEntityClass, IBaseModel model){
        /*RenderModelBase renderer = new RenderModelBase(model);
        ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, renderer);*/
    }

    @Override
    public boolean isSneakingInGui(){

        return GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak);
    }

    private void getAllKeybindsFromOptionsFile(){
        File optionsFile = new File(FMLClientHandler.instance().getClient().mcDataDir, "options.txt");
        if(optionsFile.exists()) {
            try {
                BufferedReader bufferedreader = new BufferedReader(new FileReader(optionsFile));

                try {
                    String s = "";
                    while((s = bufferedreader.readLine()) != null) {
                        try {
                            String[] astring = s.split(":");
                            if(astring[0].startsWith("key_")) {
                                keybindToKeyCodes.put(astring[0].substring(4), Integer.parseInt(astring[1]));
                            }
                        } catch(Exception exception) {
                            Log.warning("Skipping bad option: " + s);
                        }
                    }
                } finally {
                    bufferedreader.close();
                }
            } catch(Exception exception1) {
                Log.error("Failed to load options");
                exception1.printStackTrace();
            }
        }
    }

    @Override
    public void postInit(){
        EntityTrackHandler.init();
        GuiHelmetMainScreen.init();
    }

    public void registerModuleRenderers(){

    }

    @Override
    public void initConfig(Configuration config){
        for(IUpgradeRenderHandler renderHandler : UpgradeRenderHandlerList.instance().upgradeRenderers) {
            renderHandler.initConfig(config);
        }
    }

    @Override
    public World getClientWorld(){
        return FMLClientHandler.instance().getClient().theWorld;
    }

    @Override
    public EntityPlayer getPlayer(){
        return FMLClientHandler.instance().getClient().thePlayer;
    }

    @Override
    public int getArmorRenderID(String armorName){
        return 0;
    }

    @Override
    public void registerVillagerSkins(){
        VillagerRegistry.instance().registerVillagerSkin(Config.villagerMechanicID, new ResourceLocation(Textures.VILLAGER_MECHANIC));
    }

    @Override
    public HackTickHandler getHackTickHandler(){
        return clientHackTickHandler;
    }

    @Override
    public void registerSemiBlockRenderer(ItemSemiBlockBase semiBlock){
        //TODO 1.8 MinecraftForgeClient.registerItemRenderer(semiBlock, new RenderItemSemiBlock(semiBlock.semiBlockId));
    }
}
