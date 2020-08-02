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
public abstract class RegisteredContainer<C extends GenericContainer, G extends GenericGuiContainer<T, C>, T extends GenericTileEntity> {

    public static <C extends GenericContainer, G extends GenericGuiContainer<T, C>, T extends GenericTileEntity> RegisteredContainer<C, G, T> create(String name, int slots, Consumer<ContainerFactory> containerBuilder, IGuiFactory<C, T> gui) {
        return new RegisteredContainer<C, G, T>(name, slots, containerBuilder) {

            @Override
            @OnlyIn(Dist.CLIENT)
            @SuppressWarnings("unchecked")
            public Object createGui(T tile, GenericContainer container, PlayerInventory inventory) {
                return gui.createGui(tile, (C) container, inventory);
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
                GenericGuiContainer.<C, G, T>register(RegisteredContainer.this.type.get(), (genericTileEntity, genericContainer, playerInventory) -> (G) createGui(genericTileEntity, genericContainer, playerInventory));
            }
            return f;
        });
        this.type = DeepResonance.CONTAINERS.register(this.name, GenericContainer::createContainerType);
        this.modifier = (c, t) -> {
            if (t instanceof Modifier) {
                ((Modifier) t).modify(c);
            }
        };
        this.locked = false;
    }

    private final String name;
    private final RegistryObject<ContainerType<C>> type;
    private final Lazy<ContainerFactory> factory;
    private BiConsumer<DefaultContainerProvider<GenericContainer>, T> modifier;
    private boolean locked;

    public RegisteredContainer<C, G, T> modifyContainer(BiConsumer<DefaultContainerProvider<GenericContainer>, T> modifier) {
        if (locked) {
            throw new IllegalStateException();
        }
        this.modifier = this.modifier.andThen(modifier);
        return this;
    }

    public RegisteredContainer<C, G, T> modifyContainer(Modifier modifier) {
        return modifyContainer((p, t) -> modifier.modify(p));
    }

    public LazyOptional<INamedContainerProvider> build(T tile) {
        if (!locked) {
            locked = true;
        }
        return LazyOptional.of(() -> {
            DefaultContainerProvider<GenericContainer> provider = new DefaultContainerProvider<>(name);
            provider.containerSupplier((id, player) -> new DeepResonanceContainer(type.get(), id, factory.get(), tile.getPos(), tile));
            modifier.accept(provider, tile);
            return provider;
        });
    }

    @OnlyIn(Dist.CLIENT)
    public abstract Object createGui(T tile, GenericContainer container, PlayerInventory inventory);

    public interface Modifier {

        void modify(DefaultContainerProvider<GenericContainer> container);

    }

    public interface IGuiFactory<C extends GenericContainer, T extends GenericTileEntity> {

        @OnlyIn(Dist.CLIENT)
        Object createGui(T tile, C container, PlayerInventory inventory);

    }

}
