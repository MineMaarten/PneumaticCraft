package pneumaticCraft.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pneumaticCraft.PneumaticCraft;
import pneumaticCraft.api.block.IPneumaticWrenchable;
import pneumaticCraft.api.item.IUpgradeAcceptor;
import pneumaticCraft.common.item.Itemss;
import pneumaticCraft.common.thirdparty.ModInteractionUtils;
import pneumaticCraft.common.tileentity.IComparatorSupport;
import pneumaticCraft.common.tileentity.TileEntityBase;
import pneumaticCraft.common.tileentity.TileEntityPneumaticBase;
import pneumaticCraft.common.util.FluidUtils;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.lib.ModIds;
import pneumaticCraft.proxy.CommonProxy.EnumGuiId;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = ModIds.COMPUTERCRAFT)
public abstract class BlockPneumaticCraft extends BlockContainer implements IPneumaticWrenchable, IUpgradeAcceptor,
        IPeripheralProvider{

    public static final PropertyEnum<EnumFacing> ROTATION = PropertyEnum.<EnumFacing> create("facing", EnumFacing.class);

    protected BlockPneumaticCraft(Material par2Material){
        super(par2Material);
        setCreativeTab(PneumaticCraft.tabPneumaticCraft);
        setHardness(3.0F);
        setResistance(10.0F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata){
        try {
            TileEntity te = getTileEntityClass().newInstance();
            te.setWorldObj(world);
            return te;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected abstract Class<? extends TileEntity> getTileEntityClass();

    public EnumGuiId getGuiID(){
        return null;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing face, float par7, float par8, float par9){
        if(player.isSneaking() || getGuiID() == null || isRotatable() && player.getCurrentEquippedItem() != null && (player.getCurrentEquippedItem().getItem() == Itemss.manometer || ModInteractionUtils.getInstance().isModdedWrench(player.getCurrentEquippedItem().getItem()))) return false;
        else {
            if(!world.isRemote) {
                TileEntity te = world.getTileEntity(pos);

                List<ItemStack> returnedItems = new ArrayList<ItemStack>();
                if(te != null && !FluidUtils.tryInsertingLiquid(te, player.getCurrentEquippedItem(), player.capabilities.isCreativeMode, returnedItems)) {
                    player.openGui(PneumaticCraft.instance, getGuiID().ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
                } else {
                    if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().stackSize <= 0) {
                        player.setCurrentItemOrArmor(0, null);
                    }
                    for(ItemStack returnedItem : returnedItems) {
                        returnedItem = returnedItem.copy();
                        if(player.getCurrentEquippedItem() == null) {
                            player.setCurrentItemOrArmor(0, returnedItem);
                        } else {
                            player.inventory.addItemStackToInventory(returnedItem);
                        }
                    }
                }
            }

            return true;
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack){
        if(isRotatable()) {
            EnumFacing rotation = PneumaticCraftUtils.getDirectionFacing(entity, canRotateToTopOrBottom());
            setRotation(world, pos, rotation, state);
        }
    }

    protected void setRotation(World world, BlockPos pos, EnumFacing rotation){
        setRotation(world, pos, rotation, world.getBlockState(pos));
    }

    protected EnumFacing getRotation(IBlockAccess world, BlockPos pos){
        return getRotation(world.getBlockState(pos));
    }

    protected EnumFacing getRotation(IBlockState state){
        return state.getValue(ROTATION);
    }

    private void setRotation(World world, BlockPos pos, EnumFacing rotation, IBlockState state){
        world.setBlockState(pos, state.withProperty(ROTATION, rotation));
    }

    public boolean isRotatable(){
        return false;
    }

    protected boolean canRotateToTopOrBottom(){
        return false;
    }

    @Override
    protected BlockState createBlockState(){
        if(isRotatable()) {
            return new BlockState(this, new IProperty[]{ROTATION});
        } else {
            return super.createBlockState();
        }
    }

    @Override
    public int getMetaFromState(IBlockState state){
        if(isRotatable()) {
            return state.getValue(ROTATION).ordinal();
        } else {
            return super.getMetaFromState(state);
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta){
        if(isRotatable()) {
            return super.getStateFromMeta(meta).withProperty(ROTATION, EnumFacing.getFront(meta));
        } else {
            return super.getStateFromMeta(meta);
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state){
        dropInventory(world, pos);
        super.breakBlock(world, pos, state);
    }

    protected void dropInventory(World world, BlockPos pos){

        TileEntity tileEntity = world.getTileEntity(pos);

        if(!(tileEntity instanceof IInventory)) return;

        IInventory inventory = (IInventory)tileEntity;
        Random rand = new Random();
        for(int i = getInventoryDropStartSlot(inventory); i < getInventoryDropEndSlot(inventory); i++) {

            ItemStack itemStack = inventory.getStackInSlot(i);

            if(itemStack != null && itemStack.stackSize > 0) {
                float dX = rand.nextFloat() * 0.8F + 0.1F;
                float dY = rand.nextFloat() * 0.8F + 0.1F;
                float dZ = rand.nextFloat() * 0.8F + 0.1F;

                EntityItem entityItem = new EntityItem(world, pos.getX() + dX, pos.getY() + dY, pos.getZ() + dZ, new ItemStack(itemStack.getItem(), itemStack.stackSize, itemStack.getItemDamage()));

                if(itemStack.hasTagCompound()) {
                    entityItem.getEntityItem().setTagCompound((NBTTagCompound)itemStack.getTagCompound().copy());
                }

                float factor = 0.05F;
                entityItem.motionX = rand.nextGaussian() * factor;
                entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
                entityItem.motionZ = rand.nextGaussian() * factor;
                world.spawnEntityInWorld(entityItem);
                itemStack.stackSize = 0;
            }
        }
    }

    protected int getInventoryDropStartSlot(IInventory inventory){
        return 0;
    }

    protected int getInventoryDropEndSlot(IInventory inventory){
        return inventory.getSizeInventory();
    }

    @Override
    public boolean rotateBlock(World world, EntityPlayer player, BlockPos pos, EnumFacing side){
        if(player.isSneaking()) {
            if(!player.capabilities.isCreativeMode) dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
            world.setBlockToAir(pos);
            return true;
        } else {
            if(isRotatable()) {
                IBlockState state = world.getBlockState(pos);
                if(!rotateCustom(world, pos, state, side)) {
                    TileEntityBase te = (TileEntityBase)world.getTileEntity(pos);
                    if(rotateForgeWay()) {
                        if(!canRotateToTopOrBottom()) side = EnumFacing.UP;
                        if(getRotation(world, pos).getAxis() != side.getAxis()) setRotation(world, pos, getRotation(world, pos).rotateAround(side.getAxis()));
                    } else {
                        do {
                            setRotation(world, pos, EnumFacing.getFront(getRotation(world, pos).ordinal() + 1));
                        } while(canRotateToTopOrBottom() || getRotation(world, pos).getAxis() != Axis.Y);
                    }
                    te.onBlockRotated();
                }
                return true;
            } else {
                return false;
            }
        }
    }

    protected boolean rotateForgeWay(){
        return true;
    }

    protected boolean rotateCustom(World world, BlockPos pos, IBlockState state, EnumFacing side){
        return false;
    }

    /**
     * Called when a tile entity on a side of this block changes is created or is destroyed.
     * @param world The world
     * @param x The x position of this block instance
     * @param y The y position of this block instance
     * @param z The z position of this block instance
     * @param tileX The x position of the tile that changed
     * @param tileY The y position of the tile that changed
     * @param tileZ The z position of the tile that changed
     */
    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos tilePos){
        if(world instanceof World && !((World)world).isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if(te instanceof TileEntityBase) {
                ((TileEntityBase)te).onNeighborTileUpdate();
            }
        }
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block){
        if(!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if(te instanceof TileEntityBase) {
                ((TileEntityBase)te).onNeighborBlockUpdate();
            }
        }
    }

    /**
     * Produce an peripheral implementation from a block location.
     * @see dan200.computercraft.api.ComputerCraftAPI#registerPeripheralProvider(IPeripheralProvider)
     * @return a peripheral, or null if there is not a peripheral here you'd like to handle.
     */
    @Override
    @Optional.Method(modid = ModIds.COMPUTERCRAFT)
    public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side){
        TileEntity te = world.getTileEntity(pos);
        return te instanceof IPeripheral ? (IPeripheral)te : null;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List curInfo, boolean extraInfo){
        if(PneumaticCraft.proxy.isSneakingInGui()) {
            TileEntity te = createNewTileEntity(player.worldObj, 0);
            if(te instanceof TileEntityPneumaticBase) {
                float pressure = ((TileEntityPneumaticBase)te).dangerPressure;
                curInfo.add(EnumChatFormatting.YELLOW + I18n.format("gui.tooltip.maxPressure", pressure));
            }
        }

        String info = "gui.tab.info." + stack.getUnlocalizedName();
        String translatedInfo = I18n.format(info);
        if(!translatedInfo.equals(info)) {
            if(PneumaticCraft.proxy.isSneakingInGui()) {
                translatedInfo = EnumChatFormatting.AQUA + translatedInfo.substring(2);
                if(!Loader.isModLoaded(ModIds.IGWMOD)) translatedInfo += " \\n \\n" + I18n.format("gui.tab.info.assistIGW");
                curInfo.addAll(PneumaticCraftUtils.convertStringIntoList(translatedInfo, 40));
            } else {
                curInfo.add(EnumChatFormatting.AQUA + I18n.format("gui.tooltip.sneakForInfo"));
            }
        }
    }

    /**
     * If this returns true, then comparators facing away from this block will use the value from
     * getComparatorInputOverride instead of the actual redstone signal strength.
     */
    @Override
    public boolean hasComparatorInputOverride(){
        return IComparatorSupport.class.isAssignableFrom(getTileEntityClass());
    }

    /**
     * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
     * strength when this block inputs to a comparator.
     */
    @Override
    public int getComparatorInputOverride(World world, BlockPos pos){
        return ((IComparatorSupport)world.getTileEntity(pos)).getComparatorValue();
    }

    @Override
    public Set<Item> getApplicableUpgrades(){
        return ((IUpgradeAcceptor)createNewTileEntity(null, 0)).getApplicableUpgrades();
    }

    @Override
    public String getName(){
        return getUnlocalizedName() + ".name";
    }

    @Override
    public int getRenderType(){
        return 3;
    }
}
