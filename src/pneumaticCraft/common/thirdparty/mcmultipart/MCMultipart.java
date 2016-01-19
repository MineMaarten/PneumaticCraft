package pneumaticCraft.common.thirdparty.mcmultipart;

import java.util.ArrayList;
import java.util.List;

import mcmultipart.block.TileMultipart;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.MultipartRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;
import pneumaticCraft.common.thirdparty.IThirdParty;
import pneumaticCraft.lib.Names;

public class MCMultipart implements IThirdParty{

    public static Item pressureTube;
    public static Item advancedPressureTube;

    @Override
    public void preInit(){
        MultipartRegistry.registerPart(PartPressureTube.class, "pressureTube");
        MultipartRegistry.registerPart(PartAdvancedPressureTube.class, "advancedPressureTube");
        MultipartRegistry.registerPartConverter(new PartConverter());

        pressureTube = new ItemPartPressureTube();
        GameRegistry.registerItem(pressureTube, "part.pressureTube");
        advancedPressureTube = new ItemPartAdvancedPressureTube();
        GameRegistry.registerItem(advancedPressureTube, "part.advancedPressureTube");

    }

    @Override
    public void init(){

    }

    @Override
    public void postInit(){

    }

    @Override
    public void clientSide(){

    }

    @Override
    public void clientInit(){
        ResourceLocation resLoc = new ResourceLocation(Names.MOD_ID, "pressureTube");
        ModelBakery.registerItemVariants(pressureTube, resLoc);
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(pressureTube, 0, new ModelResourceLocation(resLoc, "inventory"));

    }

    private static TileMultipart getMultipartTile(IBlockAccess access, BlockPos pos){
        TileEntity te = access.getTileEntity(pos);
        return te instanceof TileMultipart ? (TileMultipart)te : null;
    }

    /* public static TMultiPart getMultiPart(IBlockAccess w, ChunkPosition bc, int part){
         TileMultipart t = getMultipartTile(w, bc);
         if(t != null) return t.partMap(part);
         return null;
     }*/

    public static <T> T getMultiPart(IBlockAccess access, BlockPos pos, Class<T> searchedClass){
        TileMultipart t = getMultipartTile(access, pos);
        return t == null ? null : getMultiPart(t, searchedClass);
    }

    public static <T> T getMultiPart(TileMultipart t, Class<T> searchedClass){
        for(IMultipart part : t.getParts()) {
            if(searchedClass.isAssignableFrom(part.getClass())) return (T)part;
        }
        return null;
    }

    public static <T> Iterable<T> getMultiParts(TileMultipart t, Class<T> searchedClass){
        List<T> parts = new ArrayList<T>();
        for(IMultipart part : t.getParts()) {
            if(searchedClass.isAssignableFrom(part.getClass())) parts.add((T)part);
        }
        return parts;
    }
}
