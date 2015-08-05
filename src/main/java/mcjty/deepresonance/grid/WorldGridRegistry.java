package mcjty.deepresonance.grid;

import elec332.core.registry.AbstractWorldRegistryHolder;
import elec332.core.util.EventHelper;
import mcjty.deepresonance.grid.fluid.DRFluidWorldGridHolder;
import mcjty.deepresonance.grid.fluid.EventHandler;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class WorldGridRegistry extends AbstractWorldRegistryHolder<DRFluidWorldGridHolder> {

    public WorldGridRegistry(){
        EventHelper.registerHandlerForge(new EventHandler());
    }

    @Override
    public boolean serverOnly() {
        return true;
    }

    @Override
    public DRFluidWorldGridHolder newRegistry(World world) {
        return new DRFluidWorldGridHolder(world);
    }

}
