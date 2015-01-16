package pneumaticCraft.common.thirdparty;

import pneumaticCraft.api.PneumaticRegistry;
import pneumaticCraft.lib.Log;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class Chisel implements IThirdParty {

	@Override
	public void preInit() {

	}

	@Override
	public void init() {

	}

	@Override
	public void postInit() {

		Class modClass = null;
		//Original chisel
		try {
			Log.info("Attempting Chisel v1 integration");
			modClass = Class.forName("info.jbcs.minecraft.chisel.Chisel");
		} catch (ClassNotFoundException e) {
			Log.info("Chisel v1 class info.jbcs.minecraft.chisel.Chisel not found, not integrating");
		}

		//Chisel v2
		try {
			Log.info("Attempting Chisel v2 integration");
			modClass = Class.forName("com.cricketcraft.chisel.Chisel");
		} catch (ClassNotFoundException e) {
			Log.info("Chisel v2 class com.cricketcraft.chisel.Chisel not found, not integrating");
		}

		if (modClass != null) {
			try {
				PneumaticRegistry.getInstance().registerConcealableRenderId(ReflectionHelper.findField(modClass, "RenderCTMId", "renderCTMId").getInt(null));
				PneumaticRegistry.getInstance().registerConcealableRenderId(ReflectionHelper.findField(modClass, "RenderEldritchId", "renderEldritchId").getInt(null));
				PneumaticRegistry.getInstance().registerConcealableRenderId(ReflectionHelper.findField(modClass, "RenderCarpetId", "renderCarpetId").getInt(null));
				Log.info("Chisel integrated");
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void clientSide() {

	}

	@Override
	public void clientInit() {
	}

}
