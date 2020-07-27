package mcjty.deepresonance.util;

import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 27-7-2020
 */
public class DeepResonanceContainer extends GenericContainer {

    public DeepResonanceContainer(@Nullable ContainerType<?> type, int id, ContainerFactory factory, BlockPos pos, @Nullable GenericTileEntity te) {
        super(type, id, factory, pos, te);
        if (te instanceof Modifier) {
            ((Modifier) te).modify(this);
        }
    }

    @Nonnull
    @Override
    public IntReferenceHolder trackInt(@Nonnull IntReferenceHolder intIn) {
        return super.trackInt(intIn);
    }

    @Override
    public void trackIntArray(@Nonnull IIntArray arrayIn) {
        super.trackIntArray(arrayIn);
    }

    public interface Modifier {

        void modify(DeepResonanceContainer container);

    }

}
