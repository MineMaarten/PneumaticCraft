package pneumaticCraft.common.config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pneumaticCraft.PneumaticCraft;
import pneumaticCraft.lib.Log;
import pneumaticCraft.lib.Names;

public class Config{
    public static Configuration config;

    public static int villagerMechanicID;

    public static double oilGenerationChance;
    public static boolean stopDroneAI;
    public static boolean disableKeroseneLampFakeAirBlock;

    public static int configCompressedIngotLossRate;
    public static int elevatorBaseBlocksPerBase;
    public static boolean useHelmetModel;
    private static int programmerDifficulty;

    public static boolean enableUpdateChecker;
    public static boolean convertMultipartsToBlocks;

    public static int pneumaticGeneratorEfficiency;
    public static int electricCompressorEfficiency;
    public static int pneumaticEngineEfficiency;
    public static int kineticCompressorEfficiency;
    public static int pneumaticPumpEfficiency;
    public static int fluxCompressorEfficiency;
    public static int pneumaticDynamoEfficiency;
    public static boolean enableElectricCompressorRecipe;
    public static boolean enablePneumaticGeneratorRecipe;
    public static boolean enablePneumaticPumpRecipe;

    public static boolean enableCoalToDiamondsRecipe;

    public static boolean enableDroneSuffocationDamage;
    public static boolean enableDungeonLoot;

    public static float configMinigunDamage;

