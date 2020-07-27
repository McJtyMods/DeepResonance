package mcjty.deepresonance.data;

import com.google.common.base.Preconditions;
import elec332.core.data.AbstractItemModelProvider;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.util.DeepResonanceResourceLocation;
import net.minecraft.block.Block;
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
class ItemModelProvider extends AbstractItemModelProvider {

    ItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, DeepResonance.MODID, existingFileHelper);
    }

    @Override
    protected void registerItemModels() {

        //Items
        simpleModel(CoreModule.FILTER_MATERIAL_ITEM);
        simpleModel(CoreModule.LIQUID_INJECTOR_ITEM);
        simpleModel(CoreModule.RESONATING_PLATE_ITEM);
        simpleModel(CoreModule.SPENT_FILTER_ITEM);
        simpleModel(RadiationModule.RADIATION_SUIT_HELMET);
        simpleModel(RadiationModule.RADIATION_SUIT_CHESTPLATE);
        simpleModel(RadiationModule.RADIATION_SUIT_LEGGINGS);
        simpleModel(RadiationModule.RADIATION_SUIT_BOOTS);

        cubeAll(CoreModule.MACHINE_FRAME_ITEM, "block/machine_side");

        //Blocks
        parentedModel(TankModule.TANK_ITEM, "builtin/entity");
        parentedModel(CoreModule.RESONATING_ORE_STONE_BLOCK);
        parentedModel(CoreModule.RESONATING_ORE_NETHER_BLOCK);
        parentedModel(CoreModule.RESONATING_ORE_END_BLOCK);
        parentedModel(RadiationModule.POISONED_DIRT_BLOCK);
        parentedModel(RadiationModule.DENSE_GLASS_BLOCK);
        parentedModel(RadiationModule.DENSE_OBSIDIAN_BLOCK);
        parentedModel(CoreModule.RESONATING_PLATE_BLOCK_BLOCK);
        parentedModel(MachinesModule.VALVE_BLOCK);
        parentedModel(MachinesModule.SMELTER_ITEM, "block/smelter_inactive");
        parentedModel(MachinesModule.PURIFIER_BLOCK);
        parentedModel(MachinesModule.PULSER_BLOCK);
    }

    private void cubeAll(Supplier<Item> item, String texture) {
        cubeAll(Preconditions.checkNotNull(item.get().getRegistryName()).getPath(), new DeepResonanceResourceLocation(texture));
    }

    private void simpleModel(Supplier<Item> item) {
        simpleModel(item, "item/" + Preconditions.checkNotNull(item.get().getRegistryName()).getPath());
    }

    private void simpleModel(Supplier<Item> item, String... textures) {
        ItemModelBuilder imb = this.getBuilder(Preconditions.checkNotNull(item.get().getRegistryName()).getPath()).parent(this.getExistingFile(this.mcLoc("item/generated")));
        for (int i = 0; i < textures.length; i++) {
            imb.texture("layer" + i, textures[i]);
        }
    }

    private ItemModelBuilder parentedModel(Supplier<Block> block) {
        return parentedModel(() -> block.get().asItem(), "block/" + Preconditions.checkNotNull(block.get().getRegistryName()).getPath());
    }

    private ItemModelBuilder parentedModel(Supplier<Item> item, Supplier<Block> block) {
        return parentedModel(item, "block/" + Preconditions.checkNotNull(block.get().getRegistryName()).getPath());
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
