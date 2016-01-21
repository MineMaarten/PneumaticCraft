package pneumaticCraft.common.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import pneumaticCraft.common.config.Config;
import pneumaticCraft.common.itemBlock.ItemBlockOmnidirectionalHopper;
import pneumaticCraft.common.itemBlock.ItemBlockPneumaticCraft;
import pneumaticCraft.common.thirdparty.ThirdPartyManager;
import pneumaticCraft.lib.Names;
import pneumaticCraft.lib.PneumaticValues;

public class Blockss{

    public static Block pressureTube;
    public static Block airCompressor;
    public static Block airCannon;
    public static Block pressureChamberWall;
    public static Block pressureChamberGlass;
    public static Block pressureChamberValve;
    public static Block pressureChamberInterface;
    public static Block chargingStation;
    public static Block elevatorBase;
    public static Block elevatorFrame;
    public static Block vacuumPump;
    public static Block pneumaticDoorBase;
    public static Block pneumaticDoor;
    public static Block assemblyPlatform;
    public static Block assemblyIOUnit;
    public static Block assemblyDrill;
    public static Block assemblyLaser;
    public static Block assemblyController;
    public static Block advancedPressureTube;
    public static Block compressedIron;
    public static Block uvLightBox;
    public static Block securityStation;
    public static Block universalSensor;
    public static Block universalActuator;
    public static Block aerialInterface;
    public static Block electrostaticCompressor;
    public static Block aphorismTile;
    public static Block omnidirectionalHopper;
    public static Block elevatorCaller;
    public static Block programmer;
    public static Block creativeCompressor;
    public static Block plasticMixer;
    public static Block liquidCompressor;
    public static Block advancedLiquidCompressor;
    public static Block advancedAirCompressor;
    public static Block liquidHopper;
    public static Block droneRedstoneEmitter;
    public static Block heatSink;
    public static Block vortexTube;
    public static Block programmableController;
    public static Block gasLift;
    public static Block refinery;
    public static Block thermopneumaticProcessingPlant;
    public static Block keroseneLamp;
    public static Block keroseneLampLight;
    public static Block sentryTurret;
    public static List<Block> blocks = new ArrayList<Block>();

    public static void init(){
        pressureTube = new BlockPressureTube(Material.iron, PneumaticValues.DANGER_PRESSURE_PRESSURE_TUBE, PneumaticValues.MAX_PRESSURE_PRESSURE_TUBE, PneumaticValues.VOLUME_PRESSURE_TUBE).setUnlocalizedName("pressureTube");
        advancedPressureTube = new BlockPressureTube(Material.iron, PneumaticValues.DANGER_PRESSURE_ADVANCED_PRESSURE_TUBE, PneumaticValues.MAX_PRESSURE_ADVANCED_PRESSURE_TUBE, PneumaticValues.VOLUME_ADVANCED_PRESSURE_TUBE).setUnlocalizedName("advancedPressureTube");
        airCompressor = new BlockAirCompressor(Material.iron).setUnlocalizedName("airCompressor");
        advancedAirCompressor = new BlockAdvancedAirCompressor(Material.iron).setUnlocalizedName("advancedAirCompressor");
        airCannon = new BlockAirCannon(Material.iron).setUnlocalizedName("airCannon");
        pressureChamberWall = new BlockPressureChamberWall(Material.iron).setResistance(2000.0F).setUnlocalizedName("pressureChamberWall");
        pressureChamberGlass = new BlockPressureChamberGlass(Material.iron).setResistance(2000.0F).setUnlocalizedName("pressureChamberGlass");
        pressureChamberValve = new BlockPressureChamberValve(Material.iron).setResistance(2000.0F).setUnlocalizedName("pressureChamberValve");
        chargingStation = new BlockChargingStation(Material.iron).setUnlocalizedName("chargingStation");
        elevatorBase = new BlockElevatorBase(Material.iron).setUnlocalizedName("elevatorBase");
        elevatorFrame = new BlockElevatorFrame(Material.iron).setUnlocalizedName("elevatorFrame");
        pressureChamberInterface = new BlockPressureChamberInterface(Material.iron).setResistance(2000.0F).setUnlocalizedName("pressureChamberInterface");
        vacuumPump = new BlockVacuumPump(Material.iron).setUnlocalizedName("vacuumPump");
        pneumaticDoorBase = new BlockPneumaticDoorBase(Material.iron).setUnlocalizedName("pneumaticDoorBase");
        pneumaticDoor = new BlockPneumaticDoor(Material.iron).setUnlocalizedName("pneumaticDoor");
        assemblyIOUnit = new BlockAssemblyIOUnit(Material.iron).setUnlocalizedName("assemblyIOUnit");
        assemblyPlatform = new BlockAssemblyPlatform(Material.iron).setUnlocalizedName("assemblyPlatform");
        assemblyDrill = new BlockAssemblyDrill(Material.iron).setUnlocalizedName("assemblyDrill");
        assemblyLaser = new BlockAssemblyLaser(Material.iron).setUnlocalizedName("assemblyLaser");
        assemblyController = new BlockAssemblyController(Material.iron).setUnlocalizedName("assemblyController");
        compressedIron = new BlockCompressedIron(Material.iron).setStepSound(Block.soundTypeMetal).setUnlocalizedName("compressedIronBlock");
        uvLightBox = new BlockUVLightBox(Material.iron).setUnlocalizedName("uvLightBox");
        securityStation = new BlockSecurityStation(Material.iron).setUnlocalizedName("securityStation");
        universalSensor = new BlockUniversalSensor(Material.iron).setUnlocalizedName("universalSensor");
        universalActuator = new BlockUniversalActuator(Material.iron).setUnlocalizedName("universalActuator");
        aerialInterface = new BlockAerialInterface(Material.iron).setUnlocalizedName("aerialInterface");
        electrostaticCompressor = new BlockElectrostaticCompressor(Material.iron).setUnlocalizedName("electrostaticCompressor");
        aphorismTile = new BlockAphorismTile(Material.rock).setHardness(1.5F).setResistance(4.0F).setUnlocalizedName("aphorismTile");
        omnidirectionalHopper = new BlockOmnidirectionalHopper(Material.iron).setUnlocalizedName("omnidirectionalHopper");
        liquidHopper = new BlockLiquidHopper(Material.iron).setUnlocalizedName("liquidHopper");
        elevatorCaller = new BlockElevatorCaller(Material.iron).setUnlocalizedName("elevatorCaller");
        programmer = new BlockProgrammer(Material.iron).setUnlocalizedName("programmer");
        creativeCompressor = new BlockCreativeCompressor(Material.iron).setUnlocalizedName("creativeCompressor");
        plasticMixer = new BlockPlasticMixer(Material.iron).setUnlocalizedName("plasticMixer");
        liquidCompressor = new BlockLiquidCompressor(Material.iron).setUnlocalizedName("liquidCompressor");
        advancedLiquidCompressor = new BlockAdvancedLiquidCompressor(Material.iron).setUnlocalizedName("advancedLiquidCompressor");
        droneRedstoneEmitter = new BlockDroneRedstoneEmitter().setUnlocalizedName("droneRedstoneEmitter");
        heatSink = new BlockHeatSink(Material.iron).setUnlocalizedName("heatSink");
        vortexTube = new BlockVortexTube(Material.iron).setUnlocalizedName("vortexTube");
        programmableController = new BlockProgrammableController(Material.iron).setUnlocalizedName("programmableController");
        gasLift = new BlockGasLift(Material.iron).setUnlocalizedName("gasLift");
        refinery = new BlockRefinery(Material.iron).setUnlocalizedName("refinery");
        thermopneumaticProcessingPlant = new BlockThermopneumaticProcessingPlant(Material.iron).setUnlocalizedName("thermopneumaticProcessingPlant");
        keroseneLamp = new BlockKeroseneLamp(Material.iron).setUnlocalizedName("keroseneLamp");
        keroseneLampLight = new BlockKeroseneLampLight().setUnlocalizedName("keroseneLampLight");
        sentryTurret = new BlockSentryTurret(Material.iron).setUnlocalizedName("sentryTurret");

        registerBlocks();

        OreDictionary.registerOre(Names.BLOCK_IRON_COMPRESSED, compressedIron);
    }

