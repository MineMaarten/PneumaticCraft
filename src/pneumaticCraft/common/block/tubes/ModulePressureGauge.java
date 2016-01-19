package pneumaticCraft.common.block.tubes;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import pneumaticCraft.common.network.NetworkHandler;
import pneumaticCraft.common.network.PacketUpdatePressureBlock;
import pneumaticCraft.common.tileentity.TileEntityPneumaticBase;
import pneumaticCraft.lib.Names;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;

public class ModulePressureGauge extends TubeModuleRedstoneEmitting{
    public ModulePressureGauge(){
        lowerBound = 0;
        higherBound = 7.5F;
    }

    @Override
    public void update(){
        super.update();
        if(!pressureTube.world().isRemote) {
            if(pressureTube.world().getTotalWorldTime() % 20 == 0) NetworkHandler.sendToAllAround(new PacketUpdatePressureBlock((TileEntityPneumaticBase)getTube()), getTube().world());
            setRedstone(getRedstone(pressureTube.getAirHandler(null).getPressure()));
        }
    }

    private int getRedstone(float pressure){
        return (int)((pressure - lowerBound) / (higherBound - lowerBound) * 15);
    }

    @Override
    public String getType(){
        return Names.MODULE_GAUGE;
    }

    @Override
    public String getModelName(){
        return "gaugeModule";
    }

    @Override
    public double getWidth(){
        return 0.5;
    }

    @Override
    protected double getHeight(){
        return 0.25;
    }

    @Override
    public void addItemDescription(List<String> curInfo){
        curInfo.add(EnumChatFormatting.BLUE + "Formula: Redstone = 2.0 x pressure(bar)");
        curInfo.add("This module emits a redstone signal of which");
        curInfo.add("the strength is dependant on how much pressure");
        curInfo.add("the tube is at.");
    }

    @Override
    public boolean onActivated(EntityPlayer player){
        return super.onActivated(player);
    }

    @Override
    protected EnumGuiId getGuiId(){
        return EnumGuiId.PRESSURE_MODULE;
    }
}
