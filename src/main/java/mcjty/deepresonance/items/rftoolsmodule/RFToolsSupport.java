package mcjty.deepresonance.items.rftoolsmodule;

import com.google.common.base.Function;
import io.netty.buffer.ByteBuf;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.radiationsensor.RadiationSensorBlock;
import mcjty.deepresonance.items.ModItems;
import mcjty.rftools.api.screens.IScreenModuleRegistry;
import mcjty.rftools.api.screens.data.IModuleData;
import mcjty.rftools.api.screens.data.IModuleDataFactory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class RFToolsSupport {

    public static RCLModuleItem rclModuleItem;
    public static RadiationModuleItem radiationModuleItem;
    public static RadiationSensorBlock radiationSensorBlock;

    public static void initItems() {
        radiationModuleItem = new RadiationModuleItem();
        rclModuleItem = new RCLModuleItem();
    }

    @SideOnly(Side.CLIENT)
    public static void initItemModels() {
        radiationModuleItem.initModel();
        rclModuleItem.initModel();
    }

    public static void initBlocks() {
        radiationSensorBlock = new RadiationSensorBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void initBlockModels() {
        radiationSensorBlock.initModel();
    }

    public static void initCrafting() {
        GameRegistry.addRecipe(new ItemStack(RFToolsSupport.radiationSensorBlock), "qcq", "tot", "qrq", 'r', Items.redstone, 'q', Items.quartz, 'o', ModBlocks.machineFrame,
                               'c', Items.clock, 't', Items.compass);
        ItemStack inkSac = new ItemStack(Items.dye, 1, 0);
        GameRegistry.addRecipe(new ItemStack(RFToolsSupport.radiationModuleItem), " c ", "rir", " b ", 'c', ModItems.radiationMonitorItem, 'r', ModItems.resonatingPlateItem, 'i', Items.iron_ingot,
                               'b', inkSac);
        GameRegistry.addRecipe(new ItemStack(RFToolsSupport.rclModuleItem), " c ", "rir", " b ", 'c', Items.comparator, 'r', ModItems.resonatingPlateItem, 'i', Items.iron_ingot,
                               'b', inkSac);
    }

    public static class GetScreenModuleRegistry implements Function<IScreenModuleRegistry, Void> {
        @Nullable
        @Override
        public Void apply(IScreenModuleRegistry manager) {
            manager.registerModuleDataFactory(ModuleDataRCL.ID, new IModuleDataFactory() {
                @Override
                public IModuleData createData(ByteBuf buf) {
                    int purity = buf.readByte();
                    int strength = buf.readByte();
                    int efficiency = buf.readByte();
                    return new ModuleDataRCL(purity, strength, efficiency);
                }
            });
            return null;
        }
    }

}
