package mcjty.deepresonance.items;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.items.armor.ItemRadiationSuit;
import mcjty.deepresonance.items.manual.DeepResonanceManualItem;
import mcjty.deepresonance.items.rftoolsmodule.RFToolsSupport;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModItems {
    public static DeepResonanceManualItem deepResonanceManualItem;
    public static RadiationMonitorItem radiationMonitorItem;
    public static ResonatingPlateItem resonatingPlateItem;
    public static FilterMaterialItem filterMaterialItem;
    public static SpentFilterMaterialItem spentFilterMaterialItem;
    public static ItemRadiationSuit helmet;
    public static ItemRadiationSuit chestplate;
    public static ItemRadiationSuit leggings;
    public static ItemRadiationSuit boots;
    public static InsertLiquidItem insertLiquidItem;

    public static void init() {
        deepResonanceManualItem = new DeepResonanceManualItem();
        radiationMonitorItem = new RadiationMonitorItem();
        resonatingPlateItem = new ResonatingPlateItem();
        filterMaterialItem = new FilterMaterialItem();
        spentFilterMaterialItem = new SpentFilterMaterialItem();
        insertLiquidItem = new InsertLiquidItem();
        if (DeepResonance.instance.rftools) {
            RFToolsSupport.initItems();
        }

        helmet = newRadiationSuitPart(EntityEquipmentSlot.HEAD, "helmet");
        chestplate = newRadiationSuitPart(EntityEquipmentSlot.CHEST, "chest");
        leggings = newRadiationSuitPart(EntityEquipmentSlot.LEGS, "leggings");
        boots = newRadiationSuitPart(EntityEquipmentSlot.FEET, "boots");
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        deepResonanceManualItem.initModel();
        radiationMonitorItem.initModel();
        resonatingPlateItem.initModel();
        filterMaterialItem.initModel();
        spentFilterMaterialItem.initModel();
        insertLiquidItem.initModel();
        if (DeepResonance.instance.rftools) {
            RFToolsSupport.initItemModels();
        }

        ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation(helmet.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(chestplate, 0, new ModelResourceLocation(chestplate.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(leggings, 0, new ModelResourceLocation(leggings.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(boots, 0, new ModelResourceLocation(boots.getRegistryName(), "inventory"));

    }

    private static ItemRadiationSuit newRadiationSuitPart(EntityEquipmentSlot i, String texture) {
        return new ItemRadiationSuit(ItemArmor.ArmorMaterial.IRON, 0, i, texture);
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
