package pneumaticCraft.common.heat.behaviour;

import net.minecraft.init.Blocks;
import pneumaticCraft.lib.Names;

public class HeatBehaviourFireTransition extends HeatBehaviourTransition{

    @Override
    public boolean isApplicable(){
        return super.isApplicable() && getBlockState().getBlock() == Blocks.fire;
    }

    @Override
    protected int getMaxExchangedHeat(){
        return 1000;
    }

    @Override
    protected boolean transitionOnTooMuchExtraction(){
        return true;
    }

    @Override
    protected void transformBlock(){
        getWorld().setBlockToAir(getPos());
    }

    @Override
    public String getId(){
        return Names.MOD_ID + ":fire";
    }

}
