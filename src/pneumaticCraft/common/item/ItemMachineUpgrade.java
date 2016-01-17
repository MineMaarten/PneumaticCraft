package pneumaticCraft.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.api.PneumaticRegistry;

public class ItemMachineUpgrade extends ItemPneumatic{

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> infoList, boolean par4){
        infoList.add("Used in:");

        PneumaticRegistry.getInstance().getItemRegistry().addTooltip(this, infoList);

        /*List<String> unlocalized = new ArrayList<String>();
        SensorHandler.getInstance().addMachineUpgradeInfo(unlocalized, stack.getItemDamage());
        switch(stack.getItemDamage()){
            case 0:
                infoList.add("Any machine driven by");
                infoList.add("pneumatic power");
                break;
            case 1:
                unlocalized.add(Blockss.airCannon.getUnlocalizedName());
                unlocalized.add(Blockss.chargingStation.getUnlocalizedName());
                unlocalized.add(Blockss.aerialInterface.getUnlocalizedName());
                unlocalized.add(Itemss.drone.getUnlocalizedName());
                break;
            case 2:
                unlocalized.add(Blockss.airCannon.getUnlocalizedName());
                infoList.add("-" + I18n.format("gui.pressureChamber"));
                unlocalized.add(Itemss.drone.getUnlocalizedName());
                break;
            case 3:
                unlocalized.add(Blockss.securityStation.getUnlocalizedName());
            case 4:
            case 6:
            case 7:
                unlocalized.add(Itemss.pneumaticHelmet.getUnlocalizedName());
                break;
            case 5:
                infoList.add("Most machines");
                /* infoList.add("-" + Names.AIR_CANNON);
                 infoList.add("-" + Names.AIR_COMPRESSOR);
                 infoList.add("-" + Names.CHARGING_STATION);
                 infoList.add("-" + Names.ELEVATOR);
                 infoList.add("-" + Names.PNEUMATIC_HELMET);
                 infoList.add("-" + Names.PRESSURE_CHAMBER_INTERFACE);
                 infoList.add("-" + Names.VACUUM_PUMP);
                 infoList.add("-" + Names.ASSEMBLY_CONTROLLER);
                 infoList.add("-" + Names.UV_LIGHT_BOX);*/
        /*       break;
           case 8:
               unlocalized.add(Blockss.securityStation.getUnlocalizedName());
               unlocalized.add(Blockss.airCannon.getUnlocalizedName());
               unlocalized.add(Itemss.pneumaticHelmet.getUnlocalizedName());
               unlocalized.add(Blockss.universalSensor.getUnlocalizedName());
               break;
           case 9:
               infoList.add("All pneumatic machines");
               /*infoList.add("-" + Names.SECURITY_STATION);
               infoList.add("-" + Names.PNEUMATIC_HELMET);
               infoList.add("-" + Names.AIR_CANNON);
               infoList.add("-" + Names.AIR_COMPRESSOR);
               infoList.add("-" + Names.CHARGING_STATION);
               infoList.add("-" + Names.ELEVATOR);
               infoList.add("-" + Names.PRESSURE_CHAMBER);
               infoList.add("-" + Names.VACUUM_PUMP);
               infoList.add("-" + Names.ASSEMBLY_CONTROLLER);
               infoList.add("-" + Names.UV_LIGHT_BOX);*/
        /*    break;
        case 10:
            unlocalized.add(Itemss.pneumaticHelmet.getUnlocalizedName());
            break;
        }

        for(String unloc : unlocalized) {
        infoList.add("-" + I18n.format(unloc + ".name"));
        }*/
        super.addInformation(stack, player, infoList, par4);
    }

}
