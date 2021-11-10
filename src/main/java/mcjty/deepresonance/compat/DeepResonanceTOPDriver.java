package mcjty.deepresonance.compat;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.block.GeneratorControllerTileEntity;
import mcjty.deepresonance.modules.generator.block.GeneratorPartTileEntity;
import mcjty.deepresonance.modules.generator.data.GeneratorBlob;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.deepresonance.modules.tank.data.TankBlob;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.math.RoundingMode;
import java.text.DecimalFormat;
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
            } else if (blockState.getBlock() == CoreModule.RESONATING_CRYSTAL_BLOCK.get()) {
                drivers.put(id, new CrystalDriver());
            } else if (blockState.getBlock() == TankModule.TANK_BLOCK.get()) {
                drivers.put(id, new TankDriver());
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
                GeneratorBlob network = te.getBlob();
                probeInfo.text(CompoundText.createLabelInfo("Collectors ", network.getCollectorBlocks()));
                probeInfo.text(CompoundText.createLabelInfo("Generators ", network.getGeneratorBlocks()));
                probeInfo.text(CompoundText.createLabelInfo("Energy ", network.getEnergy()));
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

    private static class CrystalDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (ResonatingCrystalTileEntity crystal) -> {
                DecimalFormat fmt = new DecimalFormat("#.#");
                fmt.setRoundingMode(RoundingMode.DOWN);
                probeInfo.text(CompoundText.createLabelInfo("Strength/Efficiency/Purity ", fmt.format(crystal.getStrength()) + "% "
                        + fmt.format(crystal.getEfficiency()) + "% "
                        + fmt.format(crystal.getPurity()) + "%"));
                int rfPerTick = crystal.getRfPerTick();
                if (mode == ProbeMode.DEBUG) {
                    probeInfo.text(CompoundText.createLabelInfo("RF/t ", rfPerTick + " RF/t"));
                    probeInfo.text(CompoundText.createLabelInfo("Power ", fmt.format(crystal.getPower()) + "%"));
//                    probeInfo.text(TextStyleClass.INFO + "Instability: " + fmt.format(crystal.getInstability()));
//                    probeInfo.text(TextStyleClass.INFO + "Resistance: " + crystal.getResistance());
//                    probeInfo.text(TextStyleClass.INFO + "Cooldown: " + crystal.getCooldown());
                } else {
                    probeInfo.horizontal().text(TextFormatting.YELLOW + "Power: " + fmt.format(crystal.getPower()) + "% (" + rfPerTick + " RF/t)")
                            .progress((int) crystal.getPower(), 100, probeInfo.defaultProgressStyle()
                                    .suffix("%")
                                    .width(40)
                                    .height(10)
                                    .showText(false)
                                    .filledColor(0xffff0000)
                                    .alternateFilledColor(0xff990000));
                }
            }, "Bad tile entity!");
        }
    }

    private static class TankDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (TankTileEntity tank) -> {
                TankBlob blob = tank.getBlob();
                if (blob != null) {
                    blob.getData().ifPresent(d -> {
                        FluidStack stack = d.getFluidStack();
                        if (!stack.isEmpty()) {
                            probeInfo.tankSimple(blob.getCapacity(), stack);
                            if (stack.getFluid() == CoreModule.LIQUID_CRYSTAL.get()) {
                                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                                decimalFormat.setRoundingMode(RoundingMode.DOWN);
                                probeInfo.text(CompoundText.createLabelInfo("Efficiency ", decimalFormat.format(d.getEfficiency() * 100) + "%"));
                                probeInfo.text(CompoundText.createLabelInfo("Purity ", decimalFormat.format(d.getPurity() * 100) + "%"));
                                probeInfo.text(CompoundText.createLabelInfo("Quality ", decimalFormat.format(d.getQuality() * 100) + "%"));
                                probeInfo.text(CompoundText.createLabelInfo("Strength ", decimalFormat.format(d.getStrength() * 100) + "%"));
                            }
                        }
                    });
                    if (mode == ProbeMode.DEBUG) {
                        probeInfo.text(CompoundText.createLabelInfo("Id ", tank.getMultiblockId()));
                        probeInfo.text(CompoundText.createLabelInfo("Client Height ", tank.getClientRenderHeight()));
                        probeInfo.text(CompoundText.createLabelInfo("MinY ", blob.getMinY()));
                        probeInfo.text(CompoundText.createLabelInfo("#Blocks ", blob.getTankBlocks()));
                    }
                }
            }, "Bad tile entity!");
        }
    }

}
