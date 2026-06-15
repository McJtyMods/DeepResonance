package mcjty.deepresonance.modules.pedestal.block;

import mcjty.deepresonance.compat.DeepResonanceTOPDriver;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalBlock;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.modules.core.data.Crystal;
import mcjty.deepresonance.modules.generator.block.EnergyCollectorBlock;
import mcjty.deepresonance.modules.generator.block.EnergyCollectorTileEntity;
import mcjty.deepresonance.modules.generator.util.CollectorConfig;
import mcjty.deepresonance.modules.pedestal.PedestalModule;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.container.InventoryLocator;
import mcjty.lib.setup.Registration;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.OrientationTools;
import mcjty.lib.varia.SoundTools;
import mcjty.rftoolsbase.tools.ManualHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.Optional;
import java.util.function.Function;

import static mcjty.deepresonance.DeepResonance.SHIFT_MESSAGE;
import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;
import static mcjty.lib.container.SlotDefinition.specific;

public class PedestalTileEntity extends TickingTileEntity {

    public static final int SLOT_CRYSTAL = 0;

    private static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(specific(PedestalTileEntity::isValidCrystal).in(), SLOT_CRYSTAL, 64, 24)
            .playerSlots(10, 70));

    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .itemValid((slot, stack) -> isValidCrystal(stack)
            ).build();
    @Cap(type = CapType.ITEMS_AUTOMATION)
    private static final Function<PedestalTileEntity, GenericItemHandler> ITEMS_CAP = tile -> tile.items;

    @Cap(type = CapType.CONTAINER)
    private static final Function<PedestalTileEntity, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<GenericContainer>("Pedestal")
            .containerSupplier(container(PedestalModule.CONTAINER_PEDESTAL, CONTAINER_FACTORY, be))
            .itemHandler(() -> be.items)
            .setupSync(be);

    public PedestalTileEntity(BlockPos pos, BlockState state) {
        super(PedestalModule.TYPE_PEDESTAL.get(), pos, state);
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .tileEntitySupplier(PedestalTileEntity::new)
                .topDriver(DeepResonanceTOPDriver.DRIVER)
                .infusable()
                .manualEntry(ManualHelper.create("deepresonance:machines/pedestal"))
                .info(key(SHIFT_MESSAGE))
                .infoShift(header())
        );
    }

    private int checkCounter = 0;

    // Cache for the inventory used to put the spent crystal material in.
    private final InventoryLocator inventoryLocator = new InventoryLocator();

    private BlockPos cachedLocator = null;

    @Override
    public void tickServer() {
        checkCounter--;
        if (checkCounter > 0) {
            return;
        }
        checkCounter = 20;

        BlockPos b = this.getCrystalPosition();
        if (level.getBlockState(b).isAir()) {
            // Nothing in front. We can place a new crystal if we have one.
            placeCrystal();
        } else if (level.getBlockState(b).getBlock() instanceof ResonatingCrystalBlock) {
            // Check if the crystal in front of us still has power.
            // If not we will remove it.
            checkCrystal();
        } // else we can do nothing.
    }

    public BlockPos getCrystalPosition() {
        BlockState state = level.getBlockState(worldPosition);
        Direction orientation = OrientationTools.getOrientation(state);
        return worldPosition.relative(orientation);
    }

    public Optional<ResonatingCrystalTileEntity> getCrystal() {
        if (level.getBlockEntity(this.getCrystalPosition()) instanceof ResonatingCrystalTileEntity crystal) {
            return Optional.of(crystal);
        }
        return Optional.empty();
    }

    public boolean crystalPresent() {
        return level.getBlockState(this.getCrystalPosition()).getBlock() instanceof ResonatingCrystalBlock;
    }

    private static boolean isValidCrystal(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            return blockItem.getBlock() instanceof ResonatingCrystalBlock;
        }
        return false;
    }

    private void placeCrystal() {
        BlockPos pos = getCrystalPosition();
        ItemStack crystalStack = items.getStackInSlot(SLOT_CRYSTAL);
        if (!crystalStack.isEmpty()) {
            if (crystalStack.getItem() instanceof BlockItem blockItem) {
                BlockHitResult result = new BlockHitResult(Vec3.ZERO, Direction.UP, pos, false);
                BlockPlaceContext context = new BlockPlaceContext(FakePlayerFactory.getMinecraft((ServerLevel) level),
                        InteractionHand.MAIN_HAND, crystalStack, result);
                blockItem.place(context);
                ResonatingCrystalBlock b = CoreModule.RESONATING_CRYSTAL_GENERATED.block().get();
                SoundTools.playSound(level, b.defaultBlockState().getSoundType(level, pos, null).getBreakSound(), worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 1.0f, 1.0f);

                if (findCollector()) {
                    BlockEntity tileEntity = level.getBlockEntity(new BlockPos(cachedLocator));
                    if (tileEntity instanceof EnergyCollectorTileEntity collector) {
                        collector.addCrystal(pos.getX(), pos.getY(), pos.getZ());
                    }
                }
            }
        }
    }

    private static Direction[] directions = new Direction[] {
            Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH,
            Direction.UP, Direction.DOWN
    };

    private void checkCrystal() {
        Optional<Boolean> powerLow = getCrystal().map(tile -> tile.getPower() <= EnergyCollectorTileEntity.CRYSTAL_MIN_POWER);
        if (powerLow.orElse(false)) {
            dropCrystal();
        }
    }

    public void dropCrystal() {
        getCrystal().ifPresent(crystal -> {
            BlockPos p = crystal.getBlockPos();
            BlockState crystalState = level.getBlockState(p);
            if (crystalState.getBlock() instanceof ResonatingCrystalBlock crystalBlock) {
                ItemStack spentCrystal = new ItemStack(crystalBlock.getEmpty(), 1);
                Crystal data = crystal.getData(CoreModule.CRYSTAL_DATA);
                spentCrystal.set(CoreModule.ITEM_CRYSTAL_DATA, data);
                inventoryLocator.ejectStack(level, worldPosition, spentCrystal, worldPosition, directions);
                level.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                SoundTools.playSound(level, crystalState.getSoundType().getBreakSound(), p.getX(), p.getY(), p.getZ(), 1.0f, 1.0f);
            }
        });
    }

    private boolean findCollector() {
        BlockPos crystalLocation = getCrystalPosition();
        if (cachedLocator != null) {
            if (level.getBlockState(cachedLocator).getBlock() instanceof EnergyCollectorBlock) {
                return true;
            }
            cachedLocator = null;
        }

        float closestDistance = Float.MAX_VALUE;

        int yy = crystalLocation.getY(), xx = crystalLocation.getX(), zz = crystalLocation.getZ();
        for (int y = yy - CollectorConfig.MAX_VERTICAL_CRYSTAL_DISTANCE.get() ; y <= yy + CollectorConfig.MAX_VERTICAL_CRYSTAL_DISTANCE.get() ; y++) {
            if (y >= 0 && y < level.getHeight()) {
                int maxhordist = CollectorConfig.MAX_HORIZONTAL_CRYSTAL_DISTANCE.get();
                for (int x = xx - maxhordist; x <= xx + maxhordist; x++) {
                    for (int z = zz - maxhordist; z <= zz + maxhordist; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (level.getBlockState(pos).getBlock() instanceof EnergyCollectorBlock) {
                            double sqdist = pos.distSqr(crystalLocation);
                            if (sqdist < closestDistance) {
                                closestDistance = (float)sqdist;
                                cachedLocator = pos;
                            }
                        }
                    }
                }
            }
        }
        return cachedLocator != null;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        items.applyImplicitComponents(input.get(Registration.ITEM_INVENTORY));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        items.collectImplicitComponents(builder);
    }
}
