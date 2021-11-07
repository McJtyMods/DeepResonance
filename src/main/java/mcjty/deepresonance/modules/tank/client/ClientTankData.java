package mcjty.deepresonance.modules.tank.client;

import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.lib.sync.IPositionalData;
import mcjty.lib.varia.LevelTools;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class ClientTankData implements IPositionalData {

    private final GlobalPos pos;
    private final FluidStack fluidStack;
    private final int height;   // 16 is a full tank

    public ClientTankData(GlobalPos pos, FluidStack fluidStack, int height) {
        this.pos = pos;
        this.fluidStack = fluidStack;
        this.height = height;
    }

    @Override
    public ResourceLocation getId() {
        return TankModule.TANK_SYNC_ID;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeResourceLocation(pos.dimension().location());
        buf.writeBlockPos(pos.pos());
        buf.writeFluidStack(fluidStack);
        buf.writeInt(height);
    }

    public static ClientTankData fromBytes(PacketBuffer buf) {
        RegistryKey<World> dimension = LevelTools.getId(buf.readResourceLocation());
        BlockPos pos = buf.readBlockPos();
        FluidStack fluidStack = buf.readFluidStack();
        int height = buf.readInt();
        return new ClientTankData(GlobalPos.of(dimension, pos), fluidStack, height);
    }

    public static void handleClientData(GlobalPos pos, IPositionalData data) {
        if (data instanceof ClientTankData) {
            ClientTankData cd = (ClientTankData) data;
            updateData(pos, cd);
        }
    }

    private static final Map<GlobalPos, ClientTankData> TANK_DATA = new HashMap<>();

    public static void updateData(GlobalPos pos, ClientTankData data) {
        TANK_DATA.put(pos, data);
    }

    @Nullable
    public static ClientTankData getTankData(GlobalPos pos) {
        return TANK_DATA.get(pos);
    }
}
