package mcjty.deepresonance.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import javax.annotation.Nullable;

public final class ItemDataHelper {

    private ItemDataHelper() {
    }

    public static CompoundTag getOrCreateInfo(CompoundTag tag) {
        if (!tag.contains("Info", Tag.TAG_COMPOUND)) {
            tag.put("Info", new CompoundTag());
        }
        return tag.getCompound("Info");
    }

    @Nullable
    public static CompoundTag getBlockEntityData(ItemStack stack) {
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data != null) {
            return data.copyTag();
        }
        return null;
    }

    @Nullable
    public static CompoundTag getInfoTag(ItemStack stack) {
        CompoundTag tag = getBlockEntityData(stack);
        if (tag != null && tag.contains("Info", Tag.TAG_COMPOUND)) {
            return tag.getCompound("Info");
        }
        return null;
    }

    public static int getInfoInt(ItemStack stack, String key, int defaultValue) {
        CompoundTag info = getInfoTag(stack);
        if (info != null && info.contains(key, Tag.TAG_INT)) {
            return info.getInt(key);
        }
        return defaultValue;
    }
}
