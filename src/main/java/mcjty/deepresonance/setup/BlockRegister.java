package mcjty.deepresonance.setup;

import elec332.core.api.registration.IBlockRegister;
import mcjty.deepresonance.blocks.BlockTank;
import mcjty.deepresonance.util.DeepResonanceResourceLocation;
import net.minecraft.block.Block;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Created by Elec332 on 7-1-2020
 */
public class BlockRegister implements IBlockRegister {

    public static Block tank;

    @Override
    public void preRegister() {
        tank = new BlockTank().setRegistryName(new DeepResonanceResourceLocation("tank"));
    }

    @Override
    public void register(IForgeRegistry<Block> registry) {
        registry.register(tank);
    }

}
