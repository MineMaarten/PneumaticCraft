package pneumaticCraft.common.fluid;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import pneumaticCraft.common.block.BlockFluidPneumaticCraft;

public class FluidPneumaticCraft extends Fluid{

    public FluidPneumaticCraft(String fluidName){
        this(fluidName, true);
    }

    public FluidPneumaticCraft(String fluidName, boolean registerBlock){
        super(fluidName, new ResourceLocation("pneumaticcraft:blocks/" + fluidName + "_still"), new ResourceLocation("pneumaticcraft:blocks/" + fluidName + "_flow"));
        FluidRegistry.registerFluid(this);
        if(registerBlock) setBlock(new BlockFluidPneumaticCraft(this));

    }
}
