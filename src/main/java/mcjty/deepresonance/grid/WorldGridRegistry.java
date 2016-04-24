package mcjty.deepresonance.grid;

import elec332.core.multiblock.dynamic.AbstractDynamicMultiBlockWorldHolder;
import elec332.core.registry.AbstractWorldRegistryHolder;
import mcjty.deepresonance.grid.fluid.DRFluidWorldGridHolder;
import mcjty.deepresonance.grid.fluid.EventHandler;
import mcjty.deepresonance.grid.tank.DRTankWorldHolder;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class WorldGridRegistry {

    public WorldGridRegistry(){
        this.fluidGridWorldRegistry = new DRFluidGridWorldRegistry();
        this.tankGridWorldRegistry = new DRTankGridWorldRegistry();
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    private DRFluidGridWorldRegistry fluidGridWorldRegistry;
    private DRTankGridWorldRegistry tankGridWorldRegistry;

    public DRFluidGridWorldRegistry getFluidRegistry() {
        return fluidGridWorldRegistry;
    }

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

    public static class DRFluidGridWorldRegistry extends AbstractWorldRegistryHolder<DRFluidWorldGridHolder>{

        private DRFluidGridWorldRegistry(){
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

}
