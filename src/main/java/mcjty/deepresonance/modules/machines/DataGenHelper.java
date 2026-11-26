package mcjty.deepresonance.modules.machines;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.datagen.BaseBlockStateProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import mcjty.lib.datagen.model.BlockModelBuilder;

import static mcjty.deepresonance.datagen.BlockStates.*;

public class DataGenHelper {

    public static BlockModelBuilder createCrystallizerModel(BaseBlockStateProvider provider) {
        BlockModelBuilder ret = provider.models().cube("crystallizer", DEFAULT_BOTTOM, DEFAULT_TOP, Identifier.fromNamespaceAndPath(DeepResonance.MODID, "block/crystallizer"), DEFAULT_SIDE, DEFAULT_SIDE, DEFAULT_SIDE);
        BlockModelBuilder.ElementBuilder elem = ret.element().from(0, 0, 0).to(16, 16, 16);
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
                .texture("particle", "deepresonance:block/crystallizer");
    }

    public static BlockModelBuilder createLaserModel(BaseBlockStateProvider provider) {
        Identifier laserTxt = Identifier.fromNamespaceAndPath(DeepResonance.MODID, "block/laser");
        Identifier laserBackTxt = Identifier.fromNamespaceAndPath(DeepResonance.MODID, "block/laser_back");
        return provider.models().cube("laser", DEFAULT_BOTTOM, DEFAULT_TOP, laserTxt, laserBackTxt, laserBackTxt, laserBackTxt)
                .texture("particle", laserTxt);
    }

}
