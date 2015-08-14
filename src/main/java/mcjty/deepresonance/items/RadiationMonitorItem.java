package mcjty.deepresonance.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.network.PacketGetRadiationLevel;
import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.deepresonance.radiation.RadiationConfiguration;
import mcjty.varia.Coordinate;
import mcjty.varia.GlobalCoordinate;
import mcjty.varia.Logging;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class RadiationMonitorItem extends Item {
    private static long lastTime = 0;
    public static float radiationStrength = 0.0f;

    private IIcon radiationLevel[] = new IIcon[10];

    public RadiationMonitorItem() {
        setMaxStackSize(1);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            GlobalCoordinate c = new GlobalCoordinate(new Coordinate((int) player.posX, (int) player.posY, (int) player.posZ), world.provider.dimensionId);
            float maxStrength = calculateRadiationStrength(world, c);
            if (maxStrength <= 0.0f) {
                Logging.message(player, EnumChatFormatting.GREEN + "No radiation detected");
            } else {
                Logging.message(player, EnumChatFormatting.RED + "Radiation of strength " + new DecimalFormat("#.##").format(maxStrength) + "!");
            }
        }
        return stack;
    }

    private static double getDistanceSq(Coordinate c1, Coordinate c2) {
        double dx = c1.getX() - c2.getX();
        double dy = c1.getY() - c2.getY();
        double dz = c1.getZ() - c2.getZ();
        return dx * dx + dy * dy + dz * dz;
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
                double distanceSq = getDistanceSq(player.getCoordinate(), coordinate.getCoordinate());
                if (distanceSq < radiusSq) {
                    double distance = Math.sqrt(distanceSq);
                    float strength = (float) (radiationSource.getStrength() * (radius - distance));
                    if (strength > maxStrength) {
                        maxStrength = strength;
                    }
                }
            }
        }
        return maxStrength;
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        for (int i = 0 ; i <= 9 ; i++) {
            radiationLevel[i] = iconRegister.registerIcon(DeepResonance.MODID + ":radiationMonitorItem" + i);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconIndex(ItemStack stack) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        fetchRadiation(player);
        int level = (int) ((10*radiationStrength) / RadiationConfiguration.maxRadiationMeter);
        if (level < 0) {
            level = 0;
        } else if (level > 9) {
            level = 9;
        }
        return radiationLevel[level];
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getIconString() {
        int level = (int) ((10*radiationStrength) / RadiationConfiguration.maxRadiationMeter);
        if (level < 0) {
            level = 0;
        } else if (level > 9) {
            level = 9;
        }
        return DeepResonance.MODID + ":radiationMonitorItem" + level;
    }


    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        fetchRadiation(player);
        if (radiationStrength <= 0.0f) {
            list.add(EnumChatFormatting.GREEN + "No radiation detected");
        } else {
            list.add(EnumChatFormatting.RED + "Radiation: " + new DecimalFormat("#.##").format(radiationStrength) + "!");
        }
    }

    private void fetchRadiation(EntityPlayer player) {
        if (System.currentTimeMillis() - lastTime > 500) {
            int id = player.worldObj.provider.dimensionId;
            lastTime = System.currentTimeMillis();
            GlobalCoordinate c = new GlobalCoordinate(new Coordinate((int) player.posX, (int) player.posY, (int) player.posZ), id);
            DeepResonance.networkHandler.getNetworkWrapper().sendToServer(new PacketGetRadiationLevel(c));
        }
    }
}