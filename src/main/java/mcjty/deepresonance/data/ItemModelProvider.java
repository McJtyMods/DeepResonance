package mcjty.deepresonance.data;

import com.google.common.base.Preconditions;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.lib.datagen.BaseItemModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 10-1-2020
 */
class ItemModelProvider extends BaseItemModelProvider {

    public ItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, DeepResonance.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        //Items
        simpleModel(CoreModule.FILTER_MATERIAL_ITEM, "items/filtermaterial");
        simpleModel(CoreModule.LIQUID_INJECTOR_ITEM, "items/liquidinjector");
        simpleModel(CoreModule.RESONATING_PLATE_ITEM, "items/resonatingplate");
        simpleModel(CoreModule.SPENT_FILTER_ITEM, "items/spentfiltermaterial");

        //Blocks
        parentedModel(TankModule.TANK_ITEM, "block/tank");
        parentedModel(CoreModule.MACHINE_FRAME_ITEM, "block/machine_frame");
    }

    private void simpleModel(Supplier<Item> item, String... textures) {
        ItemModelBuilder imb = this.getBuilder(Preconditions.checkNotNull(item.get().getRegistryName()).getPath()).parent(this.getExistingFile(this.mcLoc("item/generated")));
        for (int i = 0; i < textures.length; i++) {
            imb.texture("layer" + i, textures[i]);
        }
    }

    private ItemModelBuilder parentedModel(Supplier<Item> item, String parent) {
        return this.getBuilder(Preconditions.checkNotNull(item.get().getRegistryName()).getPath()).parent(new ModelFile.UncheckedModelFile(this.modLoc(parent)));
    }

    @Nonnull
    @Override
    public String getName() {
        return "Item States";
    }

}
