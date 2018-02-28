package mcjty.deepresonance.blocks.crystals;

import elec332.core.explosion.Elexplosion;
import elec332.core.main.ElecCore;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.GenericDRBlock;
import mcjty.deepresonance.blocks.collector.EnergyCollectorTileEntity;
import mcjty.deepresonance.boom.TestExplosion;
import mcjty.deepresonance.network.DRMessages;
import mcjty.deepresonance.network.PacketGetCrystalInfo;
import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.deepresonance.radiation.RadiationConfiguration;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.container.EmptyContainer;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.DecimalFormat;
import java.util.List;

public class ResonatingCrystalBlock extends GenericDRBlock<ResonatingCrystalTileEntity, EmptyContainer> {

    public static PropertyBool EMPTY = PropertyBool.create("empty");
    public static PropertyBool GENERATED = PropertyBool.create("generated");

    public static int tooltipRFTick = 0;
    public static float tooltipPower = 0;

    private static long lastTime = 0;

    public ResonatingCrystalBlock() {
        super(Material.GLASS, ResonatingCrystalTileEntity.class, EmptyContainer.class, "resonating_crystal", false);
        setHardness(3.0f);
        setResistance(5.0f);
        setHarvestLevel("pickaxe", 2);
        setSoundType(SoundType.GLASS);
    }