    public static final String[] CATEGORIES = new String[]{Configuration.CATEGORY_GENERAL, "machine_properties", "advanced", "recipe_enabling", "third_party_enabling"};
    public static List<String> NO_MC_RESTART_CATS = Arrays.asList(new String[]{"machine_properties"});
    private static ISubConfig[] subConfigs = new ISubConfig[]{new AmadronOfferSettings(), AmadronOfferStaticConfig.INSTANCE, new AmadronOfferPeriodicConfig(), new ProgWidgetConfig()};

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs){
        if(eventArgs.modID.equals(Names.MOD_ID)) {
            init(null);
        }
    }

    public static void init(File configFile){
        if(configFile != null) {
            config = new Configuration(configFile);
            config.load(); // get the actual data from the file.

            for(ISubConfig subConfig : subConfigs) {
                File subFolder = new File(configFile.getAbsolutePath().substring(0, configFile.getAbsolutePath().length() - 4) + File.separator);
                subFolder.mkdirs();
                File subFile = new File(subFolder, subConfig.getFolderName() + ".cfg");
                try {
                    subConfig.init(subFile);
                } catch(IOException e) {
                    Log.error("Config file " + subConfig.getFolderName() + " failed to create! Unexpected things can happen!");
                    e.printStackTrace();
                }
            }
        }

        oilGenerationChance = config.get(Configuration.CATEGORY_GENERAL, "oil_generation_chance", 15D, "Chance per chunk in percentage to generate an Oil Lake. Set to 0 for no spawns").getDouble();

        Property property = config.get(Configuration.CATEGORY_GENERAL, "Compressed Iron Loss Percentage", 20);
        property.comment = "Loss percentage (on average) of Compressed Iron ingots/blocks when exposed to an explosion.";
        configCompressedIngotLossRate = property.getInt();

        property = config.get(Configuration.CATEGORY_GENERAL, "Enable Dungeon Loot generation", true);
        property.comment = "Enables the dungeon loot generation of the Nuke Virus and Stop! Worm. Useless items when playing SSP.";
        enableDungeonLoot = property.getBoolean();

        useHelmetModel = config.getBoolean("Use Pneumatic Helmet model", Configuration.CATEGORY_GENERAL, false, "When true, the Pneumatic Helmet will be a model. Warning: this model looks far too good to be in MC");
        programmerDifficulty = config.getInt("Programmer Difficulty", Configuration.CATEGORY_GENERAL, 0, 0, 2, "Defines the difficulty of the programmer shown to the user. 0 = easy, 1 = medium, 2 = advanced");

        property = config.get(Configuration.CATEGORY_GENERAL, "Villager Mechanic ID", 125);
        property.comment = "Villager ID used for the Mechanic Villager. Change when ID collides with an other mod which adds villagers.";
        villagerMechanicID = property.getInt();

        property = config.get(Configuration.CATEGORY_GENERAL, "Minigun damage", 4D);
        property.comment = "Damage of the Miniguns. This applies to the Sentry Gun, Handheld Minigun, and Drone-based minigun.";
        configMinigunDamage = (float)property.getDouble();

        property = config.get("Machine_Properties", "Pneumatic Generator (PneumaticCraft --> IC2) efficiency", 40);
        property.comment = "Changing this value will alter the pressurized air usage of the Pneumatic Generator. The output, EU, will stay the same.";
        pneumaticGeneratorEfficiency = property.getInt();

        property = config.get("machine_properties", "Electric Compressor (IC2 --> PneumaticCraft) efficiency", 40);
        property.comment = "Changing this value will alter the pressurized air production of the Electric Compressor. The input, EU, will stay the same.";
        electricCompressorEfficiency = property.getInt();

        property = config.get("machine_properties", "Pneumatic Engine (PneumaticCraft --> Buildcraft) efficiency", 40);
        property.comment = "Changing this value will alter the pressurized air usage of the Pneumatic Engine. The output, MJ, will stay the same.";
        pneumaticEngineEfficiency = property.getInt();

        property = config.get("machine_properties", "Kinetic Compressor (Buildcraft --> PneumaticCraft) efficiency", 40);
        property.comment = "Changing this value will alter the pressurized air production of the Kinetic Compressor. The input, MJ, will stay the same.";
        kineticCompressorEfficiency = property.getInt();

        property = config.get("machine_properties", "Pneumatic Dynamo (PneumaticCraft --> RF) efficiency", 40);
        property.comment = "Changing this value will alter the pressurized air usage of the Pneumatic Dynamo. The output, RF, will stay the same.";
        pneumaticDynamoEfficiency = property.getInt();

        property = config.get("machine_properties", "Flux Compressor (RF --> PneumaticCraft) efficiency", 40);
        property.comment = "Changing this value will alter the pressurized air production of the Flux Compressor. The input, RF, will stay the same.";
        fluxCompressorEfficiency = property.getInt();

        property = config.get("machine_properties", "Pneumatic Pump (PneumaticCraft --> Hydraulicraft) efficiency", 40);
        property.comment = "Changing this value will alter the hydraulic bar production of the Pneumatic Pump. The input, air, will stay the same.";
        pneumaticPumpEfficiency = property.getInt();

        elevatorBaseBlocksPerBase = config.getInt("Height per Elevator Base", "machine_properties", 4, 1, 256, "The max height of an elevator per stacked Elevator Base.");

        property = config.get("advanced", "Convert Multiparts to Blocks", false);
        property.comment = "ONLY SET TO TRUE WHEN YOU KNOW WHAT YOU'RE DOING. When set to true, this will convert any Pressure Tube in the world that was a FMP to its block variant. Handy when you're about to remove FMP from the instance. This will remove any other parts from the block like covers. Exception are tube modules.";
        convertMultipartsToBlocks = property.getBoolean(true);

        property = config.get("advanced", "Stop Drone AI", false);
        property.comment = "When set to true, Drones will not execute any program. This is useful to set to true when due to a bug Drones are lagging your server or crashing it. Please report the bug if you encounter it.";
        stopDroneAI = property.getBoolean(true);

        property = config.get("advanced", "Disable Kerosene Lamp Fake Air Block", false);
        property.comment = "When set to true, the Kerosene Lamp's fake air blocks won't be registered and therefore removed from the world. Useful if this causes trouble (it shouldn't though)";
        disableKeroseneLampFakeAirBlock = property.getBoolean(true);

        enableUpdateChecker = config.get(Configuration.CATEGORY_GENERAL, "Enable Update Checker", true).getBoolean(true);

        enableDroneSuffocationDamage = config.get(Configuration.CATEGORY_GENERAL, "Enable Drone Suffocation Damage", true).getBoolean(true);

        enableCoalToDiamondsRecipe = config.get("recipe_enabling", "8 Block of Coal --> 1 Diamond (Pressure Chamber)", true).getBoolean(true);
        enableElectricCompressorRecipe = config.get("recipe_enabling", "Electric Compressor", true).getBoolean(true);
        enablePneumaticGeneratorRecipe = config.get("recipe_enabling", "Pneumatic Generator", true).getBoolean(true);
        enablePneumaticPumpRecipe = config.get("recipe_enabling", "Pneumatic Pump", true).getBoolean(true);

        PneumaticCraft.proxy.initConfig(config);

        config.save();// save the configuration file
    }

    public static void postInit(){
        for(ISubConfig subConfig : subConfigs) {
            try {
                subConfig.postInit();
            } catch(IOException e) {
                Log.error("Config file " + subConfig.getFolderName() + " failed to create! Unexpected things can happen!");
                e.printStackTrace();
            }
        }
    }

    public static void setProgrammerDifficulty(int difficulty){
        config.get(Configuration.CATEGORY_GENERAL, "Programmer Difficulty", 0).set(difficulty);
        init(null);
    }

    public static int getProgrammerDifficulty(){
        return programmerDifficulty;
    }

}
