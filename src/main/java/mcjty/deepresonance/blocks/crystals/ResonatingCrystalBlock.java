package mcjty.deepresonance.blocks.crystals;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.explosion.Elexplosion;
import elec332.core.main.ElecCore;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.collector.EnergyCollectorTileEntity;
import mcjty.deepresonance.boom.TestExplosion;
import mcjty.deepresonance.network.PacketGetCrystalInfo;
import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.deepresonance.radiation.RadiationConfiguration;
import mcjty.lib.container.GenericBlock;
import mcjty.lib.varia.Coordinate;
import mcjty.lib.varia.GlobalCoordinate;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.text.DecimalFormat;
import java.util.List;

public class ResonatingCrystalBlock extends GenericBlock {

    public static int tooltipRFTick = 0;
    public static float tooltipPower = 0;

    private static long lastTime = 0;

    public ResonatingCrystalBlock() {
        super(DeepResonance.instance, Material.glass, ResonatingCrystalTileEntity.class, false);
        setBlockName("resonatingCrystalBlock");
        setHardness(3.0f);
        setResistance(5.0f);
        setHarvestLevel("pickaxe", 2);
        setBlockTextureName(DeepResonance.MODID + ":crystal");
        setStepSound(soundTypeGlass);
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean advancedToolTips) {
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            tagCompound.removeTag("owner");
            tagCompound.removeTag("ownerM");
            tagCompound.removeTag("idM");
        }
        super.addInformation(itemStack, player, list, advancedToolTips);
        float power = 100.0f;
        if (tagCompound != null) {
            power = tagCompound.getFloat("power");
        }
        if (power > EnergyCollectorTileEntity.CRYSTAL_MIN_POWER) {
            list.add("You can feel the latent power present in this crystal.");
        } else {
            list.add("This crystal is depleted. Perhaps it still has a future use?");
        }
        if (tagCompound != null) {
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            list.add(EnumChatFormatting.GREEN + "Strength/Efficiency/Purity: " + decimalFormat.format(tagCompound.getFloat("strength")) + "% "
                    + decimalFormat.format(tagCompound.getFloat("efficiency")) + "% "
                    + decimalFormat.format(tagCompound.getFloat("purity")) + "%");
            list.add(EnumChatFormatting.YELLOW + "Power left: " + decimalFormat.format(power) + "%");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity instanceof ResonatingCrystalTileEntity) {
            ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) tileEntity;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            currenttip.add(EnumChatFormatting.GREEN + "Strength/Efficiency/Purity: " + decimalFormat.format(resonatingCrystalTileEntity.getStrength()) + "% "
                    + decimalFormat.format(resonatingCrystalTileEntity.getEfficiency()) + "% "
                    + decimalFormat.format(resonatingCrystalTileEntity.getPurity()) + "%");
            currenttip.add(EnumChatFormatting.YELLOW + "Power left: " + decimalFormat.format(tooltipPower) + "% (" + tooltipRFTick + " RF/t)");
            if (System.currentTimeMillis() - lastTime > 250) {
                lastTime = System.currentTimeMillis();
                DeepResonance.networkHandler.getNetworkWrapper().sendToServer(new PacketGetCrystalInfo(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord));
            }
        }
        return currenttip;
    }

    @Override
    public void onBlockExploded(final World world, final int x, final int y, final int z, Explosion explosion) {
        if (!world.isRemote) {
            final TileEntity theCrystalTile = world.getTileEntity(x, y, z);
            ElecCore.tickHandler.registerCall(new Runnable() {
                @Override
                public void run() {
                    float forceMultiplier = 1;
                    if (theCrystalTile instanceof ResonatingCrystalTileEntity) {
                        ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) theCrystalTile;
                        float explosionStrength = (resonatingCrystalTileEntity.getPower() * resonatingCrystalTileEntity.getStrength()) / (100.0f * 100.0f);
                        forceMultiplier = explosionStrength * (RadiationConfiguration.maximumExplosionMultiplier - RadiationConfiguration.minimumExplosionMultiplier) + RadiationConfiguration.minimumExplosionMultiplier;
                        if (forceMultiplier > RadiationConfiguration.absoluteMaximumExplosionMultiplier) {
                            forceMultiplier = RadiationConfiguration.absoluteMaximumExplosionMultiplier;
                        }
                        if (forceMultiplier > 0.001f) {
                            DRRadiationManager radiationManager = DRRadiationManager.getManager(world);
                            DRRadiationManager.RadiationSource source = radiationManager.getOrCreateRadiationSource(new GlobalCoordinate(new Coordinate(x, y, z), world.provider.dimensionId));
                            float radiationRadius = DRRadiationManager.calculateRadiationRadius(resonatingCrystalTileEntity.getEfficiency(), resonatingCrystalTileEntity.getPurity());
                            float radiationStrength = DRRadiationManager.calculateRadiationStrength(resonatingCrystalTileEntity.getStrength(), resonatingCrystalTileEntity.getPurity());
                            source.update(radiationRadius * RadiationConfiguration.radiationExplosionFactor, radiationStrength / RadiationConfiguration.radiationExplosionFactor, 1000);
                        }
                    }
                    if (forceMultiplier > 0.001f) {
                        Elexplosion boom = new TestExplosion(world, null, x, y, z, forceMultiplier);
                        boom.explode();
                    }
                }
            }, world);
        }
        super.onBlockExploded(world, x, y, z, explosion);
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    public String getSideIconName() {
        return "crystal";
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
