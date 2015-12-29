package pneumaticCraft.common.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.PneumaticCraft;
import pneumaticCraft.common.item.ItemMachineUpgrade;
import pneumaticCraft.common.item.ItemPneumaticArmor;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.tileentity.TileEntitySecurityStation;
import pneumaticCraft.lib.BBConstants;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;

public class BlockSecurityStation extends BlockPneumaticCraftModeled{

    public BlockSecurityStation(Material par2Material){
        super(par2Material);
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntitySecurityStation.class;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, BlockPos pos){
        setBlockBounds(BBConstants.SECURITY_STATION_MIN_POS, 0F, BBConstants.SECURITY_STATION_MIN_POS, BBConstants.SECURITY_STATION_MAX_POS, BBConstants.SECURITY_STATION_MAX_POS_TOP, BBConstants.SECURITY_STATION_MAX_POS);
    }

    @Override
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB axisalignedbb, List arraylist, Entity par7Entity){
        setBlockBounds(BBConstants.SECURITY_STATION_MIN_POS, BBConstants.SECURITY_STATION_MIN_POS, BBConstants.SECURITY_STATION_MIN_POS, BBConstants.SECURITY_STATION_MAX_POS, BBConstants.SECURITY_STATION_MAX_POS_TOP, BBConstants.SECURITY_STATION_MAX_POS);
        super.addCollisionBoxesToList(world, pos, state, axisalignedbb, arraylist, par7Entity);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Called when the block is placed in the world.
     */
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityLiving, ItemStack iStack){
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof TileEntitySecurityStation && entityLiving != null) {
            ((TileEntitySecurityStation)te).sharedUsers.add(((EntityPlayer)entityLiving).getGameProfile());
        }
        super.onBlockPlacedBy(world, pos, state, entityLiving, iStack);
    }

    @Override
    public boolean isRotatable(){
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float par7, float par8, float par9){
        if(player.isSneaking()) return false;
        else {
            if(!world.isRemote) {
                TileEntitySecurityStation te = (TileEntitySecurityStation)world.getTileEntity(pos);
                if(te != null) {
                    if(te.isPlayerOnWhiteList(player)) {
                        player.openGui(PneumaticCraft.instance, EnumGuiId.SECURITY_STATION_INVENTORY.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
                    } else if(!te.hasValidNetwork()) {
                        player.addChatComponentMessage(new ChatComponentTranslation(EnumChatFormatting.GREEN + "This Security Station is out of order: Its network hasn't been properly configured."));
                    } else if(te.hasPlayerHacked(player)) {
                        player.addChatComponentMessage(new ChatComponentTranslation(EnumChatFormatting.GREEN + "You've already hacked this Security Station!"));
                    } else if(getPlayerHackLevel(player) < te.getSecurityLevel()) {
                        player.addChatComponentMessage(new ChatComponentTranslation(EnumChatFormatting.RED + "You can't access or hack this Security Station. To hack it you need at least a Pneumatic Helmet upgraded with " + te.getSecurityLevel() + " Security upgrade(s)."));
                    } else {
                        player.openGui(PneumaticCraft.instance, EnumGuiId.HACKING.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
                    }
                }
            }
            return true;
        }
    }

    private int getPlayerHackLevel(EntityPlayer player){
        ItemStack armorStack = player.inventory.armorItemInSlot(3);
        if(armorStack != null && armorStack.getItem() == Itemss.pneumaticHelmet) {
            return ItemPneumaticArmor.getUpgrades(ItemMachineUpgrade.UPGRADE_SECURITY, armorStack);
        }
        return 0;//No hacking ability.
    }
}
