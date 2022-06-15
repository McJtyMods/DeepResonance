package mcjty.deepresonance.modules.machines.block;

import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class LaserContainer extends GenericContainer {

    public LaserContainer(@NotNull Supplier<MenuType<GenericContainer>> type, int id, @NotNull Supplier<ContainerFactory> factory, @Nullable GenericTileEntity te, @NotNull Player player) {
        super(type, id, factory, te, player);
    }
}
