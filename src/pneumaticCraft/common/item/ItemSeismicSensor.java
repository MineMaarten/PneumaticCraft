package pneumaticCraft.common.item;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import pneumaticCraft.common.fluid.Fluids;
import pneumaticCraft.common.util.FluidUtils;

public class ItemSeismicSensor extends ItemPneumatic{
    public ItemSeismicSensor(){
        super("seismicSensor");
        setMaxStackSize(1);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float par8, float par9, float par10){
        if(!world.isRemote) {
            int startY = pos.getY();
            while(pos.getY() > 0) {
                pos = pos.offset(EnumFacing.DOWN);
                if(world.getBlockState(pos).getBlock() == FluidRegistry.getFluid(Fluids.oil.getName()).getBlock()) {
                    Set<BlockPos> oilPositions = new HashSet<BlockPos>();
                    Stack<BlockPos> pendingPositions = new Stack<BlockPos>();
                    pendingPositions.add(new BlockPos(pos));
                    while(!pendingPositions.empty()) {
                        BlockPos checkingPos = pendingPositions.pop();
                        for(EnumFacing d : EnumFacing.VALUES) {
                            BlockPos newPos = checkingPos.offset(d);
                            if(world.getBlockState(newPos).getBlock() == Fluids.oil.getBlock() && FluidUtils.isSourceBlock(world, newPos) && oilPositions.add(newPos)) {
                                pendingPositions.add(newPos);
                            }
                        }
                    }
                    player.addChatComponentMessage(new ChatComponentTranslation("message.seismicSensor.foundOilDetails", EnumChatFormatting.GREEN.toString() + (startY - pos.getY()), EnumChatFormatting.GREEN.toString() + oilPositions.size() / 10 * 10));
                    return true;
                }
            }
            player.addChatComponentMessage(new ChatComponentTranslation("message.seismicSensor.noOilFound"));
        }
        return true; // we don't want to use the item.

    }
}
