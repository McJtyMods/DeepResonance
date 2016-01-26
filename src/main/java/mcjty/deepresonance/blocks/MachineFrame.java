package mcjty.deepresonance.blocks;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class MachineFrame extends Block {

    public MachineFrame() {
        super(Material.iron);
        setUnlocalizedName(DeepResonance.MODID + ".machineFrame");
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return false;
    }

}
