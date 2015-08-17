package mcjty.deepresonance.items;

import cpw.mods.fml.common.registry.GameRegistry;
import elec332.core.baseclasses.item.BaseItem;
import elec332.core.player.PlayerHelper;
import elec332.core.util.NBTHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.duct.TileBasicFluidDuct;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.items.manual.DeepResonanceManualItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.Random;

public final class ModItems {

    public static DeepResonanceManualItem deepResonanceManualItem;
    public static RadiationMonitorItem radiationMonitorItem;

    public static void init() {
        deepResonanceManualItem = new DeepResonanceManualItem();
        deepResonanceManualItem.setUnlocalizedName("DeepResonanceManual");
        deepResonanceManualItem.setCreativeTab(DeepResonance.tabDeepResonance);
        deepResonanceManualItem.setTextureName(DeepResonance.MODID + ":deepResonanceManual");
        GameRegistry.registerItem(deepResonanceManualItem, "deepResonanceManualItem");

        radiationMonitorItem = new RadiationMonitorItem();
        radiationMonitorItem.setUnlocalizedName("RadiationMonitor");
        radiationMonitorItem.setCreativeTab(DeepResonance.tabDeepResonance);
        GameRegistry.registerItem(radiationMonitorItem, "radiationMonitorItem");

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
            if (!world.isRemote){
                if (tile instanceof TileTank){
                    if (((TileTank) tile).getMultiBlock() == null){
                        System.out.println("ERROR: multiblock == null");
                    } else if (!player.isSneaking()){
                        PlayerHelper.sendMessageToPlayer(player, ((TileTank) tile).getMultiBlock().getTankInfo());
                    }
                } else if (tile instanceof TileBasicFluidDuct) {
                    if (((TileBasicFluidDuct) tile).getGrid() == null) {
                        System.out.println("ERROR: grid == null");
                    } else if (!player.isSneaking()) {
                        PlayerHelper.sendMessageToPlayer(player, ((TileBasicFluidDuct) tile).getGrid().getInfo());
                    } else {
                        ((TileBasicFluidDuct) tile).getGrid().addStackToInternalTank(new FluidStack(DRFluidRegistry.liquidCrystal, new Random().nextInt(3000), new NBTHelper().addToTag(new Random().nextFloat() * 5, "purity").toNBT()), true);
                    }
                }
            }
            return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
        }

    }
}
