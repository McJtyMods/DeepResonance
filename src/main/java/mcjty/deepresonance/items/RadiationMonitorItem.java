package mcjty.deepresonance.items;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.network.PacketGetRadiationLevel;
import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.deepresonance.radiation.RadiationConfiguration;
import mcjty.deepresonance.varia.QuadTree;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.lib.varia.Logging;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;

public class RadiationMonitorItem extends GenericDRItem {
    private static long lastTime = 0;
    public static float radiationStrength = 0.0f;

    public RadiationMonitorItem() {
        super("radiation_monitor");
        setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        for (int i = 0 ; i <= 9 ; i++) {
            String domain = getRegistryName().getResourceDomain();
            String path = getRegistryName().getResourcePath();
            ModelBakery.registerItemVariants(this, new ModelResourceLocation(new ResourceLocation(domain, path + i), "inventory"));
        }

        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
                fetchRadiation(player);
                int level = (int) ((10 * radiationStrength) / RadiationConfiguration.maxRadiationMeter);
                if (level < 0) {
                    level = 0;
                } else if (level > 9) {
                    level = 9;
                }
                String domain = getRegistryName().getResourceDomain();
                String path = getRegistryName().getResourcePath();
                return new ModelResourceLocation(new ResourceLocation(domain, path + level), "inventory");
            }
        });
    }


    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            GlobalCoordinate c = new GlobalCoordinate(player.getPosition(), WorldHelper.getDimID(world));
            float maxStrength = calculateRadiationStrength(world, c);
            if (maxStrength <= 0.0f) {
                Logging.message(player, TextFormatting.GREEN + "No radiation detected");
            } else {
                Logging.message(player, TextFormatting.RED + "Strength of Radiation " + new Float(maxStrength).intValue() + "!");
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    public static float calculateRadiationStrength(World world, GlobalCoordinate player) {
        int id = player.getDimension();
        DRRadiationManager radiationManager = DRRadiationManager.getManager(world);
        float maxStrength = -1.0f;
        for (Map.Entry<GlobalCoordinate, DRRadiationManager.RadiationSource> source : radiationManager.getRadiationSources().entrySet()) {
            GlobalCoordinate coordinate = source.getKey();
            if (coordinate.getDimension() == id) {
                DRRadiationManager.RadiationSource radiationSource = source.getValue();
                float radius = radiationSource.getRadius();
                float radiusSq = radius * radius;
                double distanceSq = player.getCoordinate().distanceSq(coordinate.getCoordinate());
                if (distanceSq < radiusSq) {
                    double distance = Math.sqrt(distanceSq);
                    float strength = (float) (radiationSource.getStrength() * (radius - distance) / radius);
                    int cx = coordinate.getCoordinate().getX();
                    int cy = coordinate.getCoordinate().getY();
                    int cz = coordinate.getCoordinate().getZ();
                    QuadTree radiationTree = radiationSource.getRadiationTree(world, cx, cy, cz);
                    strength = strength * (float) radiationTree.factor2(cx, cy, cz, player.getCoordinate().getX(), player.getCoordinate().getY()+1, player.getCoordinate().getZ());
                    if (strength > maxStrength) {
                        maxStrength = strength;
                    }
                }
            }
        }
        return maxStrength;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean advancedToolTip) {
        super.addInformation(itemStack, player, list, advancedToolTip);
        fetchRadiation(player);
        if (radiationStrength <= 0.0f) {
            list.add(TextFormatting.GREEN + "No radiation detected");
        } else {
            list.add(TextFormatting.RED + "Radiation: " + new Float(radiationStrength).intValue() + "!");
        }
    }

    public static void fetchRadiation(EntityPlayer player) {
        if (System.currentTimeMillis() - lastTime > 250) {
            int id = WorldHelper.getDimID(player.getEntityWorld());
            lastTime = System.currentTimeMillis();
            GlobalCoordinate c = new GlobalCoordinate(player.getPosition(), id);
            DeepResonance.networkHandler.sendToServer(new PacketGetRadiationLevel(c));
        }
    }
}