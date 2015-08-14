package mcjty.deepresonance.blocks;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class MachineFrame extends Block {
    public MachineFrame() {
        super(Material.iron);
        setBlockName("machineFrame");
        setBlockTextureName(DeepResonance.MODID + ":" + "machineSide");
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return false;
    }
}
