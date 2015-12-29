package pneumaticCraft.common.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public abstract class AbstractRecipe implements IRecipe{

    /**
     * Copied from ShapedRecipes
     */
    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv){
        ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

        for(int i = 0; i < aitemstack.length; ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            aitemstack[i] = net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack);
        }

        return aitemstack;
    }

}
