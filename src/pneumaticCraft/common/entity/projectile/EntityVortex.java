package pneumaticCraft.common.entity.projectile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import pneumaticCraft.common.util.PneumaticCraftUtils;

public class EntityVortex extends EntityThrowable{

    private int hitCounter = 0;
    private double oldMotionX;
    private double oldMotionY;
    private double oldMotionZ;

    public EntityVortex(World par1World){
        super(par1World);
    }

    public EntityVortex(World par1World, EntityLivingBase par2EntityLiving){
        super(par1World, par2EntityLiving);
    }

    public EntityVortex(World par1World, double par2, double par4, double par6){
        super(par1World, par2, par4, par6);
    }

    @Override
    protected void entityInit(){}

    @Override
    public void onUpdate(){
        oldMotionX = motionX;
        oldMotionY = motionY;
        oldMotionZ = motionZ;
        super.onUpdate();
        //blowOtherEntities();
        motionX *= 0.95D;// equal to the potion effect friction. 0.95F
        motionY *= 0.95D;
        motionZ *= 0.95D;
        if(motionX * motionX + motionY * motionY + motionZ * motionZ < 0.1D) {
            setDead();
        }
        if(!worldObj.isRemote) {
            BlockPos pos = new BlockPos(posX, posY, posZ);
            tryCutPlants(pos);
            for(EnumFacing dir : EnumFacing.VALUES) {
                tryCutPlants(pos.offset(dir));
            }
        }

    }

    private void tryCutPlants(BlockPos pos){
        Block block = worldObj.getBlockState(pos).getBlock();
        if(block instanceof IPlantable || block instanceof BlockLeaves) {
            worldObj.destroyBlock(pos, true);
        }
    }

    /*   private void blowOtherEntities(){
           List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
           for(Entity e : list) {
               if(e != getThrower() || ticksExisted >= 5) {
                   if(e instanceof EntityPlayer || e instanceof EntityItem) {
                       e.motionX += motionX;
                       e.motionY += motionY;
                       e.motionZ += motionZ;
                   }
               }
           }
       }*/

    @Override
    public float getGravityVelocity(){
        return 0;
    }

    @Override
    protected void onImpact(MovingObjectPosition objectPosition){
        if(objectPosition.entityHit != null) {
            Entity entity = objectPosition.entityHit;
            entity.motionX += motionX;
            entity.motionY += motionY;
            entity.motionZ += motionZ;
            if(!entity.worldObj.isRemote && entity instanceof IShearable) {
                IShearable shearable = (IShearable)entity;
                BlockPos pos = new BlockPos(posX, posY, posZ);
                if(shearable.isShearable(null, worldObj, pos)) {
                    List<ItemStack> drops = shearable.onSheared(null, worldObj, pos, 0);
                    for(ItemStack stack : drops) {
                        PneumaticCraftUtils.dropItemOnGround(stack, worldObj, entity.posX, entity.posY, entity.posZ);
                    }
                }
            }

        } else {
            Block block = worldObj.getBlockState(objectPosition.getBlockPos()).getBlock();
            if(block instanceof IPlantable || block instanceof BlockLeaves) {
                motionX = oldMotionX;
                motionY = oldMotionY;
                motionZ = oldMotionZ;
            } else {
                setDead();
            }
        }
        hitCounter++;
        if(hitCounter > 20) setDead();
    }
}
