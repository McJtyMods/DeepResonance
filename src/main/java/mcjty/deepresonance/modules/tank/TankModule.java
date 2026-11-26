package mcjty.deepresonance.modules.tank;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.tank.blocks.TankBlock;
import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.deepresonance.modules.tank.client.TankTESR;
import mcjty.deepresonance.modules.tank.data.TankData;
import mcjty.deepresonance.setup.Registration;
import mcjty.deepresonance.DeepResonance;
import net.minecraft.resources.Identifier;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import mcjty.lib.setup.DeferredHolder;
import net.minecraft.tags.BlockTags;
import java.util.List;

public class TankModule implements IModule {

    public static final Identifier TANK_BOTTOM = Identifier.fromNamespaceAndPath(DeepResonance.MODID, "block/tank_bottom");
    public static final Identifier TANK_TOP = Identifier.fromNamespaceAndPath(DeepResonance.MODID, "block/tank_top");
    public static final Identifier TANK_SIDE = Identifier.fromNamespaceAndPath(DeepResonance.MODID, "block/tank_side");

    public static final RBlock<TankBlock, BlockItem, TankTileEntity> TANK = Registration.registerBlockWithTile(
            "tank",
            TankTileEntity.class,
            TankBlock::new,
            (block, properties) -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()),
            TankTileEntity::new
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TankTileEntity>> TYPE_TANK = TANK.be();

    // No attachment type needed
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TankData>> ITEM_TANK_DATA = Registration.COMPONENTS.registerComponentType(
            "tank",
            builder -> builder
                    .persistent(TankData.CODEC)
                    .networkSynchronized(TankData.STREAM_CODEC));

    public TankModule() {
    }

    @Override
    public void init() {
    }

    @Override
    public void initClient() {
        TankTESR.register();
    }

    @Override
    public void initConfig() {
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider lookupProvider) {
        dataGen.add(
                Dob.blockBuilder(TANK)
                        .blockTags(List.of(BlockTags.BLOCKS_MOTION_NO_LEAVES))
                        .blockState(provider -> {
                            provider.simpleBlock(TANK.block().get(),
                                    provider.models().cubeBottomTop("tank", TANK_SIDE, TANK_BOTTOM, TANK_TOP));
                        })
                        .ironPickaxeTags()
                        .standardLoot(ITEM_TANK_DATA.get())
                        .parentedItem("block/tank")
                        .shaped(builder -> builder
                                        .define('P', CoreModule.RESONATING_PLATE_ITEM.get())
                                        .unlockedBy("has_resonant_plate", DataGen.has(CoreModule.RESONATING_PLATE_ITEM.get())),
                                "iPi", "GGG", "iOi")
        );
    }
}
