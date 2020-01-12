package mcjty.deepresonance.util;

import com.google.common.base.Preconditions;
import mcjty.deepresonance.DeepResonance;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Supplier;

/**
 * Created by Elec332 on 10-1-2020
 */
@SuppressWarnings("WeakerAccess")
public class TranslationHelper {

    public static final String TOOLTIP_PREFIX = "tooltip";
    public static final String FLUID_PREFIX = "fluid";
    public static final String MESSAGE_PREFIX = "message";

    public static String getExtendedTooltipKey(String name) {
        return makeTranslationKey(makeDeepResonancePrefix(TOOLTIP_PREFIX), name, "extended");
    }

    public static <T extends IForgeRegistryEntry<?>> String getTooltipKey(Supplier<T> supplier) {
        return getTooltipKey(supplier.get());
    }

    public static String getTooltipKey(IForgeRegistryEntry<?> object) {
        return getTooltipKey(Preconditions.checkNotNull(object.getRegistryName()).getPath());
    }

    public static String getTooltipKey(String name) {
        return makeSimpleTranslationKey(TOOLTIP_PREFIX, name);
    }

    public static String getMessageKey(String name) {
        return makeSimpleTranslationKey(MESSAGE_PREFIX, name);
    }

    public static String getFluidKey(String name) {
        return makeSimpleTranslationKey(FLUID_PREFIX, name);
    }

    public static String makeSimpleTranslationKey(String prefix, String name) {
        return makeTranslationKey(makeDeepResonancePrefix(prefix), name);
    }

    public static String makeDeepResonancePrefix(String prefix) {
        return makeTranslationKey(prefix, DeepResonance.MODID);
    }

    public static String makeTranslationKey(String... parts) {
        if (parts == null || parts.length < 1) {
            throw new IllegalArgumentException();
        }
        StringBuilder ret = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            ret.append(".");
            ret.append(parts[i]);
        }
        return ret.toString();
    }

}
