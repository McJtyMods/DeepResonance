package mcjty.deepresonance.modules.core.block;

import mcjty.deepresonance.compat.DeepResonanceTOPDriver;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.radiation.manager.DRRadiationManager;
import mcjty.deepresonance.modules.radiation.util.RadiationConfiguration;
import mcjty.deepresonance.util.Constants;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.varia.ComponentFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;

public class ResonatingCrystalBlock extends BaseBlock {

    private static final VoxelShape AABB = Shapes.box(0.1f, 0, 0.1f, 0.9f, 0.8f, 0.9f);

    private final boolean generated;
    private final boolean empty;

    public ResonatingCrystalBlock(boolean generated, boolean empty) {
        super(new BlockBuilder()
                .topDriver(DeepResonanceTOPDriver.DRIVER)
                .properties(BlockBehaviour.Properties
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
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return AABB;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof ResonatingCrystalTileEntity) {
            return createStack((ResonatingCrystalTileEntity) tile);
        }
        return new ItemStack(this);
    }

    public ItemStack createStack(ResonatingCrystalTileEntity crystal) {
        ItemStack ret = new ItemStack(this);
        ret.addTagElement(CoreModule.TILE_DATA_TAG, crystal.saveWithoutMetadata());
        return ret;
    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
        ResonatingCrystalTileEntity crystal = new ResonatingCrystalTileEntity(BlockPos.ZERO, CoreModule.RESONATING_CRYSTAL_GENERATED.get().defaultBlockState());
        for (int power : new int[]{0, 50}) {
            for (int purity : new int[]{0, 50}) {
                ItemStack stack = new ItemStack(this);
                crystal.setPurity(purity);
                crystal.setPower(power);
                stack.addTagElement(CoreModule.TILE_DATA_TAG, crystal.saveWithoutMetadata());
                items.add(stack);
            }
        }
    }

    @Override
    public void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion) {
        if (!world.isClientSide) {
            explode(world, pos);
        }
        super.onBlockExploded(state, world, pos, explosion);
    }

    private static void explodeHelper(Level world, BlockPos location, float radius) {
        Explosion boom = new Explosion(world, null, location.getX(), location.getY(), location.getZ(), radius, false, Explosion.BlockInteraction.BREAK);
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


    public static void explode(Level world, BlockPos pos) {
        BlockEntity theCrystalTile = world.getBlockEntity(pos);
        world.getServer().tell(new TickTask((int) (world.getGameTime()+1), () -> {
            double forceMultiplier = 1;
            if (theCrystalTile instanceof ResonatingCrystalTileEntity crystal) {
                float explosionStrength = (float) ((crystal.getPower() * crystal.getStrength()) / (100.0f * 100.0f));
                forceMultiplier = explosionStrength * (RadiationConfiguration.MAXIMUM_EXPLOSION_MULTIPLIER.get() - RadiationConfiguration.MINIMUM_EXPLOSION_MULTIPLIER.get()) + RadiationConfiguration.MINIMUM_EXPLOSION_MULTIPLIER.get();
                if (forceMultiplier > RadiationConfiguration.ABSOLUTE_MAXIMUM_EXPLOSION_MULTIPLIER.get()) {
                    forceMultiplier = RadiationConfiguration.ABSOLUTE_MAXIMUM_EXPLOSION_MULTIPLIER.get();
                }
                if (forceMultiplier > 0.001f) {
                    DRRadiationManager radiationManager = DRRadiationManager.getManager(world);
                    DRRadiationManager.RadiationSource source = radiationManager.getOrCreateRadiationSource(GlobalPos.of(world.dimension(), pos));
                    double radiationRadius = DRRadiationManager.calculateRadiationRadius(crystal.getStrength(), crystal.getEfficiency(), crystal.getPurity());
                    double radiationStrength = DRRadiationManager.calculateRadiationStrength(crystal.getStrength(), crystal.getPurity());
                    source.update((float) (radiationRadius * RadiationConfiguration.RADIATION_EXPLOSION_FACTOR.get()), (float) (radiationStrength / RadiationConfiguration.RADIATION_EXPLOSION_FACTOR.get()), 1000);
                }
            }
            if (forceMultiplier > 0.001f) {
                explodeHelper(world, pos, (float) forceMultiplier);
            }
        }));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable BlockGetter world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag advanced) {
        CompoundTag tagCompound = stack.getTag();
        if (tagCompound != null) {
            tagCompound = tagCompound.getCompound(CoreModule.TILE_DATA_TAG).getCompound("Info");
        }

        super.appendHoverText(stack, world, tooltip, advanced);

        float power = 100.0f;
        if (tagCompound != null) {
            power = tagCompound.getFloat("power");
        }
        if (power > Constants.CRYSTAL_MIN_POWER) {
            tooltip.add(ComponentFactory.translatable("message.deepresonance.crystal_power"));
        } else {
            tooltip.add(ComponentFactory.translatable("message.deepresonance.crystal_empty"));
        }
        if (tagCompound != null) {
            addBasicInformation(tooltip::add, tagCompound, power, true);
        }
    }

    public static void addBasicInformation(Consumer<Component> tooltip, CompoundTag tag, float power, boolean showPower) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        tooltip.accept(ComponentFactory.translatable("message.deepresonance.crystal_sep")
                .withStyle(ChatFormatting.GREEN)
                .append(": "
                        + decimalFormat.format(tag.getFloat("strength")) + "% "
                        + decimalFormat.format(tag.getFloat("efficiency")) + "% "
                        + decimalFormat.format(tag.getFloat("purity")) + "%"
                )
        );
        if (showPower) {
            tooltip.accept(ComponentFactory.literal("Power left: " + decimalFormat.format(power) + "%").withStyle(ChatFormatting.YELLOW));
        }

    }
}
