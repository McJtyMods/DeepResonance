package mcjty.deepresonance.modules.tank;

import com.google.common.base.Preconditions;
import elec332.core.api.module.ElecModule;
import elec332.core.handler.ElecCoreRegistrar;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.tank.blocks.BlockTank;
import mcjty.deepresonance.modules.tank.grid.TankGridHandler;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.deepresonance.DeepResonance.MODID;

/**
 * Created by Elec332 on 8-1-2020
 */
@ElecModule(owner = MODID, name = "tanks")
public class TankModule {

    public static final RegistryObject<Block> TANK_BLOCK = DeepResonance.BLOCKS.register("tank", BlockTank::new);
    public static final RegistryObject<Item> TANK_ITEM = DeepResonance.ITEMS.register("tank", () -> new BlockItem(Preconditions.checkNotNull(TANK_BLOCK.get()), DeepResonance.createStandardProperties()));

    @ElecModule.EventHandler
    public void setup(FMLCommonSetupEvent event) {
        DeepResonance.logger.info("Registering tank grid handler");
        ElecCoreRegistrar.GRIDHANDLERS.register(new TankGridHandler());
    }

}
