package mcjty.deepresonance;

import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.tileentity.TileEntity;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;

public class ResonatingCrystalEventHandlers {

    @SubscribeEvent
    public void onBlockBreakEvent(BlockEvent.BreakEvent event) {
    
        TileEntity te = event.getWorld().getTileEntity( event.getPos() );
        if (te instanceof ResonatingCrystalTileEntity) {
            ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) te;
            resonatingCrystalTileEntity.setGlowing(false);
        }
    }

}
