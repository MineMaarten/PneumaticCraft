package pneumaticCraft.common.tileentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import pneumaticCraft.common.block.Blockss;
import pneumaticCraft.common.network.DescSynced;
import pneumaticCraft.common.network.FilteredSynced;
import pneumaticCraft.common.network.GuiSynced;
import pneumaticCraft.common.network.LazySynced;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.lib.GuiConstants;
import pneumaticCraft.lib.PneumaticValues;
import pneumaticCraft.lib.Sounds;

public class TileEntityPressureChamberInterface extends TileEntityPressureChamberWall implements ISidedInventory,
        IGUITextFieldSensitive, IRedstoneControlled{
    @DescSynced
    @FilteredSynced(index = 0)
    private ItemStack[] inventory = new ItemStack[14];
    @DescSynced
    @LazySynced
    public int inputProgress;
    public int oldInputProgress;
    @DescSynced
    @LazySynced
    public int outputProgress;
    public int oldOutputProgress;
    public static final int MAX_PROGRESS = 40;
    private static final int UPGRADE_SLOT_START = 1;
    private static final int UPGRADE_SLOT_END = 4;
    @GuiSynced
    public EnumInterfaceMode interfaceMode = EnumInterfaceMode.NONE;
    @GuiSynced
    private boolean enoughAir = true;
    @DescSynced
    public EnumFilterMode filterMode = EnumFilterMode.ITEM;
    @GuiSynced
    public int creativeTabID;
    @DescSynced
    public String itemNameFilter = "";
    private boolean isOpeningI;//used to determine sounds.
    private boolean isOpeningO;//used to determine sounds.
    @DescSynced
    private boolean shouldOpenInput, shouldOpenOutput;
    @GuiSynced
    public int redstoneMode;
    private int inputTimeOut;
    private int oldItemCount;

    public enum EnumInterfaceMode{
        NONE, IMPORT, EXPORT;
    }

    public enum EnumFilterMode{
        ITEM, CREATIVE_TAB, NAME_BEGINS, NAME_CONTAINS;
    }

    public TileEntityPressureChamberInterface(){
        setUpgradeSlots(new int[]{UPGRADE_SLOT_START, 2, 3, UPGRADE_SLOT_END});
    }

    @Override
    public void update(){
        super.update();

        boolean wasOpeningI = isOpeningI;
        boolean wasOpeningO = isOpeningO;
        oldInputProgress = inputProgress;
        oldOutputProgress = outputProgress;
        TileEntityPressureChamberValve core = getCore();

        if(!worldObj.isRemote) {
            int itemCount = inventory[0] != null ? inventory[0].stackSize : 0;
            if(oldItemCount != itemCount) {
                oldItemCount = itemCount;
                inputTimeOut = 0;
            }

            interfaceMode = getInterfaceMode(core);
            enoughAir = true;

            if(interfaceMode != EnumInterfaceMode.NONE) {
                if(inventory[0] != null && ++inputTimeOut > 10) {
                    shouldOpenInput = false;
                    if(inputProgress == 0) {
                        shouldOpenOutput = true;
                        if(outputProgress == MAX_PROGRESS) {
                            if(interfaceMode == EnumInterfaceMode.IMPORT) {
                                outputInChamber();
                            } else {
                                exportToInventory();
                            }
                        }
                    }
                } else {
                    shouldOpenOutput = false;
                    if(outputProgress == 0) {
                        shouldOpenInput = true;
                        if(interfaceMode == EnumInterfaceMode.EXPORT && inputProgress == MAX_PROGRESS && redstoneAllows()) {
                            importFromChamber(core);
                        }
                    }
                }
            } else {
                shouldOpenInput = false;
                shouldOpenOutput = false;
            }
        }

        int speed = (int)getSpeedMultiplierFromUpgrades(getUpgradeSlots());

        if(shouldOpenInput) {
            inputProgress = Math.min(inputProgress + speed, MAX_PROGRESS);
            isOpeningI = true;
        } else {
            inputProgress = Math.max(inputProgress - speed, 0);
            isOpeningI = false;
        }

        if(shouldOpenOutput) {
            outputProgress = Math.min(outputProgress + speed, MAX_PROGRESS);
            isOpeningO = true;
        } else {
            outputProgress = Math.max(outputProgress - speed, 0);
            isOpeningO = false;
        }

        if(worldObj.isRemote && (wasOpeningI != isOpeningI || wasOpeningO != isOpeningO)) {
            worldObj.playSound(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5, Sounds.INTERFACE_DOOR, 0.1F, 1.0F, true);
        }
    }

    private void exportToInventory(){
        EnumFacing facing = getRotation();
        TileEntity te = worldObj.getTileEntity(getPos().offset(facing));
        if(te != null) {
            EnumFacing side = facing.getOpposite();
            ItemStack leftoverStack = PneumaticCraftUtils.exportStackToInventory(te, inventory[0], side);
            if(leftoverStack == null || leftoverStack.stackSize == 0) {
                inventory[0] = null;
            }
        }
    }

    private void importFromChamber(TileEntityPressureChamberValve core){
        ItemStack[] chamberStacks = core.getStacksInChamber();
        for(ItemStack chamberStack : chamberStacks) {
            if((inventory[0] == null || inventory[0].isItemEqual(chamberStack)) && isItemValidForSlot(0, chamberStack)) {
                int maxAllowedItems = Math.abs(core.getAirHandler(null).getAir()) / PneumaticValues.USAGE_CHAMBER_INTERFACE;
                if(maxAllowedItems > 0) {
                    if(inventory[0] != null) maxAllowedItems = Math.min(maxAllowedItems, chamberStack.getMaxStackSize() - inventory[0].stackSize);
                    int transferedItems = Math.min(chamberStack.stackSize, maxAllowedItems);
                    core.addAir((core.getAirHandler(null).getAir() > 0 ? -1 : 1) * transferedItems * PneumaticValues.USAGE_CHAMBER_INTERFACE);
                    ItemStack transferedStack = chamberStack.copy().splitStack(transferedItems);
                    ItemStack insertedStack = transferedStack.copy();
                    if(inventory[0] != null) insertedStack.stackSize += inventory[0].stackSize;
                    setInventorySlotContents(0, insertedStack);
                    core.clearStacksInChamber(transferedStack);
                }
            }
        }
    }

    private void outputInChamber(){
        TileEntityPressureChamberValve valve = getCore();
        if(valve != null) {
            for(EnumFacing d : EnumFacing.VALUES) {
                BlockPos neighborPos = getPos().offset(d);
                if(valve.isCoordWithinChamber(worldObj, neighborPos)) {
                    enoughAir = Math.abs(valve.getAirHandler(null).getAir()) > inventory[0].stackSize * PneumaticValues.USAGE_CHAMBER_INTERFACE;
                    if(enoughAir) {
                        valve.addAir((valve.getAirHandler(null).getAir() > 0 ? -1 : 1) * inventory[0].stackSize * PneumaticValues.USAGE_CHAMBER_INTERFACE);
                        EntityItem item = new EntityItem(worldObj, neighborPos.getX() + 0.5, neighborPos.getY() + 0.5, neighborPos.getZ() + 0.5D, inventory[0].copy());
                        worldObj.spawnEntityInWorld(item);
                        setInventorySlotContents(0, null);
                        break;
                    }
                }
            }
        }
    }

    /*
     * public void setCore(TileEntityPressureChamberValve core){ boolean
     * wasNotNull = teValve != null; super.setCore(core); if(worldObj.isRemote)
     * return; if(core != null) modeNeedsChecking = true; else if(wasNotNull)
     * modeNeedsChecking = true; }
     */
    // figure out whether the Interface is exporting or importing.
    private EnumInterfaceMode getInterfaceMode(TileEntityPressureChamberValve core){
        if(core != null) {
            boolean xMid = getPos().getX() != core.multiBlockX && getPos().getX() != core.multiBlockX + core.multiBlockSize - 1;
            boolean yMid = getPos().getY() != core.multiBlockY && getPos().getY() != core.multiBlockY + core.multiBlockSize - 1;
            boolean zMid = getPos().getZ() != core.multiBlockZ && getPos().getZ() != core.multiBlockZ + core.multiBlockSize - 1;
            int meta = getBlockMetadata();
            if(xMid && yMid && meta == 2 || xMid && zMid && meta == 0 || yMid && zMid && meta == 4) {
                if(getPos().getX() == core.multiBlockX || getPos().getY() == core.multiBlockY || getPos().getZ() == core.multiBlockZ) {
                    return EnumInterfaceMode.EXPORT;
                } else {
                    return EnumInterfaceMode.IMPORT;
                }
            } else if(xMid && yMid && meta == 3 || xMid && zMid && meta == 1 || yMid && zMid && meta == 5) {
                if(getPos().getX() == core.multiBlockX || getPos().getY() == core.multiBlockY || getPos().getZ() == core.multiBlockZ) {
                    return EnumInterfaceMode.IMPORT;
                } else {
                    return EnumInterfaceMode.EXPORT;
                }
            }
        }
        return EnumInterfaceMode.NONE;
    }

    public List<String> getProblemStat(){
        List<String> textList = new ArrayList<String>();
        if(interfaceMode == EnumInterfaceMode.NONE) {
            textList.addAll(PneumaticCraftUtils.convertStringIntoList("\u00a77The Interface can't work.", GuiConstants.maxCharPerLineLeft));
            textList.addAll(PneumaticCraftUtils.convertStringIntoList("\u00a70-The Interface is not in a properly formed Pressure Chamber, and/or", GuiConstants.maxCharPerLineLeft));
            textList.addAll(PneumaticCraftUtils.convertStringIntoList("\u00a70-The Interface is not adjacent to an air block of the Pressure Chamber, and/or", GuiConstants.maxCharPerLineLeft));
            textList.addAll(PneumaticCraftUtils.convertStringIntoList("\u00a70-The Interface isn't orientated right.", GuiConstants.maxCharPerLineLeft));
        } else if(!redstoneAllows()) {
            textList.add("gui.tab.problems.redstoneDisallows");
        } else if(!enoughAir) {
            textList.addAll(PneumaticCraftUtils.convertStringIntoList("\u00a77There's not enough pressure in the Pressure Chamber to move the items.", GuiConstants.maxCharPerLineLeft));
            textList.addAll(PneumaticCraftUtils.convertStringIntoList("\u00a70Apply more pressure to the Pressure Chamber. The required pressure is dependent on the amount of items being transported.", GuiConstants.maxCharPerLineLeft));
        }
        return textList;
    }

    public boolean hasEnoughPressure(){
        return enoughAir;
    }

    // INVENTORY METHODS- && NBT
    // ------------------------------------------------------------

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);

        // Read in the ItemStacks in the inventory from NBT
        NBTTagList tagList = tag.getTagList("Items", 10);
        inventory = new ItemStack[getSizeInventory()];
        for(int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
            byte slot = tagCompound.getByte("Slot");
            if(slot >= 0 && slot < inventory.length) {
                inventory[slot] = ItemStack.loadItemStackFromNBT(tagCompound);
            }
        }

        outputProgress = tag.getInteger("outputProgress");
        inputProgress = tag.getInteger("inputProgress");
        interfaceMode = EnumInterfaceMode.values()[tag.getInteger("interfaceMode")];
        filterMode = EnumFilterMode.values()[tag.getInteger("filterMode")];
        creativeTabID = tag.getInteger("creativeTabID");
        itemNameFilter = tag.getString("itemNameFilter");
        redstoneMode = tag.getInteger("redstoneMode");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);

        // Write the ItemStacks in the inventory to NBT
        NBTTagList tagList = new NBTTagList();
        for(int i = 0; i < inventory.length; i++)
            if(inventory[i] != null) {
                NBTTagCompound tagCompound = new NBTTagCompound();
                tagCompound.setByte("Slot", (byte)i);
                inventory[i].writeToNBT(tagCompound);
                tagList.appendTag(tagCompound);
            }
        tag.setTag("Items", tagList);
        tag.setInteger("outputProgress", outputProgress);
        tag.setInteger("inputProgress", inputProgress);
        tag.setInteger("interfaceMode", interfaceMode.ordinal());
        tag.setInteger("filterMode", filterMode.ordinal());
        tag.setInteger("creativeTabID", creativeTabID);
        tag.setString("itemNameFilter", itemNameFilter);
        tag.setInteger("redstoneMode", redstoneMode);
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory(){

        return 14;
    }

    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int slot){
        return inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount){
        ItemStack itemStack = getStackInSlot(slot);
        if(itemStack != null) {
            if(itemStack.stackSize <= amount) {
                setInventorySlotContents(slot, null);
            } else {
                itemStack = itemStack.splitStack(amount);
                if(itemStack.stackSize == 0) {
                    setInventorySlotContents(slot, null);
                }
            }
        }

        return itemStack;
    }

    @Override
    public ItemStack removeStackFromSlot(int slot){
        ItemStack itemStack = getStackInSlot(slot);
        if(itemStack != null) {
            setInventorySlotContents(slot, null);
        }
        return itemStack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack){
        inventory[slot] = itemStack;
        if(itemStack != null && itemStack.stackSize > getInventoryStackLimit()) {
            itemStack.stackSize = getInventoryStackLimit();
        }
        if(!worldObj.isRemote && slot == 0) {
            sendDescriptionPacket();
        }
    }

    @Override
    public int getInventoryStackLimit(){
        return 64;
    }

    @Override
    public String getName(){
        return Blockss.pressureChamberInterface.getUnlocalizedName();
    }

    @Override
    public void openInventory(EntityPlayer player){}

    @Override
    public void closeInventory(EntityPlayer player){}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack iStack){
        if(iStack == null) return false;
        switch(filterMode){
            case ITEM:
                boolean filterEmpty = true;
                for(int i = 0; i < 9; i++) {
                    ItemStack filterStack = getStackInSlot(i + 5);
                    if(filterStack != null) {
                        filterEmpty = false;
                        if(iStack.isItemEqual(filterStack)) {
                            return true;
                        }
                    }
                }
                return filterEmpty;
            case CREATIVE_TAB:
                try {
                    int itemCreativeTabIndex = iStack.getItem().getCreativeTab() != null ? iStack.getItem().getCreativeTab().getTabIndex() : -1;
                    if(itemCreativeTabIndex == creativeTabID) {
                        return true;
                    }
                } catch(Throwable e) {//when we are SMP getCreativeTab() is client only.
                    filterMode = EnumFilterMode.NAME_BEGINS;
                }
                return false;
            case NAME_BEGINS:
                return iStack.getDisplayName().toLowerCase().startsWith(itemNameFilter.toLowerCase());
            case NAME_CONTAINS:
                return iStack.getDisplayName().toLowerCase().contains(itemNameFilter.toLowerCase());
        }
        return false;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer){
        return true;
    }

    @Override
    public boolean isGuiUseableByPlayer(EntityPlayer par1EntityPlayer){
        return worldObj.getTileEntity(getPos()) != this ? false : par1EntityPlayer.getDistanceSq(getPos().getX() + 0.5D, getPos().getY() + 0.5D, getPos().getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing var1){
        return new int[]{0};
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, EnumFacing j){
        return inputProgress == MAX_PROGRESS && j == getRotation().getOpposite() && redstoneAllows();
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemstack, EnumFacing j){
        return outputProgress == MAX_PROGRESS && j == getRotation();
    }

    @Override
    public void clear(){
        Arrays.fill(inventory, null);
    }

    @Override
    public void handleGUIButtonPress(int guiID, EntityPlayer player){
        if(guiID == 1) {
            if(filterMode.ordinal() >= EnumFilterMode.values().length - 1) {
                filterMode = EnumFilterMode.ITEM;
            } else {
                filterMode = EnumFilterMode.values()[filterMode.ordinal() + 1];
            }
            isItemValidForSlot(0, new ItemStack(Items.stick));//when an SideOnly exception is thrown this method automatically will set the filter mode to Item.

        } else if(guiID == 2) {
            creativeTabID++;
            if(creativeTabID == 5 || creativeTabID == 11) creativeTabID++;
            if(creativeTabID >= CreativeTabs.creativeTabArray.length) {
                creativeTabID = 0;
            }
        } else if(guiID == 0) {
            redstoneMode++;
            if(redstoneMode > 2) redstoneMode = 0;
        }
    }

    @Override
    public void setText(int textFieldID, String text){
        itemNameFilter = text;
    }

    @Override
    public String getText(int textFieldID){
        return itemNameFilter;
    }

    @Override
    public boolean hasCustomName(){
        return false;
    }

    @Override
    public int getRedstoneMode(){
        return redstoneMode;
    }
}
