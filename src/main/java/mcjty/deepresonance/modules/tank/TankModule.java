package mcjty.deepresonance.modules.tank;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.tank.blocks.TankBlock;
import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.deepresonance.modules.tank.client.TankTESR;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public class TankModule implements IModule {

    public static final RBlock<TankBlock, BlockItem, TankTileEntity> TANK = Registration.registerBlockWithTile(
            "tank",
            TankTileEntity.class,
            TankBlock::new,
            block -> new BlockItem(block.get(), Registration.createStandardProperties()),
            TankTileEntity::new
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TankTileEntity>> TYPE_TANK = TANK.be();

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
    public void initConfig(IEventBus bus) {
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider lookupProvider) {
        dataGen.add(
                Dob.blockBuilder(TANK)
                        .blockState(provider -> {
                            provider.simpleBlock(TANK.block().get(),
                                    provider.models().cubeBottomTop("tank", TankTESR.TANK_SIDE, TankTESR.TANK_BOTTOM, TankTESR.TANK_TOP).renderType("translucent"));
                        })
                        .ironPickaxeTags()
//                        .standardLoot(TYPE_TANK)  // @todo 1.21
                        .parentedItem("block/tank")
                        .shaped(builder -> builder
                                        .define('P', CoreModule.RESONATING_PLATE_ITEM.get())
                                        .unlockedBy("has_resonant_plate", DataGen.has(CoreModule.RESONATING_PLATE_ITEM.get())),
                                "iPi", "GGG", "iOi")
        );
    }
}
