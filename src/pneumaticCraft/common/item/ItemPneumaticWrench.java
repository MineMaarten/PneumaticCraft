package pneumaticCraft.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import pneumaticCraft.api.block.IPneumaticWrenchable;
import pneumaticCraft.common.network.NetworkHandler;
import pneumaticCraft.common.network.PacketPlaySound;
import pneumaticCraft.common.thirdparty.ModInteractionUtils;
import pneumaticCraft.lib.PneumaticValues;
import pneumaticCraft.lib.Sounds;

//TODO Buildcraft dep @Optional.Interface(iface = "buildcraft.api.tools.IToolWrench", modid = ModIds.BUILDCRAFT)
public class ItemPneumaticWrench extends ItemPressurizable /*implements IToolWrench*/{

    public ItemPneumaticWrench(String textureLocation, int maxAir, int volume){
        super(textureLocation, maxAir, volume);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitVecX, float hitVecY, float hitVecZ){
        if(!world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            IPneumaticWrenchable wrenchable = null;
            if(block instanceof IPneumaticWrenchable) {
                wrenchable = (IPneumaticWrenchable)block;
            } else {
                wrenchable = ModInteractionUtils.getInstance().getWrenchable(world.getTileEntity(pos));
            }
            if(wrenchable != null && ((ItemPneumaticWrench)Itemss.pneumaticWrench).getPressure(stack) > 0) {
                if(wrenchable.rotateBlock(world, player, pos, side)) {
                    if(!player.capabilities.isCreativeMode) ((ItemPneumaticWrench)Itemss.pneumaticWrench).addAir(stack, -PneumaticValues.USAGE_PNEUMATIC_WRENCH);
                    NetworkHandler.sendToAllAround(new PacketPlaySound(Sounds.PNEUMATIC_WRENCH, pos, 1.0F, 1.0F, false), world);
                    return true;
                }
            } else if(block != null) {
                //rotating normal blocks doesn't cost energy.
                if(block.rotateBlock(world, pos, side)) {
                    NetworkHandler.sendToAllAround(new PacketPlaySound(Sounds.PNEUMATIC_WRENCH, pos, 1.0F, 1.0F, false), world);
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack iStack, EntityPlayer player, EntityLivingBase entity){
        if(!player.worldObj.isRemote) {
            if(entity.isEntityAlive() && entity instanceof IPneumaticWrenchable && ((ItemPneumaticWrench)Itemss.pneumaticWrench).getPressure(iStack) > 0) {
                if(((IPneumaticWrenchable)entity).rotateBlock(entity.worldObj, player, null, null)) {
                    if(!player.capabilities.isCreativeMode) ((ItemPneumaticWrench)Itemss.pneumaticWrench).addAir(iStack, -PneumaticValues.USAGE_PNEUMATIC_WRENCH);
                    NetworkHandler.sendToAllAround(new PacketPlaySound(Sounds.PNEUMATIC_WRENCH, entity.posX, entity.posY, entity.posZ, 1.0F, 1.0F, false), entity.worldObj);
                    return true;
                }
            }
        }
        return false;
    }

    /* @Override
     public boolean canWrench(EntityPlayer player, int x, int y, int z){
         return true;
     }

     @Override
     public void wrenchUsed(EntityPlayer player, int x, int y, int z){}*/
}
