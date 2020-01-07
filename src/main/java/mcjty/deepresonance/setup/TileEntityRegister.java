package mcjty.deepresonance.setup;

import elec332.core.api.registration.ITileRegister;
import elec332.core.util.RegistryHelper;
import mcjty.deepresonance.tile.TileEntityTank;
import mcjty.deepresonance.util.DeepResonanceResourceLocation;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Created by Elec332 on 7-1-2020
 */
public class TileEntityRegister implements ITileRegister {

    @Override
    public void register(IForgeRegistry<TileEntityType<?>> registry) {
        RegistryHelper.registerTileEntity(TileEntityTank.class, new DeepResonanceResourceLocation("tank"));
    }

}
