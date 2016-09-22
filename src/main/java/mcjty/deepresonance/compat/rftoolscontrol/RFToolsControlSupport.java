package mcjty.deepresonance.compat.rftoolscontrol;

import com.google.common.base.Function;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import mcjty.rftoolscontrol.api.code.Opcode;
import mcjty.rftoolscontrol.api.parameters.Inventory;
import mcjty.rftoolscontrol.api.parameters.Parameter;
import mcjty.rftoolscontrol.api.parameters.ParameterDescription;
import mcjty.rftoolscontrol.api.parameters.ParameterValue;
import mcjty.rftoolscontrol.api.registry.IOpcodeRegistry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;

import static mcjty.rftoolscontrol.api.code.IOpcodeRunnable.OpcodeResult.POSITIVE;
import static mcjty.rftoolscontrol.api.code.OpcodeOutput.SINGLE;
import static mcjty.rftoolscontrol.api.parameters.ParameterType.PAR_INTEGER;
import static mcjty.rftoolscontrol.api.parameters.ParameterType.PAR_INVENTORY;

public class RFToolsControlSupport {

    public static final Opcode EVAL_READPURITY = Opcode.builder()
            .id("deepresonance:read_purity")
            .description(
                    TextFormatting.GREEN + "Eval: read purity (Deep Resonance)",
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
                TileEntity te = processor.getTileEntityAt(inv);
                int purity = -1;
                if (te instanceof TileTank) {
                    TileTank tank = (TileTank) te;
                    LiquidCrystalFluidTagData lcd = LiquidCrystalFluidTagData.fromStack(tank.getFluid());
                    if (lcd != null) {
                        purity = (int) (lcd.getPurity() * 100 + .5f);
                    }
                }
                program.setLastValue(Parameter.builder().type(PAR_INTEGER).value(ParameterValue.constant(purity)).build());
                return POSITIVE;
            }))
            .build();
    public static final Opcode EVAL_READSTRENGTH = Opcode.builder()
            .id("deepresonance:read_strength")
            .description(
                    TextFormatting.GREEN + "Eval: read strength (Deep Resonance)",
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
                TileEntity te = processor.getTileEntityAt(inv);
                int purity = -1;
                if (te instanceof TileTank) {
                    TileTank tank = (TileTank) te;
                    LiquidCrystalFluidTagData lcd = LiquidCrystalFluidTagData.fromStack(tank.getFluid());
                    if (lcd != null) {
                        purity = (int) (lcd.getStrength() * 100 + .5f);
                    }
                }
                program.setLastValue(Parameter.builder().type(PAR_INTEGER).value(ParameterValue.constant(purity)).build());
                return POSITIVE;
            }))
            .build();
    public static final Opcode EVAL_READEFFICIENCY = Opcode.builder()
            .id("deepresonance:read_efficiency")
            .description(
                    TextFormatting.GREEN + "Eval: read efficiency (Deep Resonance)",
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
                TileEntity te = processor.getTileEntityAt(inv);
                int purity = -1;
                if (te instanceof TileTank) {
                    TileTank tank = (TileTank) te;
                    LiquidCrystalFluidTagData lcd = LiquidCrystalFluidTagData.fromStack(tank.getFluid());
                    if (lcd != null) {
                        purity = (int) (lcd.getEfficiency() * 100 + .5f);
                    }
                }
                program.setLastValue(Parameter.builder().type(PAR_INTEGER).value(ParameterValue.constant(purity)).build());
                return POSITIVE;
            }))
            .build();
    public static final Opcode EVAL_READQUALITY = Opcode.builder()
            .id("deepresonance:read_quality")
            .description(
                    TextFormatting.GREEN + "Eval: read quality (Deep Resonance)",
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
                TileEntity te = processor.getTileEntityAt(inv);
                int purity = -1;
                if (te instanceof TileTank) {
                    TileTank tank = (TileTank) te;
                    LiquidCrystalFluidTagData lcd = LiquidCrystalFluidTagData.fromStack(tank.getFluid());
                    if (lcd != null) {
                        purity = (int) (lcd.getQuality() * 100 + .5f);
                    }
                }
                program.setLastValue(Parameter.builder().type(PAR_INTEGER).value(ParameterValue.constant(purity)).build());
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
            return null;
        }
    }

}