    private static void registerBlocks(){
        registerBlock(pressureTube);
        registerBlock(airCompressor);
        registerBlock(advancedAirCompressor);
        registerBlock(airCannon);
        registerBlock(pressureChamberWall);
        registerBlock(pressureChamberGlass);
        registerBlock(pressureChamberValve);
        registerBlock(chargingStation);
        registerBlock(elevatorBase);
        registerBlock(elevatorFrame);
        registerBlock(pressureChamberInterface);
        registerBlock(vacuumPump);
        registerBlock(pneumaticDoorBase);
        registerBlock(pneumaticDoor);
        registerBlock(assemblyIOUnit);
        registerBlock(assemblyPlatform);
        registerBlock(assemblyDrill);
        registerBlock(assemblyLaser);
        registerBlock(assemblyController);
        registerBlock(advancedPressureTube);
        registerBlock(compressedIron);
        registerBlock(uvLightBox);
        registerBlock(securityStation);
        registerBlock(universalSensor);
        //     registerBlock(universalActuator);
        registerBlock(aerialInterface);
        registerBlock(electrostaticCompressor);
        registerBlock(aphorismTile);
        registerBlock(omnidirectionalHopper, ItemBlockOmnidirectionalHopper.class);
        registerBlock(liquidHopper, ItemBlockOmnidirectionalHopper.class);
        registerBlock(elevatorCaller);
        registerBlock(programmer);
        registerBlock(creativeCompressor);
        registerBlock(plasticMixer);
        registerBlock(liquidCompressor);
        registerBlock(advancedLiquidCompressor);
        registerBlock(droneRedstoneEmitter);
        registerBlock(heatSink);
        registerBlock(vortexTube);
        registerBlock(programmableController);
        registerBlock(gasLift);
        registerBlock(refinery);
        registerBlock(thermopneumaticProcessingPlant);
        registerBlock(keroseneLamp);
        if(!Config.disableKeroseneLampFakeAirBlock) registerBlock(keroseneLampLight);
        registerBlock(sentryTurret);
    }

    public static void registerBlock(Block block){
        registerBlock(block, ItemBlockPneumaticCraft.class);
    }

    private static void registerBlock(Block block, Class<? extends ItemBlockPneumaticCraft> itemBlockClass){
        GameRegistry.registerBlock(block, itemBlockClass, block.getUnlocalizedName().substring("tile.".length()));
        ThirdPartyManager.instance().onBlockRegistry(block);
        blocks.add(block);
    }
}
