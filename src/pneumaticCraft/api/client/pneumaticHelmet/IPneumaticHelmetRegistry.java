package pneumaticCraft.api.client.pneumaticHelmet;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;

public interface IPneumaticHelmetRegistry{
    public void registerEntityTrackEntry(Class<? extends IEntityTrackEntry> entry);

    public void registerBlockTrackEntry(IBlockTrackEntry entry);

    public void addHackable(Class<? extends Entity> entityClazz, Class<? extends IHackableEntity> iHackable);

    public void addHackable(Block block, Class<? extends IHackableBlock> iHackable);

    /**
     * Returns a list of all current successful hacks of a given entity. This is used for example in Enderman hacking, so the user
     * can only hack an enderman once (more times wouldn't have any effect). This is mostly used for display purposes.
     * @param entity
     * @return empty list if no hacks.
     */
    public List<IHackableEntity> getCurrentEntityHacks(Entity entity);

    /**
     * Registers a Pneumatic Helmet module
     * @param renderHandler
     */
    public void registerRenderHandler(IUpgradeRenderHandler renderHandler);
}
