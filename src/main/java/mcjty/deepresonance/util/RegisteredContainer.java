package mcjty.deepresonance.util;

import com.google.common.base.Preconditions;
import elec332.core.util.FMLHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 25-7-2020
 */
public abstract class RegisteredContainer<G extends GenericGuiContainer<T, GenericContainer>, T extends GenericTileEntity> {

    public static <G extends GenericGuiContainer<T, GenericContainer>, T extends GenericTileEntity> RegisteredContainer<G, T> create(String name, int slots, Consumer<ContainerFactory> containerBuilder, IGuiFactory<T> gui) {
        return new RegisteredContainer<G, T>(name, slots, containerBuilder) {

            @Override
            @OnlyIn(Dist.CLIENT)
            public Object createGui(T tile, GenericContainer container, PlayerInventory inventory) {
                return gui.createGui(tile, container, inventory);
            }

        };
    }

    @SuppressWarnings({"unchecked", "RedundantTypeArguments"})
    public RegisteredContainer(String name, int slots, Consumer<ContainerFactory> containerBuilder) {
        this.name = Preconditions.checkNotNull(name);
        this.factory = Lazy.of(() -> {
            ContainerFactory f = new ContainerFactory(slots);
            containerBuilder.accept(f);
            if (FMLHelper.getDist().isClient()) {
                //Type args needed for compiler
                GenericGuiContainer.<GenericContainer, G, T>register(RegisteredContainer.this.type.get(), (genericTileEntity, genericContainer, playerInventory) -> (G) createGui(genericTileEntity, genericContainer, playerInventory));
            }
            return f;
        });
        this.type = DeepResonance.CONTAINERS.register(this.name, GenericContainer::createContainerType);
        this.modifier = (c, t) -> {
        };
        this.locked = true;
    }

    private final String name;
    private final RegistryObject<ContainerType<GenericContainer>> type;
    private final Lazy<ContainerFactory> factory;
    private BiConsumer<DefaultContainerProvider<GenericContainer>, T> modifier;
    private boolean locked;

    public RegisteredContainer<G, T> modifyContainer(BiConsumer<DefaultContainerProvider<GenericContainer>, T> modifier) {
        if (locked) {
            throw new IllegalStateException();
        }
        this.modifier = this.modifier.andThen(modifier);
        return this;
    }

    public LazyOptional<INamedContainerProvider> build(T tile) {
        if (!locked) {
            locked = true;
        }
        return LazyOptional.of(() -> {
            DefaultContainerProvider<GenericContainer> provider = new DefaultContainerProvider<>(name);
            provider.containerSupplier((id, player) -> new GenericContainer(type.get(), id, factory.get(), tile.getPos(), tile));
            modifier.accept(provider, tile);
            return provider;
        });
    }

    @OnlyIn(Dist.CLIENT)
    public abstract Object createGui(T tile, GenericContainer container, PlayerInventory inventory);

    public interface IGuiFactory<T extends GenericTileEntity> {

        @OnlyIn(Dist.CLIENT)
        Object createGui(T tile, GenericContainer container, PlayerInventory inventory);

    }

}
