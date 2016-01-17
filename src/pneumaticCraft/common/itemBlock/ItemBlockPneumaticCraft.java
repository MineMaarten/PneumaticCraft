package pneumaticCraft.common.itemBlock;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidBase;
import pneumaticCraft.common.block.BlockPneumaticCraft;
import pneumaticCraft.lib.Log;
import pneumaticCraft.lib.Names;

public class ItemBlockPneumaticCraft extends ItemBlock{
    private BlockPneumaticCraft block;

    public ItemBlockPneumaticCraft(Block block){
        super(block);
        if(block instanceof BlockPneumaticCraft) {
            this.block = (BlockPneumaticCraft)block;
        } else {
            if(!(block instanceof BlockAir) && !(block instanceof BlockFluidBase)) {
                Log.warning("Block " + block.getUnlocalizedName() + " does not extend BlockPneumaticCraft! No tooltip displayed");
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean extraInfo){
        super.addInformation(stack, player, info, extraInfo);
        if(block != null) block.addInformation(stack, player, info, extraInfo);
    }

    public void registerItemVariants(){
        List<ItemStack> stacks = new ArrayList<ItemStack>();
        getSubItems(this, null, stacks);
        for(ItemStack stack : stacks) {
            ResourceLocation resLoc = new ResourceLocation(Names.MOD_ID, getModelLocation(stack));
            ModelBakery.registerItemVariants(this, resLoc);
            System.out.println("Registering item block texture: " + resLoc + ", damage: " + stack.getItemDamage());
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, stack.getItemDamage(), new ModelResourceLocation(resLoc, "inventory"));
        }
    }

    protected String getModelLocation(ItemStack stack){
        return stack.getUnlocalizedName().substring(5);
    }

}
