package pneumaticCraft.common.progwidgets;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.client.gui.GuiProgrammer;
import pneumaticCraft.client.gui.programmer.GuiProgWidgetDropItem;
import pneumaticCraft.common.ai.DroneAIImExBase;
import pneumaticCraft.common.ai.IDroneBase;
import pneumaticCraft.common.item.ItemPlastic;
import pneumaticCraft.lib.Textures;

public class ProgWidgetDropItem extends ProgWidgetInventoryBase implements IItemDropper{
    private boolean dropStraight;

    @Override
    public String getWidgetString(){
        return "dropItem";
    }

    @Override
    public ResourceLocation getTexture(){
        return Textures.PROG_WIDGET_DROP_ITEM;
    }

    @Override
    public boolean dropStraight(){
        return dropStraight;
    }

    @Override
    public void setDropStraight(boolean dropStraight){
        this.dropStraight = dropStraight;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        tag.setBoolean("dropStraight", dropStraight);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        dropStraight = tag.getBoolean("dropStraight");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen getOptionWindow(GuiProgrammer guiProgrammer){
        return new GuiProgWidgetDropItem(this, guiProgrammer);
    }

    @Override
    public EntityAIBase getWidgetAI(IDroneBase drone, IProgWidget widget){
        return new DroneAIImExBase(drone, (ProgWidgetAreaItemBase)widget){

            private final Set<BlockPos> visitedPositions = new HashSet<BlockPos>();

            @Override
            public boolean shouldExecute(){
                boolean shouldExecute = false;
                for(int i = 0; i < drone.getInv().getSizeInventory(); i++) {
                    ItemStack stack = drone.getInv().getStackInSlot(i);
                    if(stack != null && widget.isItemValidForFilters(stack)) {
                        shouldExecute = super.shouldExecute();
                        break;
                    }
                }
                return shouldExecute;
            }

            @Override
            protected boolean moveIntoBlock(){
                return true;
            }

            @Override
            protected boolean isValidPosition(BlockPos pos){
                return !visitedPositions.contains(pos);//another requirement is that the drone can navigate to this exact block, but that's handled by the pathfinder.
            }

            @Override
            protected boolean doBlockInteraction(BlockPos pos, double distToBlock){
                visitedPositions.add(pos);
                for(int i = 0; i < drone.getInv().getSizeInventory(); i++) {
                    ItemStack stack = drone.getInv().getStackInSlot(i);
                    if(stack != null && widget.isItemValidForFilters(stack)) {

                        if(useCount() && getRemainingCount() < stack.stackSize) {
                            stack = stack.splitStack(getRemainingCount());
                            decreaseCount(getRemainingCount());
                        } else {
                            decreaseCount(stack.stackSize);
                            drone.getInv().setInventorySlotContents(i, null);
                        }
                        EntityItem item = new EntityItem(drone.world(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                        if(((IItemDropper)widget).dropStraight()) {
                            item.motionX = 0;
                            item.motionY = 0;
                            item.motionZ = 0;
                        }
                        drone.world().spawnEntityInWorld(item);
                        if(useCount() && getRemainingCount() == 0) break;
                    }
                }
                return false;
            }

        };
    }

    @Override
    public int getCraftingColorIndex(){
        return ItemPlastic.POTION_PLANT_DAMAGE;
    }

    @Override
    protected boolean isUsingSides(){
        return false;
    }

    @Override
    public String getExtraStringInfo(){
        return dropStraight() ? "Straight" : "Random";
    }
}
