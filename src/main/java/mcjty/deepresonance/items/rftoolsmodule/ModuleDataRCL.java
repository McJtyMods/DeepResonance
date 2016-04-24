package mcjty.deepresonance.items.rftoolsmodule;

import io.netty.buffer.ByteBuf;
import mcjty.deepresonance.DeepResonance;
import mcjty.rftools.api.screens.data.IModuleData;

public class ModuleDataRCL implements IModuleData {

    public static final String ID = DeepResonance.MODID + ":RCL";

    private final int purity;
    private final int strength;
    private final int efficiency;

    public ModuleDataRCL(int purity, int strength, int efficiency) {
        this.efficiency = efficiency;
        this.purity = purity;
        this.strength = strength;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public int getPurity() {
        return purity;
    }

    public int getStrength() {
        return strength;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void writeToBuf(ByteBuf buf) {
        buf.writeByte((byte) purity);
        buf.writeByte((byte) strength);
        buf.writeByte((byte) efficiency);
    }
}
