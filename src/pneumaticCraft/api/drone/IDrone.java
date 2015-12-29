package pneumaticCraft.api.drone;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.fluids.IFluidTank;
import pneumaticCraft.api.item.IPressurizable;

public interface IDrone extends IPressurizable{
    /**
     * 
     * @param upgradeIndex metadata value of the upgrade item
     * @return amount of inserted upgrades in the drone
     */
    public int getUpgrades(int upgradeIndex);

    public World getWorld();

    public IFluidTank getTank();

    public IInventory getInv();

    public Vec3 getDronePos();

    public IPathNavigator getPathNavigator();

    public void sendWireframeToClient(BlockPos pos);

    public EntityPlayerMP getFakePlayer();

    public boolean isBlockValidPathfindBlock(BlockPos pos);

    public void dropItem(ItemStack stack);

    public void setDugBlock(BlockPos pos);

    public EntityAITasks getTargetAI();

    public IExtendedEntityProperties getProperty(String key);

    public void setProperty(String key, IExtendedEntityProperties property);

    public void setEmittingRedstone(EnumFacing orientation, int emittingRedstone);

    public void setName(String string);

    public void setCarryingEntity(Entity entity);

    public Entity getCarryingEntity();

    public boolean isAIOverriden();

    public void onItemPickupEvent(EntityItem curPickingUpEntity, int stackSize);
}
