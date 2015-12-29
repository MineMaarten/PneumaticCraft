package pneumaticCraft.common.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.common.item.ItemMachineUpgrade;
import pneumaticCraft.common.tileentity.TileEntityChargingStation;
import pneumaticCraft.lib.BBConstants;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;

public class BlockChargingStation extends BlockPneumaticCraftModeled{

    public BlockChargingStation(Material par2Material){
        super(par2Material);

    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos){
        if(((TileEntityChargingStation)world.getTileEntity(pos)).getUpgrades(ItemMachineUpgrade.UPGRADE_DISPENSER_DAMAGE) > 0) {
            setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            setBlockBounds(BBConstants.CHARGING_STATION_MIN_POS, 0F, BBConstants.CHARGING_STATION_MIN_POS, BBConstants.CHARGING_STATION_MAX_POS, BBConstants.CHARGING_STATION_MAX_POS_TOP, BBConstants.CHARGING_STATION_MAX_POS);
        }
    }

    @Override
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB axisalignedbb, List arraylist, Entity par7Entity){
        setBlockBoundsBasedOnState(world, pos);
        super.addCollisionBoxesToList(world, pos, state, axisalignedbb, arraylist, par7Entity);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityChargingStation.class;
    }

    @Override
    public EnumGuiId getGuiID(){
        return EnumGuiId.CHARGING_STATION;
    }

    @Override
    public boolean isRotatable(){
        return true;
    }

    @Override
    protected int getInventoryDropEndSlot(IInventory inventory){
        return 5;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float par7, float par8, float par9){
        if(!world.isRemote && player.isSneaking()) {
            TileEntityChargingStation station = (TileEntityChargingStation)world.getTileEntity(pos);
            station.setCamoStack(player.getCurrentEquippedItem());
            return player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemBlock;
        } else return super.onBlockActivated(world, pos, state, player, side, par7, par8, par9);
    }

}
