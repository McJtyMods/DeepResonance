package mcjty.deepresonance.modules.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 18-1-2020
 */
public class BlockMachineFrame extends Block {

    public BlockMachineFrame() {
        super(Properties.create(Material.IRON));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return null;
    }

}
