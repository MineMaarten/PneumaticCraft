package pneumaticCraft.common.progwidgets;

import java.util.Set;

import net.minecraft.util.BlockPos;

public interface IAreaProvider{
    public void getArea(Set<BlockPos> area);
}
