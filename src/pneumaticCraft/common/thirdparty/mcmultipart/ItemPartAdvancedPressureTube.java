package pneumaticCraft.common.thirdparty.mcmultipart;

import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import pneumaticCraft.PneumaticCraft;

public class ItemPartAdvancedPressureTube extends ItemMultiPart{
    public ItemPartAdvancedPressureTube(){
        setUnlocalizedName("advancedPressureTube");
        setCreativeTab(PneumaticCraft.tabPneumaticCraft);
    }

    @Override
    public IMultipart createPart(World world, BlockPos blockPos, EnumFacing enumFacing, Vec3 vec3, ItemStack itemStack, EntityPlayer player){
        return new PartAdvancedPressureTube();
    }

}
