package mcjty.deepresonance.modules.tank;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.tank.blocks.TankBlock;
import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.deepresonance.modules.tank.client.TankTESR;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static mcjty.deepresonance.DeepResonance.tab;
import static mcjty.deepresonance.setup.Registration.TILES;

public class TankModule implements IModule {

    public static final DeferredBlock<Block> TANK_BLOCK = Registration.BLOCKS.register("tank", TankBlock::new);
    public static final DeferredItem<Item> TANK_ITEM = Registration.ITEMS.register("tank", tab(() -> new BlockItem(TANK_BLOCK.get(),
            Registration.createStandardProperties()
    )));
    public static final Supplier<BlockEntityType<TankTileEntity>> TYPE_TANK = TILES.register("tank", () -> BlockEntityType.Builder.of(TankTileEntity::new, TANK_BLOCK.get()).build(null));

    public TankModule() {
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        TankTESR.register();
    }

    @Override
    public void initConfig() {
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(TANK_BLOCK)
                        .blockState(provider -> {
                            provider.simpleBlock(TANK_BLOCK.get(),
                                    provider.models().cubeBottomTop("tank", TankTESR.TANK_SIDE, TankTESR.TANK_BOTTOM, TankTESR.TANK_TOP).renderType("translucent"));
                        })
                        .ironPickaxeTags()
                        .standardLoot(TYPE_TANK)
                        .parentedItem("block/tank")
                        .shaped(builder -> builder
                                        .define('P', CoreModule.RESONATING_PLATE_ITEM.get())
                                        .unlockedBy("has_resonant_plate", DataGen.has(CoreModule.RESONATING_PLATE_ITEM.get())),
                                "iPi", "GGG", "iOi")
        );
    }
}
