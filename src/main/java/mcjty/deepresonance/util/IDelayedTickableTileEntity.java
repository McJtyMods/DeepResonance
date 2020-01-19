package mcjty.deepresonance.util;

import com.google.common.base.Preconditions;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Elec332 on 19-1-2020
 */
public interface IDelayedTickableTileEntity extends ITickableTileEntity {

    @Override
    default void tick() {
        if (!Preconditions.checkNotNull(((TileEntity) this).getWorld()).isRemote) {
            DeepResonanceTickHandler.INSTANCE.addDelayedTickable(this);
        }
    }

    void postTick();

}
