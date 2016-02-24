package mcjty.deepresonance.items;

import net.minecraftforge.fml.common.registry.GameRegistry;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.items.armor.ItemRadiationSuit;
import mcjty.deepresonance.items.manual.DeepResonanceManualItem;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModItems {
    public static DeepResonanceManualItem deepResonanceManualItem;
    public static RadiationMonitorItem radiationMonitorItem;
    public static ResonatingPlateItem resonatingPlateItem;
    public static FilterMaterialItem filterMaterialItem;
    public static SpentFilterMaterialItem spentFilterMaterialItem;
//    public static ItemRadiationSuit helmet, chestplate, leggings, boots;
    public static InsertLiquidItem insertLiquidItem;

    public static void init() {
        deepResonanceManualItem = new DeepResonanceManualItem();
        radiationMonitorItem = new RadiationMonitorItem();
        resonatingPlateItem = new ResonatingPlateItem();
        filterMaterialItem = new FilterMaterialItem();
        spentFilterMaterialItem = new SpentFilterMaterialItem();
        insertLiquidItem = new InsertLiquidItem();

//        helmet = newRadiationSuitPart(0, "Helmet");
//        chestplate = newRadiationSuitPart(1, "Chest");
//        leggings = newRadiationSuitPart(2, "Leggings");
//        boots = newRadiationSuitPart(3, "Boots");
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        deepResonanceManualItem.initModel();
        radiationMonitorItem.initModel();
        resonatingPlateItem.initModel();
        filterMaterialItem.initModel();
        spentFilterMaterialItem.initModel();
        insertLiquidItem.initModel();
    }

    private static ItemRadiationSuit newRadiationSuitPart(int i, String texture) {
        ItemRadiationSuit ret = new ItemRadiationSuit(ItemArmor.ArmorMaterial.IRON, 0, i, texture);
        GameRegistry.registerItem(ret, "radiationSuit"+i);
        return ret;
    }

    /**
     * Testing only, will be removed soon *TM*
     *
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

    }*/
}
