package pneumaticCraft.common.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.common.DamageSourcePneumaticCraft;
import pneumaticCraft.common.fluid.Fluids;

public class BlockFluidEtchingAcid extends BlockFluidPneumaticCraft{

    public BlockFluidEtchingAcid(){
        super(Fluids.etchingAcid, new MaterialLiquid(MapColor.waterColor){
            @Override
            public int getMaterialMobility(){
                return 1;
            }
        });
        setUnlocalizedName("etchingAcid");
    }

    @Override
    //TODO 1.8 test verify renderpass
    public int colorMultiplier(IBlockAccess iblockaccess, BlockPos pos, int renderPass){
        return 0x501c00;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity){
        if(entity instanceof EntityLivingBase && entity.ticksExisted % 10 == 0) {
            ((EntityLivingBase)entity).attackEntityFrom(DamageSourcePneumaticCraft.etchingAcid, 1);
        }
    }

}
