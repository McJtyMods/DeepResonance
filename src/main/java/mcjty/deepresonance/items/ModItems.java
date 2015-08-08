package mcjty.deepresonance.items;

import cpw.mods.fml.common.registry.GameRegistry;
import elec332.core.baseclasses.item.BaseItem;
import elec332.core.player.PlayerHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.duct.TileBasicFluidDuct;
import mcjty.deepresonance.items.manual.DeepResonanceManualItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public final class ModItems {

    public static DeepResonanceManualItem deepResonanceManualItem;

    public static void init() {
        deepResonanceManualItem = new DeepResonanceManualItem();
        deepResonanceManualItem.setUnlocalizedName("DeepResonanceManual");
        deepResonanceManualItem.setCreativeTab(DeepResonance.tabDeepResonance);
        deepResonanceManualItem.setTextureName(DeepResonance.MODID + ":deepResonanceManual");
        GameRegistry.registerItem(deepResonanceManualItem, "deepResonanceManualItem");
        new TestItem(); //Auto-registers
    }

    /**
     * Testing only, will be removed soon *TM*
     */
    private static class TestItem extends BaseItem {

        public TestItem() {
            super("GridDebugger", DeepResonance.tabDeepResonance, DeepResonance.MODID);
        }

        @Override
        public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileBasicFluidDuct && !world.isRemote){
                PlayerHelper.sendMessageToPlayer(player, ""+((TileBasicFluidDuct) tile).getGrid().amount);
            }
            return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
        }

    }
}
