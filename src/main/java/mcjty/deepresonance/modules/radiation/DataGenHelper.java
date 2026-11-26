package mcjty.deepresonance.modules.radiation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mcjty.lib.datagen.BaseItemModelProvider;
import mcjty.lib.datagen.model.ModelProvider;
import net.minecraft.resources.Identifier;

import static mcjty.deepresonance.DeepResonance.MODID;
import static mcjty.deepresonance.modules.radiation.item.RadiationMonitorItem.RADIATION_PROPERTY;

public final class DataGenHelper {

    private DataGenHelper() {
    }

    public static void generateMonitor(BaseItemModelProvider provider) {
        for (int level = 0; level < 10; level++) {
            provider.getBuilder("radiationmonitoritem" + level)
                    .parent(provider.getExistingFile(provider.mcLoc("item/handheld")))
                    .texture("layer0", "item/monitor/radiationmonitoritem" + level);
        }
        provider.itemHandheld(RadiationModule.RADIATION_MONITOR.get(), "item/monitor/radiationmonitoritem0");
    }

    public static JsonObject monitorDefinition() {
        JsonObject range = new JsonObject();
        range.addProperty("type", "minecraft:range_dispatch");
        range.addProperty("property", RADIATION_PROPERTY.toString());
        JsonArray entries = new JsonArray();
        for (int level = 1; level < 10; level++) {
            JsonObject entry = new JsonObject();
            entry.addProperty("threshold", level);
            entry.add("model", model(level));
            entries.add(entry);
        }
        range.add("entries", entries);
        range.add("fallback", model(0));
        JsonObject root = new JsonObject();
        root.add("model", range);
        return root;
    }

    private static JsonObject model(int level) {
        return ModelProvider.itemDefinition(Identifier.fromNamespaceAndPath(MODID, "item/radiationmonitoritem" + level))
                .getAsJsonObject("model");
    }
}