    public static final AxisAlignedBB BLOCK_AABB = new AxisAlignedBB(0.1F, 0.0F, 0.1F, 0.9F, 0.8F, 0.9F);

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BLOCK_AABB;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initModel() {
        ClientRegistry.bindTileEntitySpecialRenderer(ResonatingCrystalTileEntity.class, new ResonatingCrystalTESR());

        ModelResourceLocation emptyNaturalModel = new ModelResourceLocation(getRegistryName(), "empty=true,facing=north,generated=false");
        ModelResourceLocation fullNaturalModel = new ModelResourceLocation(getRegistryName(), "empty=false,facing=north,generated=false");
        ModelResourceLocation emptyGeneratedModel = new ModelResourceLocation(getRegistryName(), "empty=true,facing=north,generated=true");
        ModelResourceLocation fullGeneratedModel = new ModelResourceLocation(getRegistryName(), "empty=false,facing=north,generated=true");

        ModelBakery.registerItemVariants(Item.getItemFromBlock(this), emptyNaturalModel, fullNaturalModel);
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(this), new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                float power = stack.getTagCompound() == null ? 1.0f : stack.getTagCompound().getFloat("power");
                float purity = stack.getTagCompound() == null ? 1.0f : stack.getTagCompound().getFloat("purity");
                if (power < EnergyCollectorTileEntity.CRYSTAL_MIN_POWER) {
                    if (purity > 30.0f) {
                        return emptyGeneratedModel;
                    } else {
                        return emptyNaturalModel;
                    }
                } else {
                    if (purity > 30.0f) {
                        return fullGeneratedModel;
                    } else {
                        return fullNaturalModel;
                    }
                }
            }
        });
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag advancedToolTips) {
        NBTTagCompound tagCompound = itemStack.getTagCompound();

        boolean oldOwnership = GeneralConfig.manageOwnership;
        GeneralConfig.manageOwnership = false;
        super.addInformation(itemStack, player, list, advancedToolTips);
        GeneralConfig.manageOwnership = oldOwnership;

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
            list.add(TextFormatting.GREEN + "Strength/Efficiency/Purity: " + decimalFormat.format(tagCompound.getFloat("strength")) + "% "
                    + decimalFormat.format(tagCompound.getFloat("efficiency")) + "% "
                    + decimalFormat.format(tagCompound.getFloat("purity")) + "%");
            list.add(TextFormatting.YELLOW + "Power left: " + decimalFormat.format(power) + "%");
        }
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        TileEntity te = world.getTileEntity(data.getPos());
        if (te instanceof ResonatingCrystalTileEntity) {
            ResonatingCrystalTileEntity crystal = (ResonatingCrystalTileEntity) te;
            DecimalFormat fmt = new DecimalFormat("#.#");
            probeInfo.text(TextFormatting.GREEN + "Strength/Efficiency/Purity: " + fmt.format(crystal.getStrength()) + "% "
                    + fmt.format(crystal.getEfficiency()) + "% "
                    + fmt.format(crystal.getPurity()) + "%");
            int rfPerTick = crystal.getRfPerTick();
            if (mode == ProbeMode.DEBUG) {
                probeInfo.text(TextStyleClass.INFO + "RF/t: " + rfPerTick + " RF/t");
                probeInfo.text(TextStyleClass.INFO + "Power: " + fmt.format(crystal.getPower()) + "%");
                probeInfo.text(TextStyleClass.INFO + "Instability: " + fmt.format(crystal.getInstability()));
                probeInfo.text(TextStyleClass.INFO + "Resistance: " + crystal.getResistance());
                probeInfo.text(TextStyleClass.INFO + "Cooldown: " + crystal.getCooldown());
            } else {
                probeInfo.horizontal().text(TextFormatting.YELLOW + "Power: " + fmt.format(crystal.getPower()) + "% (" + rfPerTick + " RF/t)")
                        .progress((int) crystal.getPower(), 100, probeInfo.defaultProgressStyle()
                                .suffix("%")
                                .width(40)
                                .height(10)
                                .showText(false)
                                .filledColor(0xffff0000)
                                .alternateFilledColor(0xff990000));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity instanceof ResonatingCrystalTileEntity) {
            ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) tileEntity;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            currenttip.add(TextFormatting.GREEN + "Strength/Efficiency/Purity: " + decimalFormat.format(resonatingCrystalTileEntity.getStrength()) + "% "
                    + decimalFormat.format(resonatingCrystalTileEntity.getEfficiency()) + "% "
                    + decimalFormat.format(resonatingCrystalTileEntity.getPurity()) + "%");
            currenttip.add(TextFormatting.YELLOW + "Power left: " + decimalFormat.format(tooltipPower) + "% (" + tooltipRFTick + " RF/t)");
            if (System.currentTimeMillis() - lastTime > 250) {
                lastTime = System.currentTimeMillis();
                DRMessages.INSTANCE.sendToServer(new PacketGetCrystalInfo(tileEntity.getPos()));
            }
        }
        return currenttip;
    }

    @Override
    public void onBlockExploded(final World world, final BlockPos pos, Explosion explosion) {
        if (!world.isRemote) {
            explode(world, pos, false);
        }
        super.onBlockExploded(world, pos, explosion);
    }

    public static void explode(World world, BlockPos pos, boolean strong) {
        final TileEntity theCrystalTile = world.getTileEntity(pos);
        ElecCore.tickHandler.registerCall(() -> {
            float forceMultiplier = 1;
            if (theCrystalTile instanceof ResonatingCrystalTileEntity) {
                ResonatingCrystalTileEntity crystal = (ResonatingCrystalTileEntity) theCrystalTile;
                float explosionStrength = (crystal.getPower() * crystal.getStrength()) / (100.0f * 100.0f);
                forceMultiplier = explosionStrength * (RadiationConfiguration.maximumExplosionMultiplier - RadiationConfiguration.minimumExplosionMultiplier) + RadiationConfiguration.minimumExplosionMultiplier;
                if (forceMultiplier > RadiationConfiguration.absoluteMaximumExplosionMultiplier) {
                    forceMultiplier = RadiationConfiguration.absoluteMaximumExplosionMultiplier;
                }
                if (forceMultiplier > 0.001f) {
                    DRRadiationManager radiationManager = DRRadiationManager.getManager(world);
                    DRRadiationManager.RadiationSource source = radiationManager.getOrCreateRadiationSource(new GlobalCoordinate(pos, WorldHelper.getDimID(world)));
                    float radiationRadius = DRRadiationManager.calculateRadiationRadius(crystal.getStrength(), crystal.getEfficiency(), crystal.getPurity());
                    float radiationStrength = DRRadiationManager.calculateRadiationStrength(crystal.getStrength(), crystal.getPurity());
                    source.update(radiationRadius * RadiationConfiguration.radiationExplosionFactor, radiationStrength / RadiationConfiguration.radiationExplosionFactor, 1000);
                }
            }
            if (forceMultiplier > 0.001f) {
                Elexplosion boom = new TestExplosion(world, null, pos.getX(), pos.getY(), pos.getZ(), forceMultiplier);
                boom.explode();
                if (strong) {
//                    boom = new TestExplosion(world, null, pos.getX()-15, pos.getY(), pos.getZ(), forceMultiplier);
//                    boom.explode();
//                    boom = new TestExplosion(world, null, pos.getX()-15, pos.getY(), pos.getZ(), forceMultiplier);
//                    boom.explode();
                }
            }
        }, world);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }


    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING_HORIZ, EMPTY, GENERATED);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState metadata, int fortune) {
        List<ItemStack> drops = super.getDrops(world, pos, metadata, fortune);
        for (ItemStack drop : drops) {
            if (drop.hasTagCompound()) {
                NBTTagCompound tagCompound = drop.getTagCompound();
                if (tagCompound.hasKey("glowing")) {
                    tagCompound.setBoolean("glowing", false);
                }
            }
        }

        return drops;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity te = worldIn.getTileEntity(pos);
        boolean empty = false;
        boolean generated = false;
        if (te instanceof ResonatingCrystalTileEntity) {
            ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) te;
            empty = resonatingCrystalTileEntity.isEmpty();
            generated = resonatingCrystalTileEntity.getPurity() > 30.0f;
        }
        return super.getActualState(state, worldIn, pos).withProperty(EMPTY, empty).withProperty(GENERATED, generated);
    }
}
