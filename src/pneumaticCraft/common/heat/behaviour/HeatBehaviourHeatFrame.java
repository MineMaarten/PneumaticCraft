package pneumaticCraft.common.heat.behaviour;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import pneumaticCraft.api.heat.HeatBehaviour;
import pneumaticCraft.api.heat.IHeatExchangerLogic;
import pneumaticCraft.common.heat.HeatExchangerLogic;
import pneumaticCraft.common.semiblock.ISemiBlock;
import pneumaticCraft.common.semiblock.SemiBlockHeatFrame;
import pneumaticCraft.common.semiblock.SemiBlockManager;
import pneumaticCraft.lib.Names;

public class HeatBehaviourHeatFrame extends HeatBehaviour{
    private ISemiBlock semiBlock;

    @Override
    public void initialize(IHeatExchangerLogic connectedHeatLogic, World world, BlockPos pos){
        super.initialize(connectedHeatLogic, world, pos);
        semiBlock = null;
    }

    @Override
    public String getId(){
        return Names.MOD_ID + ":heatFrame";
    }

    private ISemiBlock getSemiBlock(){
        if(semiBlock == null) {
            semiBlock = SemiBlockManager.getInstance(getWorld()).getSemiBlock(getWorld(), getPos());
        }
        return semiBlock;
    }

    @Override
    public boolean isApplicable(){
        return getSemiBlock() instanceof SemiBlockHeatFrame;
    }

    @Override
    public void update(){
        SemiBlockHeatFrame frame = (SemiBlockHeatFrame)getSemiBlock();
        HeatExchangerLogic.exchange(frame.getHeatExchangerLogic(null), getHeatExchanger());
    }

}
