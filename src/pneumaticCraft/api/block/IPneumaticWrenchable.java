package pneumaticCraft.api.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Should be implemented by any block or FMP that allows to be rotated by a Pneumatic Wrench. It uses almost the same
 * rotate method as the Vanilla (Forge) method. However it uses energy to rotate (when rotateBlock() return true).
 */
public interface IPneumaticWrenchable{

    public boolean rotateBlock(World world, EntityPlayer player, BlockPos pos, EnumFacing side);
}
