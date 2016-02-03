package pneumaticCraft.common.ai;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import pneumaticCraft.common.progwidgets.ISignEditWidget;
import pneumaticCraft.common.progwidgets.ProgWidgetAreaItemBase;
import pneumaticCraft.common.tileentity.TileEntityAphorismTile;

public class DroneAIEditSign extends DroneAIBlockInteraction<ProgWidgetAreaItemBase>{

    public DroneAIEditSign(IDroneBase drone, ProgWidgetAreaItemBase widget){
        super(drone, widget);
    }

    @Override
    protected boolean isValidPosition(BlockPos pos){
        TileEntity te = drone.world().getTileEntity(pos);
        if(te instanceof TileEntitySign) {
            TileEntitySign sign = (TileEntitySign)te;
            String[] lines = ((ISignEditWidget)widget).getLines();
            for(int i = 0; i < 4; i++) {
                sign.signText[i] = new ChatComponentText(i < lines.length ? lines[i] : ""); //TODO 1.8 test
            }
            drone.world().markBlockForUpdate(pos);
        } else if(te instanceof TileEntityAphorismTile) {
            TileEntityAphorismTile sign = (TileEntityAphorismTile)te;
            sign.setTextLines(((ISignEditWidget)widget).getLines());
        }
        return false;
    }

    @Override
    protected boolean doBlockInteraction(BlockPos pos, double distToBlock){
        return false;
    }
}
