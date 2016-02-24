package mcjty.deepresonance.items;

import elec332.core.world.WorldHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.network.PacketGetRadiationLevel;
import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.deepresonance.radiation.RadiationConfiguration;
import mcjty.deepresonance.varia.QuadTree;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.lib.varia.Logging;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

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
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            GlobalCoordinate c = new GlobalCoordinate(player.getPosition(), WorldHelper.getDimID(world));
            float maxStrength = calculateRadiationStrength(world, c);
            if (maxStrength <= 0.0f) {
                Logging.message(player, EnumChatFormatting.GREEN + "No radiation detected");
            } else {
                Logging.message(player, EnumChatFormatting.RED + "Strength of Radiation " + new Float(maxStrength).intValue() + "!");
            }
        }
        return stack;
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
                    strength = strength * (float) radiationTree.factor(cx, cy, cz, player.getCoordinate().getX(), player.getCoordinate().getY(), player.getCoordinate().getZ());
                    if (strength > maxStrength) {
                        maxStrength = strength;
                    }
                }
            }
        }
        return maxStrength;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        fetchRadiation(player);
        if (radiationStrength <= 0.0f) {
            list.add(EnumChatFormatting.GREEN + "No radiation detected");
        } else {
            list.add(EnumChatFormatting.RED + "Radiation: " + new Float(radiationStrength).intValue() + "!");
        }
    }

    public static void fetchRadiation(EntityPlayer player) {
        if (System.currentTimeMillis() - lastTime > 250) {
            int id = WorldHelper.getDimID(player.getEntityWorld());
            lastTime = System.currentTimeMillis();
            GlobalCoordinate c = new GlobalCoordinate(player.getPosition(), id);
            DeepResonance.networkHandler.getNetworkWrapper().sendToServer(new PacketGetRadiationLevel(c));
        }
    }
}