package mcjty.deepresonance.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import elec332.core.api.annotations.StaticLoad;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.Collection;

/**
 * Created by Elec332 on 19-1-2020
 */
@StaticLoad
public enum DeepResonanceTickHandler {

    INSTANCE;

    static {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, INSTANCE::onPostWorldTick);
    }

    private final Multimap<Class<? extends IDelayedTickableTileEntity>, IDelayedTickableTileEntity> typeMap;

    DeepResonanceTickHandler() {
        typeMap = HashMultimap.create();
    }

    @SuppressWarnings("unchecked")
    public void addDelayedTickable(IDelayedTickableTileEntity tile) {
        Class<? extends IDelayedTickableTileEntity> type = tile.getClass();
        while (IDelayedTickableTileEntity.class.isAssignableFrom(type.getSuperclass())) {
            type = (Class<? extends IDelayedTickableTileEntity>) type.getSuperclass();
        }
        Class<? extends IDelayedTickableTileEntity> type_ = type;
        accessMap(() -> typeMap.put(type_, tile));
    }

    public void tickType(Class<? extends IDelayedTickableTileEntity> type) {
        accessMap(() -> {
            Collection<IDelayedTickableTileEntity> tickables = typeMap.removeAll(type);
            tickables.forEach(IDelayedTickableTileEntity::postTick);
        });
    }

    private void onPostWorldTick(TickEvent.WorldTickEvent event) {
        if (event.world.isRemote) {
            return;
        }
        accessMap(() -> {
            typeMap.values().forEach(IDelayedTickableTileEntity::postTick);
            typeMap.clear();
        });
    }

    private synchronized void accessMap(Runnable r) {
        r.run();
    }

}
