package mcjty.deepresonance.util;

import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 27-7-2020
 */
public class DeepResonanceContainer extends GenericContainer {

    public DeepResonanceContainer(@Nullable ContainerType<?> type, int id, ContainerFactory factory, BlockPos pos, @Nullable GenericTileEntity te) {
        super(type, id, factory, pos, te);
    }

}
