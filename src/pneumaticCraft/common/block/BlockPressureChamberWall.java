package pneumaticCraft.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import pneumaticCraft.common.tileentity.TileEntityPressureChamberValve;
import pneumaticCraft.common.tileentity.TileEntityPressureChamberWall;

public class BlockPressureChamberWall extends BlockPressureChamberWallBase{

    public enum EnumWallState implements IStringSerializable{
        NONE, CENTER, XEDGE, ZEDGE, YEDGE, XMIN_YMIN_ZMIN, XMIN_YMIN_ZMAX, XMIN_YMAX_ZMIN, XMIN_YMAX_ZMAX;

        @Override
        public String getName(){
            return toString().toLowerCase();
        }
    }

    public static final PropertyEnum<EnumWallState> WALL_STATE = PropertyEnum.<EnumWallState> create("wallState", EnumWallState.class);

    public BlockPressureChamberWall(Material par2Material){
        super(par2Material);
    }

    @Override
    protected BlockState createBlockState(){
        return new BlockState(this, WALL_STATE);
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return state.getValue(WALL_STATE).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta){
        return super.getStateFromMeta(meta).withProperty(WALL_STATE, EnumWallState.values()[meta]);
    }

    public IBlockState updateState(IBlockState state, IBlockAccess world, BlockPos pos){
        state = super.getExtendedState(state, world, pos);
        TileEntityPressureChamberWall wall = (TileEntityPressureChamberWall)world.getTileEntity(pos);
        EnumWallState wallState;
        if(wall != null) {
            TileEntityPressureChamberValve core = wall.getCore();
            if(core != null) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                boolean xMin = x == core.multiBlockX;
                boolean yMin = y == core.multiBlockY;
                boolean zMin = z == core.multiBlockZ;
                boolean xMax = x == core.multiBlockX + core.multiBlockSize - 1;
                boolean yMax = y == core.multiBlockY + core.multiBlockSize - 1;
                boolean zMax = z == core.multiBlockZ + core.multiBlockSize - 1;

                //Corners
                if(xMin && yMin && zMin || xMax && yMax && zMax) {
                    wallState = EnumWallState.XMIN_YMIN_ZMIN;
                } else if(xMin && yMin && zMax || xMax && yMax && zMin) {
                    wallState = EnumWallState.XMIN_YMIN_ZMAX;
                } else if(xMin && yMax && zMax || xMax && yMin && zMin) {
                    wallState = EnumWallState.XMIN_YMAX_ZMAX;
                } else if(xMin && yMax && zMin || xMax && yMin && zMax) {
                    wallState = EnumWallState.XMIN_YMAX_ZMIN;
                }
                //Edges
                else if(yMin && xMin || yMax && xMax || yMin && xMax || yMax && xMin) {
                    wallState = EnumWallState.XEDGE;
                } else if(yMin && zMin || yMax && zMax || yMin && zMax || yMax && zMin) {
                    wallState = EnumWallState.ZEDGE;
                } else if(!yMin && !yMax) {
                    if(xMin && zMin || xMax && zMax || xMin && zMax || xMax && zMin) {
                        wallState = EnumWallState.YEDGE;
                    } else {
                        wallState = EnumWallState.CENTER;
                    }
                } else {
                    wallState = EnumWallState.CENTER;
                }
            } else {
                wallState = EnumWallState.NONE;
            }
        } else {
            wallState = EnumWallState.NONE;
        }
        state = state.withProperty(WALL_STATE, wallState);
        return state;
    }
}
