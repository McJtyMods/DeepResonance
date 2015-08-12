package mcjty.deepresonance.compat.handlers;

import cpw.mods.fml.common.event.FMLInterModComms;
import elec332.core.baseclasses.tileentity.BlockTileBase;
import elec332.core.util.AbstractCompatHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Elec332 on 12-8-2015.
 */
public class WailaCompatHandler extends AbstractCompatHandler.ICompatHandler implements IWailaDataProvider{

    public WailaCompatHandler(){
        instance = this;
    }

    private static WailaCompatHandler instance;

    @Override
    public String getName() {
        return "Waila";
    }

    @Override
    public void init() {
        FMLInterModComms.sendMessage("Waila", "register", "mcjty.deepresonance.compat.handlers.WailaCompatHandler.register");
        FMLInterModComms.sendMessage("Waila", "register", "mcjty.wailasupport.WailaCompatibility.load");
    }

    public static void register(IWailaRegistrar registrar){
        registrar.registerHeadProvider(instance, IWailaInfoTile.class);
        registrar.registerBodyProvider(instance, IWailaInfoTile.class);
        registrar.registerTailProvider(instance, IWailaInfoTile.class);
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currentTip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof IWailaInfoTile){
            ((IWailaInfoTile) accessor.getTileEntity()).getWailaBody(itemStack, currentTip, accessor, config);
        }
        return currentTip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currentTip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
        return tag;
    }

    public static interface IWailaInfoTile{

        public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config);

    }
}
