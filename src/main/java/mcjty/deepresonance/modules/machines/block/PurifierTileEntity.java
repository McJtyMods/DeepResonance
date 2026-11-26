package mcjty.deepresonance.modules.machines.block;

import mcjty.deepresonance.modules.tank.util.TankTransfers;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.minecraft.world.level.block.Block;
import static mcjty.lib.setup.Registration.ITEM_INVENTORY;
import net.minecraft.core.RegistryAccess;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.util.config.PurifierConfig;
import mcjty.deepresonance.modules.tank.util.DualTankHook;
import mcjty.deepresonance.util.LiquidCrystalData;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.container.InventoryLocator;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolsbase.tools.ManualHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;

import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.container.GenericItemHandler.match;
import static mcjty.lib.container.SlotDefinition.specific;

public class PurifierTileEntity extends TickingTileEntity {

    public static final int SLOT = 0;

    private final DualTankHook tankHook = new DualTankHook(this, Direction.UP, Direction.DOWN).allowDuplicates().setTimeout(10);

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(specific(CoreModule.FILTER_MATERIAL_ITEM.get()).in().out(), SLOT, 64, 24)
            .playerSlots(10, 70));

    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .itemValid(match(CoreModule.FILTER_MATERIAL_ITEM))
            .build();
    @Cap(type = CapType.ITEMS_AUTOMATION)
    private static final Function<PurifierTileEntity, GenericItemHandler> ITEMS_CAP = tile -> tile.items;

    @Cap(type = CapType.CONTAINER)
    private static final Function<PurifierTileEntity, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<GenericContainer>("Purifier")
            .containerSupplier(container(MachinesModule.PURIFIER_CONTAINER, CONTAINER_FACTORY, be))
            .itemHandler(() -> be.items)
            .setupSync(be);

    // Cache for the inventory used to put the spent filter material in.
    private final InventoryLocator inventoryLocator = new InventoryLocator();

    private int timeToGo = 0;
    private ILiquidCrystalData processing = null;

    public PurifierTileEntity(BlockPos pos, BlockState state) {
        super(MachinesModule.TYPE_PURIFIER.get(), pos, state);
    }

    public static BaseBlock createBlock(Block.Properties properties) {
        return new BaseBlock(
                new BlockBuilder()
                        .properties(properties)
                        .tileEntitySupplier(PurifierTileEntity::new)
                .manualEntry(ManualHelper.create("deepresonance:machines/purifier"))
                        .info(TooltipBuilder.key("message.deepresonance.shiftmessage"))
                        .infoShift(TooltipBuilder.header())) {

            @Override
            public RotationType getRotationType() {
                return RotationType.HORIZROTATION;
            }
        };
    }

    @Override
    public void tickServer() {
        if (!tankHook.checkTanks()) {
            maybeOutput();
            return;
        }
        if (timeToGo >= 0) {
            if (!hasFilter()) {
                return;
            }
            if (timeToGo == 0) {
                maybeOutput();
                return;
            }
            timeToGo--;
        } else {
            if (!LiquidCrystalData.isValidLiquidCrystalStack(TankTransfers.simulateExtract(tankHook.getTank1(), 1))) {
                timeToGo = 20; //Wait 1 second before re-trying
                return;
            }
            processing = LiquidCrystalData.fromStack(TankTransfers.extract(tankHook.getTank1(), PurifierConfig.RCL_PER_PURIFY.get()));
            timeToGo = PurifierConfig.TICKS_PER_PURIFY.get();
        }
        setChanged();
    }

    private void maybeOutput() {
        if (processing != null) {
            if (!hasFilter()) {
                return;
            }
            int purify = doPurify(processing);
            if (purify < 0) {
                return;
            }
            if (level.getRandom().nextInt(purify) == 0) {
                consumeFilter();
            }
        }
        timeToGo = -1;
        setChanged();
    }

    private void consumeFilter() {
        items.decrStackSize(SLOT, 1);
        ItemStack spentMaterial = new ItemStack(CoreModule.SPENT_FILTER_ITEM.get(), 1);
        inventoryLocator.ejectStack(level, worldPosition, spentMaterial, worldPosition, OrientationTools.HORIZONTAL_DIRECTION_VALUES);
    }

    private int doPurify(@Nonnull ILiquidCrystalData fluidData) {
        if (!tankHook.tank2Present()) {
            return -1; //Wait
        }
        ResourceHandler<FluidResource> outputTank = tankHook.getTank2();
        double purity = fluidData.getPurity();
        double maxPurityToAdd = PurifierConfig.ADDED_PURITY.get() / 100.0;
        double maxPurity = (PurifierConfig.MAX_PURITY.get() + 0.1) / 100.0 * fluidData.getQuality();
        double addedPurity = Math.min(maxPurityToAdd, Math.max(0, maxPurity - purity));

        var result = LiquidCrystalData.fromStack(fluidData.getFluidStack());
        if (addedPurity >= 0.0001) {
            result.setPurity(purity + addedPurity);
        }
        if (!TankTransfers.insertAll(outputTank, result.getFluidStack())) {
            return -1;
        }
        processing = null;
        return addedPurity < 0.0001 ? 100000 : (int) ((maxPurityToAdd - addedPurity) * 40 / maxPurityToAdd + 1);
    }

    private boolean hasFilter() {
        return items.getStackInSlot(SLOT).getItem() == CoreModule.FILTER_MATERIAL_ITEM.get();
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        HolderLookup.Provider provider = level == null ? RegistryAccess.EMPTY : level.registryAccess();
        CompoundTag tagCompound = new CompoundTag();
        tagCompound.putInt("timeToGo", timeToGo);
        if (processing != null) {
            tagCompound.put("processing", LiquidCrystalData.writeStack(processing.getFluidStack(), provider));
        }
        output.store(MapCodec.assumeMapUnsafe(CompoundTag.CODEC), tagCompound);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        HolderLookup.Provider provider = input.lookup();
        CompoundTag tagCompound = input.read(MapCodec.assumeMapUnsafe(CompoundTag.CODEC)).orElseGet(CompoundTag::new);
        timeToGo = tagCompound.getIntOr("timeToGo", 0);
        if (tagCompound.contains("processing")) {
            processing = LiquidCrystalData.fromStack(LiquidCrystalData.readStack(provider, tagCompound.getCompoundOrEmpty("processing")));
        } else {
            processing = null;
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        super.applyImplicitComponents(input);
        items.applyImplicitComponents(input.get(ITEM_INVENTORY));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        items.collectImplicitComponents(builder);
    }

}
