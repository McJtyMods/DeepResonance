package mcjty.deepresonance.blocks.crystals;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.GenericDRBlock;
import mcjty.lib.container.EmptyContainer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import elec332.core.explosion.Elexplosion;
import elec332.core.main.ElecCore;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.collector.EnergyCollectorTileEntity;
import mcjty.deepresonance.boom.TestExplosion;
import mcjty.deepresonance.network.PacketGetCrystalInfo;
import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.deepresonance.radiation.RadiationConfiguration;
import mcjty.lib.container.GenericBlock;
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

public class ResonatingCrystalBlock extends GenericDRBlock<ResonatingCrystalTileEntity, EmptyContainer> {

    public static int tooltipRFTick = 0;
    public static float tooltipPower = 0;

    private static long lastTime = 0;

    public ResonatingCrystalBlock() {
        super(Material.glass, ResonatingCrystalTileEntity.class, EmptyContainer.class, "resonating_crystal", false);
        setHardness(3.0f);
        setResistance(5.0f);
        setHarvestLevel("pickaxe", 2);
        setStepSound(soundTypeGlass);
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
                DeepResonance.networkHandler.getNetworkWrapper().sendToServer(new PacketGetCrystalInfo(tileEntity.getPos()));
            }
        }
        return currenttip;
    }

    @Override
    public void onBlockExploded(final World world, final BlockPos pos, Explosion explosion) {
        if (!world.isRemote) {
            final TileEntity theCrystalTile = WorldHelper.getTileAt(world, pos);
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
                            DRRadiationManager.RadiationSource source = radiationManager.getOrCreateRadiationSource(new GlobalCoordinate(pos, WorldHelper.getDimID(world)));
                            float radiationRadius = DRRadiationManager.calculateRadiationRadius(resonatingCrystalTileEntity.getEfficiency(), resonatingCrystalTileEntity.getPurity());
                            float radiationStrength = DRRadiationManager.calculateRadiationStrength(resonatingCrystalTileEntity.getStrength(), resonatingCrystalTileEntity.getPurity());
                            source.update(radiationRadius * RadiationConfiguration.radiationExplosionFactor, radiationStrength / RadiationConfiguration.radiationExplosionFactor, 1000);
                        }
                    }
                    if (forceMultiplier > 0.001f) {
                        Elexplosion boom = new TestExplosion(world, null, pos.getX(), pos.getY(), pos.getZ(), forceMultiplier);
                        boom.explode();
                    }
                }
            }, world);
        }
        super.onBlockExploded(world, pos, explosion);
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    public int getRenderType() {
        return 2;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
