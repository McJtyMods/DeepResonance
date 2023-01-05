package mcjty.deepresonance.modules.machines;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.datagen.BaseBlockStateProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;

import static mcjty.deepresonance.datagen.BlockStates.*;

public class DataGenHelper {

    public static BlockModelBuilder createCrystallizerModel(BaseBlockStateProvider provider) {
        BlockModelBuilder ret = provider.models().cube("crystallizer", DEFAULT_BOTTOM, DEFAULT_TOP, new ResourceLocation(DeepResonance.MODID, "block/crystallizer"), DEFAULT_SIDE, DEFAULT_SIDE, DEFAULT_SIDE);
        ModelBuilder<BlockModelBuilder>.ElementBuilder elem = ret.element();
        for (Direction direction : Direction.values()) {
            elem = elem.face(direction).cullface(direction).texture("#" + direction.getName()).end();
        }
        ret = elem.end();
        ret = ret.element().from(0, 0, 0).to(16, 7, 16).face(Direction.UP).texture("#" + Direction.UP.getName()).end().end();
        elem = ret.element().from(16, 16, 16).to(0, 0, 0);
        for (Direction direction : Direction.values()) {
            elem = elem.face(direction.getOpposite()).cullface(Direction.NORTH).texture("#" + Direction.UP.getName()).end();
        }
        return elem.end()
                .texture("particle", "deepresonance:block/crystallizer")
                .renderType("translucent");
    }

    public static BlockModelBuilder createLaserModel(BaseBlockStateProvider provider) {
        ResourceLocation laserTxt = new ResourceLocation(DeepResonance.MODID, "block/laser");
        ResourceLocation laserBackTxt = new ResourceLocation(DeepResonance.MODID, "block/laser_back");
        return provider.models().cube("laser", DEFAULT_BOTTOM, DEFAULT_TOP, laserTxt, laserBackTxt, laserBackTxt, laserBackTxt)
                .texture("particle", laserTxt);
    }

}
