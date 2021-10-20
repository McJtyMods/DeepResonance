package mcjty.deepresonance.compat;

import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.block.GeneratorControllerTileEntity;
import mcjty.deepresonance.modules.generator.block.GeneratorPartTileEntity;
import mcjty.lib.compat.theoneprobe.McJtyLibTOPDriver;
import mcjty.lib.compat.theoneprobe.TOPDriver;
import mcjty.lib.varia.Tools;
import mcjty.theoneprobe.api.CompoundText;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class DeepResonanceTOPDriver implements TOPDriver {

    public static final DeepResonanceTOPDriver DRIVER = new DeepResonanceTOPDriver();

    private final Map<ResourceLocation, TOPDriver> drivers = new HashMap<>();

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
        ResourceLocation id = blockState.getBlock().getRegistryName();
        if (!drivers.containsKey(id)) {
            if (blockState.getBlock() == GeneratorModule.GENERATOR_PART_BLOCK.get()) {
                drivers.put(id, new GeneratorPartDriver());
            } else if (blockState.getBlock() == GeneratorModule.GENERATOR_CONTROLLER_BLOCK.get()) {
                drivers.put(id, new GeneratorControllerDriver());
            } else {
                drivers.put(id, new DefaultDriver());
            }
        }
        TOPDriver driver = drivers.get(id);
        if (driver != null) {
            driver.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        }
    }

    private static class DefaultDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
        }
    }

    private static class GeneratorPartDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (GeneratorPartTileEntity te) -> {
                int id = te.getMultiblockId();
                probeInfo.text(CompoundText.createLabelInfo("Id ", id));
            }, "Bad tile entity!");
        }
    }

    private static class GeneratorControllerDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (GeneratorControllerTileEntity te) -> {
                int level = te.getPowerLevel();
                probeInfo.text(CompoundText.createLabelInfo("Power ", level));
            }, "Bad tile entity!");
        }
    }
}
