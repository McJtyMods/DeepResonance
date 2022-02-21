package mcjty.deepresonance.modules.core.block;

import mcjty.deepresonance.compat.DeepResonanceTOPDriver;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.radiation.manager.DRRadiationManager;
import mcjty.deepresonance.modules.radiation.util.RadiationConfiguration;
import mcjty.deepresonance.util.Constants;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;

public class ResonatingCrystalBlock extends BaseBlock {

    private static final VoxelShape AABB = VoxelShapes.box(0.1f, 0, 0.1f, 0.9f, 0.8f, 0.9f);

    private final boolean generated;
    private final boolean empty;

    public ResonatingCrystalBlock(boolean generated, boolean empty) {
        super(new BlockBuilder()
                .topDriver(DeepResonanceTOPDriver.DRIVER)
                .properties(AbstractBlock.Properties
                        .of(Material.METAL)
                        .strength(2.0f)
                        .sound(SoundType.METAL)
                        .noOcclusion()
                )
                .tileEntitySupplier(ResonatingCrystalTileEntity::new));
        this.generated = generated;
        this.empty = empty;
    }

    public ResonatingCrystalBlock getEmpty() {
        if (generated) {
            return CoreModule.RESONATING_CRYSTAL_GENERATED_EMPTY.get();
        } else {
            return CoreModule.RESONATING_CRYSTAL_NATURAL_EMPTY.get();
        }
    }

    public boolean isGenerated() {
        return generated;
    }

    public boolean isEmpty() {
        return empty;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return AABB;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof ResonatingCrystalTileEntity) {
            return createStack((ResonatingCrystalTileEntity) tile);
        }
        return new ItemStack(this);
    }

    public ItemStack createStack(ResonatingCrystalTileEntity crystal) {
        ItemStack ret = new ItemStack(this);
        ret.addTagElement(CoreModule.TILE_DATA_TAG, crystal.save(new CompoundNBT()));
        return ret;
    }

    @Override
    public void fillItemCategory(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        ResonatingCrystalTileEntity crystal = new ResonatingCrystalTileEntity();
        for (int power : new int[]{0, 50}) {
            for (int purity : new int[]{0, 50}) {
                ItemStack stack = new ItemStack(this);
                crystal.setPurity(purity);
                crystal.setPower(power);
                stack.addTagElement(CoreModule.TILE_DATA_TAG, crystal.save(new CompoundNBT()));
                items.add(stack);
            }
        }
    }

    @Override
    public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {
        if (!world.isClientSide) {
            explode(world, pos, false);
        }
        super.onBlockExploded(state, world, pos, explosion);
    }

    private static void explodeHelper(World world, BlockPos location, float radius) {
        Explosion boom = new Explosion(world, null, location.getX(), location.getY(), location.getZ(), radius, false, Explosion.Mode.BREAK);
        for(int x = (int)(-radius); x < radius; ++x) {
            for(int y = (int)(-radius); y < radius; ++y) {
                for(int z = (int)(-radius); z < radius; ++z) {
                    BlockPos targetPosition = location.offset(x, y, z);
                    double dist = Math.sqrt(location.distSqr(targetPosition));
                    if(dist < radius) {
                        BlockState state = world.getBlockState(targetPosition);
                        Block block = state.getBlock();
                        float resistance = state.getExplosionResistance(world, targetPosition, boom);
                        if (!state.isAir() && resistance > 0 && (dist < radius - 1.0F || world.random.nextFloat() > 0.7D)) {
                            block.onBlockExploded(state, world, targetPosition, boom);
                        }
                    }
                }
            }
        }
    }


    public static void explode(World world, BlockPos pos, boolean strong) {
        TileEntity theCrystalTile = world.getBlockEntity(pos);
        world.getServer().tell(new TickDelayedTask((int) (world.getGameTime()+1), () -> {
            float forceMultiplier = 1;
            if (theCrystalTile instanceof ResonatingCrystalTileEntity) {
                ResonatingCrystalTileEntity crystal = (ResonatingCrystalTileEntity) theCrystalTile;
                float explosionStrength = (float) ((crystal.getPower() * crystal.getStrength()) / (100.0f * 100.0f));
                forceMultiplier = explosionStrength * (RadiationConfiguration.maximumExplosionMultiplier - RadiationConfiguration.minimumExplosionMultiplier) + RadiationConfiguration.minimumExplosionMultiplier;
                if (forceMultiplier > RadiationConfiguration.absoluteMaximumExplosionMultiplier) {
                    forceMultiplier = RadiationConfiguration.absoluteMaximumExplosionMultiplier;
                }
                if (forceMultiplier > 0.001f) {
                    DRRadiationManager radiationManager = DRRadiationManager.getManager(world);
                    DRRadiationManager.RadiationSource source = radiationManager.getOrCreateRadiationSource(GlobalPos.of(world.dimension(), pos));
                    float radiationRadius = DRRadiationManager.calculateRadiationRadius(crystal.getStrength(), crystal.getEfficiency(), crystal.getPurity());
                    float radiationStrength = DRRadiationManager.calculateRadiationStrength(crystal.getStrength(), crystal.getPurity());
                    source.update(radiationRadius * RadiationConfiguration.radiationExplosionFactor, radiationStrength / RadiationConfiguration.radiationExplosionFactor, 1000);
                }
            }
            if (forceMultiplier > 0.001f) {
                explodeHelper(world, pos, forceMultiplier);
                if (strong) {
//                    explodeHelper(world, pos.west(15), forceMultiplier);
//                    explodeHelper(world, pos.west(15), forceMultiplier);
                }
            }
        }));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable IBlockReader world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag advanced) {
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound != null) {
            tagCompound = tagCompound.getCompound(CoreModule.TILE_DATA_TAG).getCompound("Info");
        }

        super.appendHoverText(stack, world, tooltip, advanced);

        float power = 100.0f;
        if (tagCompound != null) {
            power = tagCompound.getFloat("power");
        }
        if (power > Constants.CRYSTAL_MIN_POWER) {
            tooltip.add(new TranslationTextComponent("message.deepresonance.crystal_power"));
        } else {
            tooltip.add(new TranslationTextComponent("message.deepresonance.crystal_empty"));
        }
        if (tagCompound != null) {
            addBasicInformation(tooltip::add, tagCompound, power, true);
        }
    }

    public static void addBasicInformation(Consumer<ITextComponent> tooltip, CompoundNBT tag, float power, boolean showPower) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        tooltip.accept(new TranslationTextComponent("message.deepresonance.crystal_sep")
                .withStyle(TextFormatting.GREEN)
                .append(": "
                        + decimalFormat.format(tag.getFloat("strength")) + "% "
                        + decimalFormat.format(tag.getFloat("efficiency")) + "% "
                        + decimalFormat.format(tag.getFloat("purity")) + "%"
                )
        );
        if (showPower) {
            tooltip.accept(new StringTextComponent("Power left: " + decimalFormat.format(power) + "%").withStyle(TextFormatting.YELLOW));
        }

    }
}
