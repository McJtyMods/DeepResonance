package mcjty.deepresonance.data;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.client.TankRenderer;
import mcjty.deepresonance.util.DeepResonanceResourceLocation;
import mcjty.lib.datagen.BaseBlockStateProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

/**
 * Created by Elec332 on 10-1-2020
 */
public class BlockStateProvider extends BaseBlockStateProvider {

    BlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, DeepResonance.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(TankModule.TANK_BLOCK.get(), cubeBottomTop("tank", TankRenderer.SIDE_TEXTURE, TankRenderer.BOTTOM_TEXTURE, TankRenderer.TOP_TEXTURE));
        simpleBlock(CoreModule.MACHINE_FRAME_BLOCK.get(), cubeAll("machine_frame", new DeepResonanceResourceLocation("blocks/machine_side")));
    }

}
