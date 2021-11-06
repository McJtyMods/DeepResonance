package mcjty.deepresonance.modules.tank.data;

import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.deepresonance.util.LiquidCrystalData;
import mcjty.lib.McJtyLib;
import mcjty.lib.sync.IPositionalData;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ClientTankData implements IPositionalData {

    private final LiquidCrystalData data;

    public ClientTankData(LiquidCrystalData data) {
        this.data = data;
    }

    @Override
    public ResourceLocation getId() {
        return TankModule.TANK_SYNC_ID;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        data.toBytes(buf);
    }

    public static ClientTankData fromBytes(PacketBuffer buf) {
        return new ClientTankData(new LiquidCrystalData(buf));
    }

    public static void handleClientData(GlobalPos pos, IPositionalData data) {
        if (data instanceof ClientTankData) {
            ClientTankData cd = (ClientTankData) data;
            // We're client side so the world has to be the only existing one
            World world = McJtyLib.proxy.getClientWorld();
            TileEntity be = world.getBlockEntity(pos.pos());
            if (be instanceof TankTileEntity) {
                int multiblockId = ((TankTileEntity) be).getMultiblockId();
                updateData(multiblockId, cd.data);
            }
        }
    }

    private static final Map<Integer, LiquidCrystalData> TANK_DATA = new HashMap<>();

    public static void updateData(int tankBlobId, LiquidCrystalData data) {
        TANK_DATA.put(tankBlobId, data);
    }

    @Nullable
    public static LiquidCrystalData getTankData(int tankBlobId) {
        return TANK_DATA.get(tankBlobId);
    }
}
