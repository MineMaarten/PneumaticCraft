package pneumaticCraft.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import pneumaticCraft.common.tileentity.TileEntityPressureChamberInterface;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;

public class BlockPressureChamberInterface extends BlockPneumaticCraftModeled implements IBlockPressureChamber{

    public BlockPressureChamberInterface(Material par2Material){
        super(par2Material);
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityPressureChamberInterface.class;
    }

    @Override
    public EnumGuiId getGuiID(){
        return EnumGuiId.PRESSURE_CHAMBER_INTERFACE;
    }

    @Override
    public boolean isRotatable(){
        return true;
    }

    @Override
    protected boolean canRotateToTopOrBottom(){
        return true;
    }

    @Override
    protected int getInventoryDropEndSlot(IInventory inventory){
        return 5;
    }

}
