package pneumaticCraft.common.progwidgets;

import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.client.gui.GuiProgrammer;
import pneumaticCraft.client.gui.programmer.GuiProgWidgetCoordinate;
import pneumaticCraft.common.ai.DroneAIManager;
import pneumaticCraft.common.item.ItemPlastic;
import pneumaticCraft.lib.Textures;

public class ProgWidgetCoordinate extends ProgWidget implements IVariableWidget{

    private int x, y, z;
    private String variable = "";
    private boolean useVariable;
    private DroneAIManager aiManager;

    @Override
    public boolean hasStepInput(){
        return false;
    }

    @Override
    public Class<? extends IProgWidget> returnType(){
        return ProgWidgetCoordinate.class;
    }

    @Override
    public Class<? extends IProgWidget>[] getParameters(){
        return new Class[]{ProgWidgetCoordinate.class};
    }

    @Override
    public void addWarnings(List<String> curInfo, List<IProgWidget> widgets){
        super.addWarnings(curInfo, widgets);
        if(!useVariable && x == 0 && y == 0 && z == 0) {
            curInfo.add("gui.progWidget.coordinate.warning.noCoordinate");
        }
    }

    @Override
    public void addErrors(List<String> curInfo, List<IProgWidget> widgets){
        super.addErrors(curInfo, widgets);
        if(useVariable && variable.equals("")) {
            curInfo.add("gui.progWidget.general.error.emptyVariable");
        }
    }

    @Override
    public String getWidgetString(){
        return "coordinate";
    }

    @Override
    public int getCraftingColorIndex(){
        return ItemPlastic.CREEPER_PLANT_DAMAGE;
    }

    @Override
    public WidgetDifficulty getDifficulty(){
        return WidgetDifficulty.ADVANCED;
    }

    @Override
    public ResourceLocation getTexture(){
        return Textures.PROG_WIDGET_COORDINATE;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        tag.setInteger("posX", x);
        tag.setInteger("posY", y);
        tag.setInteger("posZ", z);
        tag.setString("variable", variable);
        tag.setBoolean("useVariable", useVariable);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        x = tag.getInteger("posX");
        y = tag.getInteger("posY");
        z = tag.getInteger("posZ");
        variable = tag.getString("variable");
        useVariable = tag.getBoolean("useVariable");
    }

    @Override
    public void setAIManager(DroneAIManager aiManager){
        this.aiManager = aiManager;
    }

    public BlockPos getCoordinate(){
        if(useVariable) {
            return aiManager.getCoordinate(variable);
        } else {
            return getRawCoordinate();
        }
    }

    public BlockPos getRawCoordinate(){
        return new BlockPos(x, y, z);
    }

    public void setCoordinate(BlockPos pos){
        if(pos != null) {
            x = pos.getX();
            y = pos.getY();
            z = pos.getZ();
        } else {
            x = y = z = 0;
        }
    }

    public void setVariable(String varName){
        variable = varName;
    }

    public String getVariable(){
        return variable;
    }

    public boolean isUsingVariable(){
        return useVariable;
    }

    public void setUsingVariable(boolean useVariable){
        this.useVariable = useVariable;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen getOptionWindow(GuiProgrammer guiProgrammer){
        return new GuiProgWidgetCoordinate(this, guiProgrammer);
    }

    @Override
    public void getTooltip(List<String> curTooltip){
        super.getTooltip(curTooltip);

        if(useVariable) curTooltip.add("XYZ: \"" + variable + "\"");
        else if(x != 0 || y != 0 || z != 0) curTooltip.add("X: " + x + ", Y: " + y + ", Z: " + z);
    }

    @Override
    public String getExtraStringInfo(){
        if(useVariable) return "\"" + variable + "\"";
        else return x != 0 || y != 0 || z != 0 ? x + ", " + y + ", " + z : null;
    }

    @Override
    public void addVariables(Set<String> variables){
        variables.add(variable);
    }
}
