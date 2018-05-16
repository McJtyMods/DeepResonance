package mcjty.deepresonance.blocks;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.blocks.GenericBlock;
import mcjty.lib.blocks.GenericItemBlock;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.function.BiFunction;

public abstract class GenericDRBlock<T extends GenericTileEntity, C extends Container> extends GenericBlock<T, C> {

    public GenericDRBlock(Material material, Class<? extends T> tileEntityClass, BiFunction<EntityPlayer, IInventory, C> containerFactory, String name, boolean isContainer) {
        super(DeepResonance.instance, material, tileEntityClass, containerFactory, GenericItemBlock.class, name, isContainer);
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

    public GenericDRBlock(Material material, Class<? extends T> tileEntityClass, BiFunction<EntityPlayer, IInventory, C> containerFactory, Class<? extends ItemBlock> itemBlockClass, String name, boolean isContainer) {
        super(DeepResonance.instance, material, tileEntityClass, containerFactory, itemBlockClass, name, isContainer);
        setCreativeTab(DeepResonance.tabDeepResonance);
    }


    @Override
    protected boolean checkAccess(World world, EntityPlayer player, TileEntity te) {
        if (te instanceof GenericTileEntity) {
//            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
//            if ((!SecurityTools.isAdmin(player)) && (!player.getPersistentID().equals(genericTileEntity.getOwnerUUID()))) {
//                int securityChannel = genericTileEntity.getSecurityChannel();
//                if (securityChannel != -1) {
//                    SecurityChannels securityChannels = SecurityChannels.getChannels(world);
//                    SecurityChannels.SecurityChannel channel = securityChannels.getChannel(securityChannel);
//                    boolean playerListed = channel.getPlayers().contains(player.getDisplayName());
//                    if (channel.isWhitelist() != playerListed) {
//                        Logging.message(player, TextFormatting.RED + "You have no permission to use this block!");
//                        return true;
//                    }
//                }
//            }
        }
        return false;
    }


}
