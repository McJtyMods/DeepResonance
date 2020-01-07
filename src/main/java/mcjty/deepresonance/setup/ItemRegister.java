package mcjty.deepresonance.setup;

import elec332.core.api.registration.IItemRegister;
import mcjty.deepresonance.DeepResonance;
import mcjty.lib.blocks.BaseBlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Created by Elec332 on 6-1-2020
 */
public class ItemRegister implements IItemRegister {

    public static Item tankItem;

    @Override
    public void preRegister() {
        tankItem = new BaseBlockItem(BlockRegister.tank, DeepResonance.createStandardProperties());
    }

    @Override
    public void register(IForgeRegistry<Item> registry) {
        registry.register(tankItem);
    }

}
