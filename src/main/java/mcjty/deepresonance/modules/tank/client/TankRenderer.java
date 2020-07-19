package mcjty.deepresonance.modules.tank.client;

import com.google.common.base.Preconditions;
import elec332.core.api.client.IIconRegistrar;
import elec332.core.api.client.model.IElecModelBakery;
import elec332.core.api.client.model.IElecQuadBakery;
import elec332.core.api.client.model.IElecTemplateBakery;
import elec332.core.api.client.model.IModelAndTextureLoader;
import mcjty.deepresonance.util.DeepResonanceResourceLocation;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 11-1-2020
 */
@OnlyIn(Dist.CLIENT)
public class TankRenderer implements IModelAndTextureLoader {

    public static final ResourceLocation TOP_TEXTURE = new DeepResonanceResourceLocation("block/tank_top");
    public static final ResourceLocation BOTTOM_TEXTURE = new DeepResonanceResourceLocation("block/tank_bottom");
    public static final ResourceLocation SIDE_TEXTURE = new DeepResonanceResourceLocation("block/tank_side");

    public static final TankRenderer INSTANCE = new TankRenderer();

    private final TextureAtlasSprite[] textures;
    private final BakedQuad[] quads;
    private IBakedModel tankModel;
    private boolean initialized;

    private TankRenderer() {
        textures = new TextureAtlasSprite[3];
        quads = new BakedQuad[Direction.values().length];
    }

    public boolean hasInitialized() {
        return initialized;
    }

    @Nonnull
    public BakedQuad getInsideQuad(Direction direction) {
        return quads[Preconditions.checkNotNull(direction).ordinal()];
    }

    @Nonnull
    @SuppressWarnings("all")
    public List<BakedQuad> getModelQuads(Direction side) {
        return tankModel.getQuads(null, side, null);
    }

    public IBakedModel setModel(IBakedModel model) {
        this.tankModel = model;
        return model;
    }

    @Override
    public void registerTextures(IIconRegistrar iconRegistrar) {
        textures[Direction.DOWN.ordinal()] = iconRegistrar.registerSprite(BOTTOM_TEXTURE);
        textures[Direction.UP.ordinal()] = iconRegistrar.registerSprite(TOP_TEXTURE);
        textures[2] = iconRegistrar.registerSprite(SIDE_TEXTURE);
    }

    @Override
    public void registerModels(IElecQuadBakery quadBakery, IElecModelBakery modelBakery, IElecTemplateBakery templateBakery) {
        float offset = 0.05f;
        for (Direction dir : Direction.values()) {
            TextureAtlasSprite tex = textures[dir.getAxis() == Direction.Axis.Y ? dir.ordinal() : 2];
            Vector3f start = new Vector3f(0, 0, 0);
            Vector3f end = new Vector3f(16, 16, 16);
            Vector3f offzet = new Vector3f(new Vec3d(dir.getDirectionVec()));
            offzet.mul(16 - offset);
            start.sub(offzet);
            end.sub(offzet);
            quads[dir.getOpposite().ordinal()] = quadBakery.bakeQuad(start, end, tex, dir);
        }
        this.initialized = true;
    }

}
