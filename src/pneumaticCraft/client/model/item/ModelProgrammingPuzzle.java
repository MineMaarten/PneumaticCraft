package pneumaticCraft.client.model.item;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelCustomData;
import net.minecraftforge.client.model.IModelPart;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.TRSRTransformation;

import org.apache.commons.lang3.tuple.Pair;

import pneumaticCraft.common.item.ItemProgrammingPuzzle;
import pneumaticCraft.common.progwidgets.IProgWidget;
import pneumaticCraft.common.progwidgets.ProgWidgetStart;
import pneumaticCraft.common.progwidgets.WidgetRegistrator;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class ModelProgrammingPuzzle implements IModel, IModelCustomData{
    public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation("forge", "dynbucket"), "inventory");

    // minimal Z offset to prevent depth-fighting
    private static final float NORTH_Z_BASE = 7.496f / 16f;
    private static final float SOUTH_Z_BASE = 8.504f / 16f;
    private static final float NORTH_Z_FLUID = 7.498f / 16f;
    private static final float SOUTH_Z_FLUID = 8.502f / 16f;

    public static final IModel MODEL = new ModelProgrammingPuzzle();

    private final IProgWidget widget;

    public ModelProgrammingPuzzle(){
        this(new ProgWidgetStart());
    }

    public ModelProgrammingPuzzle(IProgWidget widget){
        this.widget = widget;
    }

    @Override
    public Collection<ResourceLocation> getDependencies(){
        return ImmutableList.of();
    }

    @Override
    public Collection<ResourceLocation> getTextures(){
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
        for(IProgWidget widget : WidgetRegistrator.registeredWidgets) {
            builder.add(getWidgetTexture(widget));
        }
        return builder.build();
    }

    private static ResourceLocation getWidgetTexture(IProgWidget widget){
        String resourcePath = widget.getTexture().toString();
        resourcePath = resourcePath.replace("textures/", "").replace(".png", "");
        return new ResourceLocation(resourcePath);
    }

    @Override
    public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){

        ImmutableMap<TransformType, TRSRTransformation> transformMap = IPerspectiveAwareModel.MapWrapper.getTransforms(state);

        TRSRTransformation transform = state.apply(Optional.<IModelPart> absent()).or(TRSRTransformation.identity());
        TextureAtlasSprite widgetSprite = bakedTextureGetter.apply(getWidgetTexture(widget));
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        int width = widget.getWidth() + (widget.getParameters() != null && widget.getParameters().length > 0 ? 10 : 0);
        int height = widget.getHeight() + (widget.hasStepOutput() ? 5 : 0);

        Pair<Double, Double> maxUV = widget.getMaxUV();
        int textureSize = widget.getTextureSize();
        float scale = 1F / (float)Math.max(maxUV.getLeft(), maxUV.getRight());
        float transX = 0;//maxUV.getLeft().floatValue();
        float transY = -1 + maxUV.getRight().floatValue();
        transform = transform.compose(new TRSRTransformation(new Vector3f(0, 0, 0), null, new Vector3f(scale, scale, 1), null));
        transform = transform.compose(new TRSRTransformation(new Vector3f(transX, transY, 0), null, new Vector3f(1, 1, 1), null));

        builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16 * maxUV.getLeft().floatValue(), 16 * maxUV.getRight().floatValue(), NORTH_Z_BASE, widgetSprite, EnumFacing.NORTH, 0xffffffff));
        builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16 * maxUV.getLeft().floatValue(), 16 * maxUV.getRight().floatValue(), SOUTH_Z_BASE, widgetSprite, EnumFacing.SOUTH, 0xffffffff));

        return new BakedProgrammingPuzzle(this, builder.build(), widgetSprite, format, Maps.immutableEnumMap(transformMap), Maps.<String, IFlexibleBakedModel> newHashMap());
    }

    @Override
    public IModelState getDefaultState(){
        return TRSRTransformation.identity();
    }

    @Override
    public IModel process(ImmutableMap<String, String> customData){
        String progWidgetName = customData.get("progWidget");
        IProgWidget widget = WidgetRegistrator.getWidgetFromName(progWidgetName);

        if(widget == null) throw new IllegalStateException("Invalid widget: " + progWidgetName);

        // create new model with correct widget
        return new ModelProgrammingPuzzle(widget);
    }

    public enum LoaderProgrammingPuzzle implements ICustomModelLoader{
        instance;

        @Override
        public boolean accepts(ResourceLocation modelLocation){
            return modelLocation.getResourceDomain().equals("pneumaticcraft") && modelLocation.getResourcePath().contains("programmingPuzzle");
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) throws IOException{
            return MODEL;
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager){
            // no need to clear cache since we create a new model instance
        }
    }

    // the dynamic bucket is based on the empty bucket
    protected static class BakedProgrammingPuzzle extends ItemLayerModel.BakedModel implements ISmartItemModel,
            IPerspectiveAwareModel{

        private final ModelProgrammingPuzzle parent;
        private final Map<String, IFlexibleBakedModel> cache; // contains all the baked models since they'll never change
        private final ImmutableMap<TransformType, TRSRTransformation> transforms;

        public BakedProgrammingPuzzle(ModelProgrammingPuzzle parent, ImmutableList<BakedQuad> quads,
                TextureAtlasSprite particle, VertexFormat format,
                ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms,
                Map<String, IFlexibleBakedModel> cache){
            super(quads, particle, format);
            this.parent = parent;
            this.transforms = transforms;
            this.cache = cache;
        }

        @Override
        public IBakedModel handleItemState(ItemStack stack){
            IProgWidget widget = ItemProgrammingPuzzle.getWidgetForPiece(stack);
            if(widget == null) return this;
            String name = widget.getWidgetString();

            if(!cache.containsKey(name)) {
                IModel model = parent.process(ImmutableMap.of("progWidget", name));
                Function<ResourceLocation, TextureAtlasSprite> textureGetter;
                textureGetter = new Function<ResourceLocation, TextureAtlasSprite>(){
                    @Override
                    public TextureAtlasSprite apply(ResourceLocation location){
                        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
                    }
                };

                IFlexibleBakedModel bakedModel = model.bake(new SimpleModelState(transforms), getFormat(), textureGetter);
                cache.put(name, bakedModel);
                return bakedModel;
            }

            return cache.get(name);
        }

        @Override
        public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType){
            return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, transforms, cameraTransformType);
        }
    }
}
