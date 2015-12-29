package pneumaticCraft.common.block;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidPneumaticCraft extends BlockFluidClassic{

    public BlockFluidPneumaticCraft(Fluid fluid, Material material){
        super(fluid, material);
        setUnlocalizedName(fluid.getName());
    }

    public BlockFluidPneumaticCraft(Fluid fluid){
        this(fluid, Material.water);
    }
}
