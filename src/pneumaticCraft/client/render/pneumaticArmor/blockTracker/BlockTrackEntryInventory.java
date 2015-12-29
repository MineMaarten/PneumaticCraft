package pneumaticCraft.client.render.pneumaticArmor.blockTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import pneumaticCraft.api.client.pneumaticHelmet.IBlockTrackEntry;
import pneumaticCraft.api.client.pneumaticHelmet.InventoryTrackEvent;
import pneumaticCraft.client.render.pneumaticArmor.HUDHandler;
import pneumaticCraft.common.network.NetworkHandler;
import pneumaticCraft.common.network.PacketDescriptionPacketRequest;
import pneumaticCraft.common.util.IOHelper;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.lib.Log;

public class BlockTrackEntryInventory implements IBlockTrackEntry{

    public static Map tileEntityClassToNameMapping;
    private static List<String> invBlackList = new ArrayList<String>();// list of all inventories that could have crashed the helmet.

    public static void addTileEntityToBlackList(TileEntity te, Throwable e){
        e.printStackTrace();
        String title = te.getWorld().getBlockState(te.getPos()).getBlock().getLocalizedName();
        HUDHandler.instance().addMessage("Block tracking failed for " + title + "! A stacktrace can be found in the log.", new ArrayList<String>(), 60, 0xFFFF0000);
        invBlackList.add((String)BlockTrackEntryInventory.tileEntityClassToNameMapping.get(te.getClass()));
    }

    @Override
    public boolean shouldTrackWithThisEntry(IBlockAccess world, BlockPos pos, IBlockState state, TileEntity te){
        if(tileEntityClassToNameMapping == null) {
            try {
                tileEntityClassToNameMapping = (Map)ReflectionHelper.findField(TileEntity.class, "field_145853_j", "classToNameMap").get(null);
            } catch(Exception e) {
                Log.error("[BlockTrackEntryInventory.class] Uhm reflection failed here!");
                e.printStackTrace();
            }
        }
        if(te instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest)te;
            if(chest.adjacentChestXNeg != null || chest.adjacentChestZNeg != null) return false;
        }
        return te != null && !invBlackList.contains(tileEntityClassToNameMapping.get(te.getClass())) && te instanceof IInventory && ((IInventory)te).getSizeInventory() > 0 && !MinecraftForge.EVENT_BUS.post(new InventoryTrackEvent(te));
    }

    @Override
    public boolean shouldBeUpdatedFromServer(TileEntity te){
        if(te instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest)te;
            if(chest.adjacentChestXPos != null) NetworkHandler.sendToServer(new PacketDescriptionPacketRequest(chest.adjacentChestXPos.getPos()));
            if(chest.adjacentChestZPos != null) NetworkHandler.sendToServer(new PacketDescriptionPacketRequest(chest.adjacentChestZPos.getPos()));
        }
        return true;
    }

    @Override
    public int spamThreshold(){
        return 6;
    }

    @Override
    public void addInformation(World world, BlockPos pos, TileEntity te, List<String> infoList){
        IInventory inventory = IOHelper.getInventoryForTE(te);
        if(inventory != null) {
            boolean empty = true;
            ItemStack[] inventoryStacks = new ItemStack[inventory.getSizeInventory()];
            for(int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack iStack = inventory.getStackInSlot(i);
                if(iStack != null) empty = false;
                inventoryStacks[i] = iStack;
            }
            if(empty) {
                infoList.add("Contents: Empty");
            } else {
                infoList.add("Contents:");
                PneumaticCraftUtils.sortCombineItemStacksAndToString(infoList, inventoryStacks);
            }
        }
    }

    @Override
    public String getEntryName(){
        return "blockTracker.module.inventories";
    }
}
