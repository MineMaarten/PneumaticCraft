package pneumaticCraft.common.thirdparty.igwmod;

import igwmod.lib.Constants;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import pneumaticCraft.common.thirdparty.IThirdParty;

public class IGWMod implements IThirdParty{

    @Override
    public void preInit(){
        try {
            int minorVersion = Integer.parseInt((String)ReflectionHelper.getPrivateValue(Constants.class, null, "MINOR"));
            if(minorVersion < 7) MinecraftForge.EVENT_BUS.register(this);
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(){}

    @Override
    public void postInit(){}

    @Override
    public void clientSide(){
        FMLInterModComms.sendMessage("IGWMod", "pneumaticCraft.common.thirdparty.igwmod.IGWHandler", "init");
    }

    @Override
    public void clientInit(){}

    @SubscribeEvent
    public void notifyOutdatedIGW(TickEvent.PlayerTickEvent event){
        if(event.player.worldObj.isRemote && event.player == FMLClientHandler.instance().getClientPlayerEntity()) {
            event.player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You are running an outdated version of IGW-Mod which will not work properly with PneumaticCraft!:( Please update!"));
            FMLCommonHandler.instance().bus().unregister(this);
        }
    }
}
