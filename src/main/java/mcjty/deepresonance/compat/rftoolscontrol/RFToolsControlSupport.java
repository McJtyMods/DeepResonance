package mcjty.deepresonance.compat.rftoolscontrol;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.modules.machines.block.LaserTileEntity;
import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.deepresonance.util.LiquidCrystalData;
import mcjty.rftoolsbase.api.control.code.Opcode;
import mcjty.rftoolsbase.api.control.parameters.*;
import mcjty.rftoolsbase.api.control.registry.IOpcodeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.function.Function;

import static mcjty.rftoolsbase.api.control.code.IOpcodeRunnable.OpcodeResult.POSITIVE;
import static mcjty.rftoolsbase.api.control.code.OpcodeOutput.SINGLE;
import static mcjty.rftoolsbase.api.control.parameters.ParameterType.*;

public class RFToolsControlSupport {
    public static final Opcode EVAL_READPURITY = Opcode.builder()
            .id("deepresonance:read_purity")
            .description(
                    ChatFormatting.GREEN + "Eval: read purity (Deep Resonance)",
                    "read purity of the RCL liquid in a tank",
                    "adjacent to the processor or a connected node",
                    "This opcode returns -1 if the tank is not a",
                    "tank or does not contain RCL")
            .outputDescription("purity between 0 and 100 (integer)")
            .opcodeOutput(SINGLE)
            .parameter(ParameterDescription.builder().name("tank").type(PAR_INVENTORY).description("tank adjacent to (networked) block").build())
            .icon(0, 5, DeepResonance.MODID + ":textures/gui/guielements.png")
            .runnable(((processor, program, opcode) -> {
                Inventory inv = processor.evaluateParameter(opcode, program, 0);
                BlockEntity te = processor.getTileEntityAt(inv);
                int purity = -1;
                if (te instanceof TankTileEntity tank) {
                    LiquidCrystalData lcd = LiquidCrystalData.fromStack(tank.getFluidHandler().getFluid());
                    purity = (int) (lcd.getPurity() * 100 + .5f);
                }
                program.setLastValue(Parameter.builder().type(PAR_INTEGER).value(ParameterValue.constant(purity)).build());
                return POSITIVE;
            }))
            .build();
    public static final Opcode EVAL_READSTRENGTH = Opcode.builder()
            .id("deepresonance:read_strength")
            .description(
                    ChatFormatting.GREEN + "Eval: read strength (Deep Resonance)",
                    "read strength of the RCL liquid in a tank",
                    "adjacent to the processor or a connected node",
                    "This opcode returns -1 if the tank is not a",
                    "tank or does not contain RCL")
            .outputDescription("strength between 0 and 100 (integer)")
            .opcodeOutput(SINGLE)
            .parameter(ParameterDescription.builder().name("tank").type(PAR_INVENTORY).description("tank adjacent to (networked) block").build())
            .icon(1, 5, DeepResonance.MODID + ":textures/gui/guielements.png")
            .runnable(((processor, program, opcode) -> {
                Inventory inv = processor.evaluateParameter(opcode, program, 0);
                BlockEntity te = processor.getTileEntityAt(inv);
                int purity = -1;
                if (te instanceof TankTileEntity tank) {
                    LiquidCrystalData lcd = LiquidCrystalData.fromStack(tank.getFluidHandler().getFluid());
                    purity = (int) (lcd.getStrength() * 100 + .5f);
                }
                program.setLastValue(Parameter.builder().type(PAR_INTEGER).value(ParameterValue.constant(purity)).build());
                return POSITIVE;
            }))
            .build();
    public static final Opcode EVAL_READEFFICIENCY = Opcode.builder()
            .id("deepresonance:read_efficiency")
            .description(
                    ChatFormatting.GREEN + "Eval: read efficiency (Deep Resonance)",
                    "read efficiency of the RCL liquid in a tank",
                    "adjacent to the processor or a connected node",
                    "This opcode returns -1 if the tank is not a",
                    "tank or does not contain RCL")
            .outputDescription("efficiency between 0 and 100 (integer)")
            .opcodeOutput(SINGLE)
            .parameter(ParameterDescription.builder().name("tank").type(PAR_INVENTORY).description("tank adjacent to (networked) block").build())
            .icon(2, 5, DeepResonance.MODID + ":textures/gui/guielements.png")
            .runnable(((processor, program, opcode) -> {
                Inventory inv = processor.evaluateParameter(opcode, program, 0);
                BlockEntity te = processor.getTileEntityAt(inv);
                int purity = -1;
                if (te instanceof TankTileEntity tank) {
                    LiquidCrystalData lcd = LiquidCrystalData.fromStack(tank.getFluidHandler().getFluid());
                    purity = (int) (lcd.getEfficiency() * 100 + .5f);
                }
                program.setLastValue(Parameter.builder().type(PAR_INTEGER).value(ParameterValue.constant(purity)).build());
                return POSITIVE;
            }))
            .build();
    public static final Opcode EVAL_READQUALITY = Opcode.builder()
            .id("deepresonance:read_quality")
            .description(
                    ChatFormatting.GREEN + "Eval: read quality (Deep Resonance)",
                    "read quality of the RCL liquid in a tank",
                    "adjacent to the processor or a connected node",
                    "This opcode returns -1 if the tank is not a",
                    "tank or does not contain RCL")
            .outputDescription("quality between 0 and 100 (integer)")
            .opcodeOutput(SINGLE)
            .parameter(ParameterDescription.builder().name("tank").type(PAR_INVENTORY).description("tank adjacent to (networked) block").build())
            .icon(3, 5, DeepResonance.MODID + ":textures/gui/guielements.png")
            .runnable(((processor, program, opcode) -> {
                Inventory inv = processor.evaluateParameter(opcode, program, 0);
                BlockEntity te = processor.getTileEntityAt(inv);
                int purity = -1;
                if (te instanceof TankTileEntity tank) {
                    LiquidCrystalData lcd = LiquidCrystalData.fromStack(tank.getFluidHandler().getFluid());
                    purity = (int) (lcd.getQuality() * 100 + .5f);
                }
                program.setLastValue(Parameter.builder().type(PAR_INTEGER).value(ParameterValue.constant(purity)).build());
                return POSITIVE;
            }))
            .build();
    public static final Opcode EVAL_READPOWER = Opcode.builder()
            .id("deepresonance:read_power")
            .description(
                    ChatFormatting.GREEN + "Eval: read power (Deep Resonance)",
                    "read remaining power (in percentage between 0 and 100)",
                    "of a crystal adjacent to the processor or a connected node",
                    "Returns -1 if the block there is not a crystal")
            .outputDescription("power between 0 and 100 (integer)")
            .opcodeOutput(SINGLE)
            .parameter(ParameterDescription.builder().name("crystal").type(PAR_SIDE).description("crystal adjacent to (networked) block").build())
            .icon(4, 5, DeepResonance.MODID + ":textures/gui/guielements.png")
            .runnable(((processor, program, opcode) -> {
                BlockSide inv = processor.evaluateSideParameterNonNull(opcode, program, 0);
                BlockEntity te = processor.getTileEntityAt(inv);
                int power = -1;
                if (te instanceof ResonatingCrystalTileEntity crystal) {
                    power = (int) crystal.getPower();
                }
                program.setLastValue(Parameter.builder().type(PAR_INTEGER).value(ParameterValue.constant(power)).build());
                return POSITIVE;
            }))
            .build();
    public static final Opcode EVAL_READLASER = Opcode.builder()
            .id("deepresonance:read_laser")
            .description(
                    ChatFormatting.GREEN + "Eval: read laser (Deep Resonance)",
                    "read the remaining crystal liquid in a laser",
                    "adjacent to the processor or a connected node",
                    "Returns -1 if the block there is not a laser")
            .outputDescription("remaining crystal liquid in laser (integer)")
            .opcodeOutput(SINGLE)
            .parameter(ParameterDescription.builder().name("laser").type(PAR_SIDE).description("laser adjacent to (networked) block").build())
            .icon(5, 5, DeepResonance.MODID + ":textures/gui/guielements.png")
            .runnable(((processor, program, opcode) -> {
                BlockSide inv = processor.evaluateSideParameterNonNull(opcode, program, 0);
                BlockEntity te = processor.getTileEntityAt(inv);
                float crystal = -1;
                if (te instanceof LaserTileEntity laser) {
                    crystal = laser.getCrystalLiquid();
                }
                program.setLastValue(Parameter.builder().type(PAR_FLOAT).value(ParameterValue.constant(crystal)).build());
                return POSITIVE;
            }))
            .build();

    public static class GetOpcodeRegistry implements Function<IOpcodeRegistry, Void> {
        @Nullable
        @Override
        public Void apply(IOpcodeRegistry registry) {
            registry.register(EVAL_READPURITY);
            registry.register(EVAL_READSTRENGTH);
            registry.register(EVAL_READEFFICIENCY);
            registry.register(EVAL_READQUALITY);
            registry.register(EVAL_READPOWER);
            registry.register(EVAL_READLASER);
            return null;
        }
    }
}
