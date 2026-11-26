package mcjty.deepresonance.util;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.worlddata.AbstractWorldData;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ResonanceSavedData {

    private ResonanceSavedData() {
    }

    public static <T extends AbstractWorldData<T>> T get(Level level, String path, String legacyName,
            Function<CompoundTag, T> loader, Supplier<T> factory) {
        var id = Identifier.fromNamespaceAndPath(DeepResonance.MODID, path);
        return AbstractWorldData.getData(level, loader, () -> {
            var overworld = LevelTools.getOverworld(level);
            var legacy = overworld.getServer().getWorldPath(LevelResource.ROOT).resolve("data").resolve(legacyName + ".dat");
            if (Files.notExists(legacy)) {
                return factory.get();
            }
            try {
                var root = overworld.getDataStorage().readTagFromDisk(legacy, null, SharedConstants.getCurrentVersion().dataVersion().version());
                T migrated = loader.apply(root.getCompound("data").orElseThrow(() -> new IOException("Missing saved data in " + legacy)));
                migrated.setDirty();
                return migrated;
            } catch (IOException exception) {
                throw new UncheckedIOException("Cannot migrate Deep Resonance data from " + legacy, exception);
            }
        }, id.toString());
    }
}
