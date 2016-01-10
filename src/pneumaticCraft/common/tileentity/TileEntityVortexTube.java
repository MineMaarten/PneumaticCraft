package pneumaticCraft.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import pneumaticCraft.api.PneumaticRegistry;
import pneumaticCraft.api.heat.IHeatExchangerLogic;
import pneumaticCraft.api.tileentity.IHeatExchanger;
import pneumaticCraft.common.network.DescSynced;

public class TileEntityVortexTube extends TileEntityPneumaticBase implements IHeatExchanger{
    private final IHeatExchangerLogic coldHeatExchanger = PneumaticRegistry.getInstance().getHeatRegistry().getHeatExchangerLogic();
    private final IHeatExchangerLogic hotHeatExchanger = PneumaticRegistry.getInstance().getHeatRegistry().getHeatExchangerLogic();
    private final IHeatExchangerLogic connectingExchanger = PneumaticRegistry.getInstance().getHeatRegistry().getHeatExchangerLogic();
    private int visualizationTimer = 60;

    @DescSynced
    private boolean visualize;
    @DescSynced
    private int roll;
    @DescSynced
    private int coldHeatLevel = 10, hotHeatLevel = 10;

    public TileEntityVortexTube(){
        super(20, 25, 2000);
        coldHeatExchanger.setThermalResistance(0.01);
        hotHeatExchanger.setThermalResistance(0.01);
        connectingExchanger.setThermalResistance(100);
        connectingExchanger.addConnectedExchanger(coldHeatExchanger);
        connectingExchanger.addConnectedExchanger(hotHeatExchanger);
    }

    @Override
    public IHeatExchangerLogic getHeatExchangerLogic(EnumFacing side){
        if(side == null || side == getRotation().getOpposite()) {
            return hotHeatExchanger;
        } else if(side == getRotation()) {
            return coldHeatExchanger;
        } else {
            return null;
        }
    }

    @Override
    protected EnumFacing[] getConnectedHeatExchangerSides(){
        return new EnumFacing[]{getRotation().getOpposite()};
    }

    @Override
    protected void initializeIfHeatExchanger(){
        super.initializeIfHeatExchanger();
        initializeHeatExchanger(coldHeatExchanger, getRotation());
    }

    @Override
    public boolean isConnectedTo(EnumFacing side){
        return getTubeDirection() == side;
    }

    public EnumFacing getTubeDirection(){
        EnumFacing d;

        switch(getRotation()){
            case DOWN:
            case NORTH:
            case UP:
                d = EnumFacing.WEST;
                break;
            case SOUTH:
                d = EnumFacing.EAST;
                break;
            case WEST:
                d = EnumFacing.SOUTH;
                break;
            case EAST:
                d = EnumFacing.NORTH;
                break;
            default:
                d = EnumFacing.SOUTH;
        }
        for(int i = 0; i < roll; i++) {
            d = d.rotateAround(getRotation().getAxis()); //TODO 1.8 test
        }
        return d;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        NBTTagCompound coldHeatTag = new NBTTagCompound();
        coldHeatExchanger.writeToNBT(coldHeatTag);
        tag.setTag("coldHeat", coldHeatTag);
        tag.setByte("roll", (byte)roll);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        coldHeatExchanger.readFromNBT(tag.getCompoundTag("coldHeat"));
        roll = tag.getByte("roll");
    }

    public int getColdHeatLevel(){
        return coldHeatLevel;
    }

    public int getHotHeatLevel(){
        return hotHeatLevel;
    }

    public boolean shouldVisualize(){
        return visualize;
    }

    public int getRoll(){
        return roll;
    }

    public void rotateRoll(int rotation){
        roll += rotation;
        if(roll > 3) roll = 0;
        if(roll < 0) roll = 3;
        updateNeighbours();
    }

    @Override
    public void update(){
        super.update();
        if(!worldObj.isRemote) {
            connectingExchanger.update();
            coldHeatExchanger.update();//Only update the cold and connecting side, the hot side is handled in TileEntityBase.
            int usedAir = (int)(getPressure() * 10);
            if(usedAir > 0) {
                addAir(-usedAir);
                double generatedHeat = usedAir / 10D;
                coldHeatExchanger.addHeat(-generatedHeat);
                hotHeatExchanger.addHeat(generatedHeat);
            }
            visualize = visualizationTimer > 0;
            if(visualize) visualizationTimer--;
            coldHeatLevel = TileEntityCompressedIronBlock.getHeatLevelForTemperature(coldHeatExchanger.getTemperature());
            hotHeatLevel = TileEntityCompressedIronBlock.getHeatLevelForTemperature(hotHeatExchanger.getTemperature());
        }
    }

    @Override
    public void onBlockRotated(){
        visualizationTimer = 60;
    }

}
