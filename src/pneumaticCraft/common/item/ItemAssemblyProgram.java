package pneumaticCraft.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.recipes.programs.AssemblyProgram;
import pneumaticCraft.common.recipes.programs.ProgramDrill;
import pneumaticCraft.common.recipes.programs.ProgramDrillLaser;
import pneumaticCraft.common.recipes.programs.ProgramLaser;

public class ItemAssemblyProgram extends ItemPneumatic{
    public static final int PROGRAMS_AMOUNT = 3;

    public static final int DRILL_DAMAGE = 0;
    public static final int LASER_DAMAGE = 1;
    public static final int DRILL_LASER_DAMAGE = 2;

    private AssemblyProgram[] referencePrograms;

    public ItemAssemblyProgram(){
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack is){
        return super.getUnlocalizedName(is) + is.getItemDamage();
    }

    @Override
    public int getMetadata(int meta){
        return meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs tab, List subItems){
        for(int i = 0; i < PROGRAMS_AMOUNT; i++) {
            subItems.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List infoList, boolean par4){
        infoList.add("Required Machines:");
        infoList.add("-" + Blockss.assemblyController.getLocalizedName());

        if(referencePrograms == null) {
            referencePrograms = new AssemblyProgram[PROGRAMS_AMOUNT];
            for(int i = 0; i < PROGRAMS_AMOUNT; i++) {
                referencePrograms[i] = getProgramFromItem(i);
            }
        }
        AssemblyProgram program = referencePrograms[Math.min(stack.getItemDamage(), PROGRAMS_AMOUNT - 1)];
        AssemblyProgram.EnumMachine[] requiredMachines = program.getRequiredMachines();
        for(AssemblyProgram.EnumMachine machine : requiredMachines) {
            switch(machine){
                case PLATFORM:
                    infoList.add("-" + Blockss.assemblyPlatform.getLocalizedName());
                    break;
                case DRILL:
                    infoList.add("-" + Blockss.assemblyDrill.getLocalizedName());
                    break;
                case LASER:
                    infoList.add("-" + Blockss.assemblyLaser.getLocalizedName());
                    break;
                case IO_UNIT_EXPORT:
                    infoList.add("-" + Blockss.assemblyIOUnit.getLocalizedName() + " (export)");//TODO localize
                    break;
                case IO_UNIT_IMPORT:
                    infoList.add("-" + Blockss.assemblyIOUnit.getLocalizedName() + " (import)");
                    break;
            }
        }
    }

    public static AssemblyProgram getProgramFromItem(int meta){
        switch(meta){
            case DRILL_DAMAGE:
                return new ProgramDrill();
            case LASER_DAMAGE:
                return new ProgramLaser();
            case DRILL_LASER_DAMAGE:
                return new ProgramDrillLaser();
        }
        return null;
    }

}
