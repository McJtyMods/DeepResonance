package mcjty.deepresonance.setup;

import com.google.common.base.Preconditions;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.util.TranslationHelper;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class Registration {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DeepResonance.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DeepResonance.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, DeepResonance.MODID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, DeepResonance.MODID);
    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, DeepResonance.MODID);

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        FLUIDS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static Item.Properties createStandardProperties() {
        return new Item.Properties().tab(DeepResonance.setup.getTab());
    }

    public static RegistryObject<Block> defaultBlock(final String name, final Supplier<TileEntity> tile) {
        return defaultBlock(name, tile, null);
    }

    public static RegistryObject<Block> defaultBlock(final String name, final Supplier<TileEntity> tile, UnaryOperator<BlockState> mod, Property<?>... props) {
        return block(name, tile, builder -> new BaseBlock(builder) {

            @Override
            public RotationType getRotationType() {
                return RotationType.HORIZROTATION;
            }

            @Override
            protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
                super.createBlockStateDefinition(builder);
                builder.add(props);
            }

        });
    }

    public static RegistryObject<Block> nonRotatingBlock(final String name, final Supplier<TileEntity> tile) {
        return nonRotatingBlock(name, tile, null);
    }

    public static RegistryObject<Block> nonRotatingBlock(final String name, final Supplier<TileEntity> tile, UnaryOperator<BlockState> mod, Property<?>... props) {
        return block(name, tile, builder -> new BaseBlock(builder) {

            @Override
            public RotationType getRotationType() {
                return RotationType.NONE;
            }

            @Override
            protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
                super.createBlockStateDefinition(builder);
                builder.add(props);
            }

        });
    }

    public static RegistryObject<Block> block(final String name, final Supplier<TileEntity> tile, final Function<BlockBuilder, BaseBlock> constructor) {
        return BLOCKS.register(name, () -> constructor.apply(new BlockBuilder().tileEntitySupplier(tile).infoShift(TooltipBuilder.key(TranslationHelper.getTooltipKey(name)))));
    }

    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        Preconditions.checkNotNull(block.getId().getPath());
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(Preconditions.checkNotNull(block.get()), createStandardProperties()));
    }
}
