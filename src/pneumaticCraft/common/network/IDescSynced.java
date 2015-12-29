package pneumaticCraft.common.network;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import pneumaticCraft.common.inventory.SyncedField;

public interface IDescSynced{
    public static enum Type{
        TILE_ENTITY, SEMI_BLOCK;
    }

    public Type getSyncType();

    public List<SyncedField> getDescriptionFields();

    public void writeToPacket(NBTTagCompound tag);

    public void readFromPacket(NBTTagCompound tag);

    public BlockPos getPos();

    public void onDescUpdate();
}
