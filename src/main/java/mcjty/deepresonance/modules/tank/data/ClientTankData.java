package mcjty.deepresonance.modules.tank.data;

import mcjty.deepresonance.util.LiquidCrystalData;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ClientTankData {

    private static final Map<Integer, LiquidCrystalData> TANK_DATA = new HashMap<>();

    public static void updateData(int tankBlobId, LiquidCrystalData data) {
        TANK_DATA.put(tankBlobId, data);
    }

    @Nullable
    public static LiquidCrystalData getTankData(int tankBlobId) {
        return TANK_DATA.get(tankBlobId);
    }
}
