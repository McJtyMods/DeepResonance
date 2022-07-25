package mcjty.deepresonance.modules.tank;

import mcjty.deepresonance.modules.tank.blocks.TankBlock;
import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.deepresonance.modules.tank.client.TankTESR;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.modules.IModule;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import static mcjty.deepresonance.setup.Registration.TILES;

public class TankModule implements IModule {

    public static final RegistryObject<Block> TANK_BLOCK = Registration.BLOCKS.register("tank", TankBlock::new);
    public static final RegistryObject<Item> TANK_ITEM = Registration.ITEMS.register("tank", () -> new BlockItem(TANK_BLOCK.get(),
            Registration.createStandardProperties()
//                    .setISTER(TankItemRenderer::getRenderer)));   // @todo 1.18
    ));
    public static final RegistryObject<BlockEntityType<TankTileEntity>> TYPE_TANK = TILES.register("tank", () -> BlockEntityType.Builder.of(TankTileEntity::new, TANK_BLOCK.get()).build(null));

    public TankModule() {
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        TankTESR.register();
    }

    @Override
    public void initConfig() {
    }
}
