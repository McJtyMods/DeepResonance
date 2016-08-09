package mcjty.deepresonance.grid;

import elec332.core.multiblock.dynamic.AbstractDynamicMultiBlockWorldHolder;
import elec332.core.registry.AbstractWorldRegistryHolder;
import mcjty.deepresonance.grid.tank.DRTankWorldHolder;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class WorldGridRegistry {

    public WorldGridRegistry(){
        this.tankGridWorldRegistry = new DRTankGridWorldRegistry();
    }

    private DRTankGridWorldRegistry tankGridWorldRegistry;

    public DRTankGridWorldRegistry getTankRegistry() {
        return tankGridWorldRegistry;
    }

    public static class DRTankGridWorldRegistry extends AbstractWorldRegistryHolder<AbstractDynamicMultiBlockWorldHolder>{

        private DRTankGridWorldRegistry(){
        }

        @Override
        public boolean serverOnly() {
            return true;
        }

        @Override
        public AbstractDynamicMultiBlockWorldHolder newRegistry(World world) {
            return new DRTankWorldHolder(world);
        }
    }

}
