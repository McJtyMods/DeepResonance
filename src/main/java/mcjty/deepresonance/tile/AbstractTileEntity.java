package mcjty.deepresonance.tile;

import elec332.core.api.registration.RegisteredTileEntity;
import elec332.core.handler.annotations.TileEntityAnnotationProcessor;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 7-1-2020
 */
public class AbstractTileEntity extends GenericTileEntity implements RegisteredTileEntity.TypeSetter {

    public AbstractTileEntity() {
        super(null);
        this.setTileEntityType(TileEntityAnnotationProcessor.getTileType(this.getClass()));
    }

    public AbstractTileEntity(TileEntityType<?> type) {
        super(type);
        this.type = type;
    }

    private TileEntityType<?> type;

    @Override
    public void setTileEntityType(TileEntityType<?> type) {
        this.type = type;
    }

    @Nonnull
    @Override
    public TileEntityType<?> getType() {
        return this.type;
    }

}
