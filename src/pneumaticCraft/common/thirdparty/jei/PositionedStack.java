package pneumaticCraft.common.thirdparty.jei;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

public class PositionedStack{
    private final List<ItemStack> stacks;
    private final int x, y;

    public PositionedStack(ItemStack stack, int x, int y){
        this(Arrays.asList(stack), x, y);
    }

    public PositionedStack(List<ItemStack> stacks, int x, int y){
        this.stacks = stacks;
        this.x = x;
        this.y = y;
    }

    public List<ItemStack> getStacks(){
        return stacks;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }
}
