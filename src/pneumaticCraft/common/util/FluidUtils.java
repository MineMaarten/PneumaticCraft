package pneumaticCraft.common.util;

import java.util.List;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

public class FluidUtils{
    public static boolean tryInsertingLiquid(TileEntity te, ItemStack liquidContainer, boolean creative, List<ItemStack> returnedItems){
        if(te instanceof IFluidHandler) {
            IFluidHandler fluidHandler = (IFluidHandler)te;

            if(liquidContainer != null) {
                FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(liquidContainer);
                if(fluid != null) {
                    fluid.amount = 1000;
                    if(fluidHandler.canFill(null, fluid.getFluid()) && fluidHandler.fill(null, fluid, false) == 1000) {
                        fluidHandler.fill(null, fluid, true);
                        if(!creative) {
                            liquidContainer.stackSize--;

                            ItemStack returnedItem = null;
                            FluidContainerData[] allFluidData = FluidContainerRegistry.getRegisteredFluidContainerData();
                            for(FluidContainerData fluidData : allFluidData) {
                                if(fluidData.filledContainer.isItemEqual(liquidContainer)) {
                                    returnedItem = fluidData.emptyContainer;
                                    break;
                                }
                            }
                            if(returnedItem != null) {
                                returnedItems.add(returnedItem.copy());
                            }
                        }
                        return true;
                    }
                } else if(liquidContainer.getItem() instanceof IFluidContainerItem) {
                    IFluidContainerItem container = (IFluidContainerItem)liquidContainer.getItem();

                    fluid = container.getFluid(liquidContainer);
                    if(fluid != null) {
                        fluid = fluid.copy();
                        if(fluidHandler.canFill(null, fluid.getFluid()) && fluidHandler.fill(null, fluid, false) == fluid.amount) {
                            ItemStack returnedItem = liquidContainer.copy();
                            returnedItem.stackSize = 1;
                            container.drain(returnedItem, fluid.amount, true);
                            fluidHandler.fill(null, fluid, true);

                            if(!creative) {
                                liquidContainer.stackSize--;
                                returnedItems.add(returnedItem.copy());
                            }
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    public static boolean tryExtractingLiquid(TileEntity te, ItemStack liquidContainer, List<ItemStack> returnedItems){
        if(te instanceof IFluidHandler) {
            IFluidHandler fluidHandler = (IFluidHandler)te;

            if(liquidContainer != null) {
                int containerCapacity = FluidContainerRegistry.getContainerCapacity(liquidContainer);
                if(containerCapacity > 0 || liquidContainer.getItem() == Items.bucket) {
                    if(containerCapacity == 0) containerCapacity = 1000;
                    FluidStack extractedLiquid = fluidHandler.drain(null, containerCapacity, false);
                    if(extractedLiquid != null && extractedLiquid.amount == containerCapacity) {
                        ItemStack filledContainer = FluidContainerRegistry.fillFluidContainer(extractedLiquid, liquidContainer);
                        if(filledContainer != null) {
                            fluidHandler.drain(null, containerCapacity, true);
                            liquidContainer.stackSize--;
                            returnedItems.add(filledContainer.copy());
                            return true;
                        }
                    }

                } else if(liquidContainer.getItem() instanceof IFluidContainerItem) {
                    IFluidContainerItem container = (IFluidContainerItem)liquidContainer.getItem();

                    ItemStack singleItem = liquidContainer.copy();
                    singleItem.stackSize = 1;
                    FluidStack extractedLiquid = fluidHandler.drain(null, container.getCapacity(singleItem), false);
                    if(extractedLiquid != null) {
                        int filledAmount = container.fill(singleItem, extractedLiquid, true);
                        if(filledAmount > 0) {
                            liquidContainer.stackSize--;
                            returnedItems.add(singleItem);

                            FluidStack fluid = extractedLiquid.copy();
                            fluid.amount = filledAmount;
                            fluidHandler.drain(null, fluid, true);
                            return true;
                        }

                    }
                }
            }
        }
        return false;
    }

    public static boolean isSourceBlock(World world, BlockPos pos){
        IBlockState state = world.getBlockState(pos);
        if(state.getProperties().containsKey(BlockLiquid.LEVEL)) {
            return world.getBlockState(pos).getValue(BlockLiquid.LEVEL).intValue() == 0;
        } else if(state.getProperties().containsKey(BlockFluidBase.LEVEL)) {
            return world.getBlockState(pos).getValue(BlockFluidBase.LEVEL).intValue() == 0;
        } else {
            return false;
        }
    }
}
