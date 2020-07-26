package mcjty.deepresonance.modules.tank;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.matrix.MatrixStack;
import elec332.core.api.client.model.ModelLoadEvent;
import elec332.core.api.config.IConfigurableElement;
import elec332.core.api.module.ElecModule;
import elec332.core.handler.ElecCoreRegistrar;
import elec332.core.item.AbstractItemBlock;
import elec332.core.loader.client.RenderingRegistry;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.tank.blocks.BlockTank;
import mcjty.deepresonance.modules.tank.client.TankRenderer;
import mcjty.deepresonance.modules.tank.client.TankTESR;
import mcjty.deepresonance.modules.tank.grid.TankGrid;
import mcjty.deepresonance.modules.tank.grid.TankGridHandler;
import mcjty.deepresonance.util.DeepResonanceResourceLocation;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nonnull;

import static mcjty.deepresonance.DeepResonance.MODID;

/**
 * Created by Elec332 on 8-1-2020
 */
@ElecModule(owner = MODID, name = "Tanks")
public class TankModule implements IConfigurableElement {

    public static final RegistryObject<Block> TANK_BLOCK = DeepResonance.BLOCKS.register("tank", BlockTank::new);
    public static final RegistryObject<Item> TANK_ITEM = DeepResonance.ITEMS.register("tank", () -> new BlockItem(Preconditions.checkNotNull(TANK_BLOCK.get()), DeepResonance.createStandardProperties()));

    public static ForgeConfigSpec.BooleanValue quickRender;

    public TankModule() {
        DeepResonance.config.registerConfigurableElement(this);
        DeepResonance.clientConfig.registerConfigurableElement(this);
    }

    @ElecModule.EventHandler
    public void setup(FMLCommonSetupEvent event) {
        DeepResonance.logger.info("Registering tank grid handler");
        ElecCoreRegistrar.GRIDHANDLERS.register(new TankGridHandler());
    }

    @OnlyIn(Dist.CLIENT)
    @ElecModule.EventHandler
    public void setupClient(FMLClientSetupEvent event) {
        RenderingRegistry.instance().registerLoader(TankRenderer.INSTANCE);
        final TankTESR tesr = new TankTESR();
        RenderingRegistry.instance().setItemRenderer(TANK_ITEM.get(), new ItemStackTileEntityRenderer() {

            @Override
            public void render(@Nonnull ItemStack itemStackIn, @Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
                FluidStack stack = BlockTank.readFromTileNbt(AbstractItemBlock.getTileData(itemStackIn));
                if (!stack.isEmpty()) {
                    tesr.render(matrixStackIn, bufferIn, stack.getFluid(), (stack.getAmount() / (float) TankGrid.TANK_BUCKETS), combinedLightIn);
                }
            }

        });
    }

    @OnlyIn(Dist.CLIENT)
    @ElecModule.EventHandler
    public void loadModels(ModelLoadEvent event) {
        ModelResourceLocation location = new ModelResourceLocation(new DeepResonanceResourceLocation("tank"), "");
        event.registerModel(location, TankRenderer.INSTANCE.setModel(event.getModel(location)));
    }

    @Override
    public void registerProperties(@Nonnull ForgeConfigSpec.Builder config, ModConfig.Type type) {
        config.comment("Tank settings").push("tanks");

        if (type == ModConfig.Type.CLIENT) {
            quickRender = config.comment("Whether to use the fast renderer or the fancy one when rendering the inside of tanks.").define("fastRenderer", false);
        }

        config.pop();
    }

}
