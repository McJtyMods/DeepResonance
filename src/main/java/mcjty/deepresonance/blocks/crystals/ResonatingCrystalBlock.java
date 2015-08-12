package mcjty.deepresonance.blocks.crystals;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.main.ElecCore;
import elec332.core.world.WorldHelper;
import mcjty.container.GenericBlock;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.network.PacketGetCrystalInfo;
import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.varia.Coordinate;
import mcjty.varia.GlobalCoordinate;
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
        super.addInformation(itemStack, player, list, advancedToolTips);
        list.add("You can feel the latent power present in this crystal.");
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            list.add(EnumChatFormatting.GREEN + "Strength/Efficiency/Purity: " + new DecimalFormat("#.##").format(tagCompound.getFloat("strength")) + "% "
                    + new DecimalFormat("#.##").format(tagCompound.getFloat("efficiency")) + "% "
                    + new DecimalFormat("#.##").format(tagCompound.getFloat("purity")) + "%");
            list.add(EnumChatFormatting.YELLOW + "Power left: " + new DecimalFormat("#.##").format(tagCompound.getFloat("power")) + "%");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity instanceof ResonatingCrystalTileEntity) {
            ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) tileEntity;
            currenttip.add(EnumChatFormatting.GREEN + "Strength/Efficiency/Purity: " + new DecimalFormat("#.##").format(resonatingCrystalTileEntity.getStrength()) + "% "
                    + new DecimalFormat("#.##").format(resonatingCrystalTileEntity.getEfficiency()) + "% "
                    + new DecimalFormat("#.##").format(resonatingCrystalTileEntity.getPurity()) + "%");
            currenttip.add(EnumChatFormatting.YELLOW + "Power left: " + new DecimalFormat("#.##").format(tooltipPower) + "% (" + tooltipRFTick + " RF/t)");
            if (System.currentTimeMillis() - lastTime > 250) {
                lastTime = System.currentTimeMillis();
                DeepResonance.networkHandler.getNetworkWrapper().sendToServer(new PacketGetCrystalInfo(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord));
            }
        }
        return currenttip;
    }

    @Override
    public void onBlockExploded(final World world, final int x, final int y, final int z, Explosion explosion) {
        ElecCore.tickHandler.registerCall(new Runnable() {
            @Override
            public void run() {
                float forceMultiplier = 1;
                TileEntity tile = world.getTileEntity(x, y, z);
                if (tile instanceof ResonatingCrystalTileEntity){
                    ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) tile;
                    forceMultiplier = resonatingCrystalTileEntity.getPower()/10;
                    DRRadiationManager radiationManager = DRRadiationManager.getManager(world);
                    DRRadiationManager.RadiationSource source = radiationManager.getOrCreateRadiationSource(new GlobalCoordinate(new Coordinate(x, y, z), world.provider.dimensionId));
                    source.update(DRRadiationManager.calculateRadiationRadius(resonatingCrystalTileEntity.getRfPerTick(), resonatingCrystalTileEntity.getPurity()),
                            DRRadiationManager.calculateRadiationStrength(resonatingCrystalTileEntity.getStrength(), resonatingCrystalTileEntity.getPurity()),
                            10000);
                }
                WorldHelper.spawnExplosion(world, x, y, z, forceMultiplier);
            }
        });
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
