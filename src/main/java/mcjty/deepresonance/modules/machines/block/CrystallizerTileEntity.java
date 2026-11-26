package mcjty.deepresonance.modules.machines.block;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import mcjty.deepresonance.modules.tank.util.TankTransfers;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.world.level.block.Block;
import static mcjty.lib.setup.Registration.ITEM_INVENTORY;
import static mcjty.lib.setup.Registration.ITEM_ENERGY;
import net.minecraft.core.RegistryAccess;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.modules.core.data.LCD;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.data.CrystalizerData;
import mcjty.deepresonance.modules.machines.util.config.CrystallizerConfig;
import mcjty.deepresonance.util.LiquidCrystalData;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.rftoolsbase.tools.ManualHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import mcjty.lib.varia.Lazy;
import mcjty.lib.varia.FluidStack;

import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.container.GenericItemHandler.no;
import static mcjty.lib.container.GenericItemHandler.yes;
import static mcjty.lib.container.SlotDefinition.generic;

public class CrystallizerTileEntity extends TickingTileEntity {

    public static final int SLOT = 0;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(generic().out(), SLOT, 64, 24)
            .playerSlots(10, 70));

    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .itemValid((integer, stack) -> stack.getItem() == CoreModule.RESONATING_CRYSTAL_GENERATED.item().get())
            .insertable(no())
            .extractable(yes())
            .build();
    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final static Function<CrystallizerTileEntity, GenericItemHandler> ITEMS_CAP = tile -> tile.items;

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, CrystallizerConfig.POWER_MAXIMUM.get(), CrystallizerConfig.POWER_PER_TICK_IN.get());
    @Cap(type = CapType.ENERGY)
    private final static Function<CrystallizerTileEntity, GenericEnergyStorage> ENERGY_CAP = tile -> tile.energyStorage;

    @Cap(type = CapType.CONTAINER)
    private static final Function<CrystallizerTileEntity, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<GenericContainer>("Crystalizer")
            .containerSupplier(container(MachinesModule.CRYSTALIZER_CONTAINER, CONTAINER_FACTORY, be))
            .itemHandler(() -> be.items)
            .energyHandler(() -> be.energyStorage)
            .setupSync(be);

    private int progress;   // Clientside only
    private LiquidCrystalData crystalData;
    private Storage<FluidVariant> rclTank;
    private int tankCooldown = 0;

    public CrystallizerTileEntity(BlockPos pos, BlockState state) {
        super(MachinesModule.TYPE_CRYSTALIZER.get(), pos, state);
    }

    public static BaseBlock createBlock(Block.Properties properties) {
        return new BaseBlock(new BlockBuilder()
                .properties(properties)
                .tileEntitySupplier(CrystallizerTileEntity::new)
                .manualEntry(ManualHelper.create("deepresonance:machines/crystallizer"))
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
        if (!canCrystallize()) {
            return;
        }

        int oldProgress = crystalData == null ? 0 : (int) ((crystalData.getAmount() / (float) getRclPerCrystal()) * 100);
        energyStorage.consumeEnergy(CrystallizerConfig.POWER_PER_TICK.get());
        int rclPerCrystal = getRclPerCrystal();
        int drain = CrystallizerConfig.RCL_PER_TICK.get();
        if (crystalData != null) {
            drain = Math.min(drain, rclPerCrystal - crystalData.getAmount());
        }
        if (drain > 0) { //Config can change between ticks
            FluidStack stack = TankTransfers.extract(rclTank, drain);
            LiquidCrystalData data = LiquidCrystalData.fromStack(stack);
            if (crystalData == null) {
                crystalData = data;
            } else {
                crystalData.merge(data);
            }
        }
        if (crystalData != null && crystalData.getAmount() >= rclPerCrystal) {
            ResonatingCrystalTileEntity crystal = new ResonatingCrystalTileEntity(worldPosition, CoreModule.RESONATING_CRYSTAL_GENERATED.block().get().defaultBlockState());
            crystal.setEfficiency(crystalData.getEfficiency() * 100f);
            crystal.setPurity(crystalData.getPurity() * 100f);
            crystal.setStrength(crystalData.getStrength() * 100f);
            crystal.setPower(100f);
            crystalData = null;
            items.setStackInSlot(SLOT, CoreModule.RESONATING_CRYSTAL_GENERATED.block().get().createStack(crystal));
            setChanged();
        }
        int newProgress = crystalData == null ? 0 : (int) ((crystalData.getAmount() / (float) getRclPerCrystal()) * 100);
        if (oldProgress != newProgress) {
            markDirtyClient();
        }
    }

    public int getMaxPower() {
        return energyStorage.getMaxEnergyStored();
    }

    public int getCurrentPower() {
        return energyStorage.getEnergyStored();
    }

    private boolean canCrystallize() {
        if (tankCooldown > 0) {
            tankCooldown--;
        }
        if (rclTank == null && !checkTank()) {
            return false;
        }

        if (energyStorage.getEnergyStored() < CrystallizerConfig.POWER_PER_TICK.get()) {
            return false;
        }

        if (hasCrystal()) {
            return false;
        }

        FluidStack fluidStack = TankTransfers.simulateExtract(rclTank, CrystallizerConfig.RCL_PER_TICK.get());
        if (fluidStack.isEmpty() || fluidStack.getAmount() < 1) {
            return false;
        }

        return LiquidCrystalData.isValidLiquidCrystalStack(fluidStack);
    }

    private boolean checkTank() {
        rclTank = null;
        if (tankCooldown <= 0) {
            tankCooldown = 21;
            BlockEntity tile = level.getBlockEntity(worldPosition.below());
            if (tile != null) {
                rclTank = FluidStorage.SIDED.find(level, worldPosition.below(), null);
                return rclTank != null;
            }
        }
        return false;
    }

    public boolean hasCrystal() {
        return !items.getStackInSlot(SLOT).isEmpty();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        HolderLookup.Provider provider = input.lookup();
        CompoundTag tag = input.read(MapCodec.assumeMapUnsafe(CompoundTag.CODEC)).orElseGet(CompoundTag::new);
        if (tag.contains("amount") && tag.contains("lcd")) {
            int amount = tag.getIntOr("amount", 0);
            LCD lcd = LCD.CODEC.parse(NbtOps.INSTANCE, tag.get("lcd")).result().orElseThrow(() -> new IllegalStateException("Invalid LCD"));
            FluidStack stack = LiquidCrystalData.makeLiquidCrystalStack(amount, lcd);
            crystalData = LiquidCrystalData.fromStack(stack);
        }
        energyStorage.load(tag, "energy", provider);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        HolderLookup.Provider provider = level == null ? RegistryAccess.EMPTY : level.registryAccess();
        CompoundTag tag = new CompoundTag();
        if (crystalData != null) {
            tag.putInt("amount", crystalData.getAmount());
            LCD lcd = crystalData.getLCD();
            tag.put("lcd", LCD.CODEC.encodeStart(NbtOps.INSTANCE, lcd).result().orElseThrow(() -> new IllegalStateException("Invalid LCD")));
        }
        energyStorage.save(tag, "energy", provider);
        output.store(MapCodec.assumeMapUnsafe(CompoundTag.CODEC), tag);
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tagCompound, HolderLookup.Provider provider) {
        progress = tagCompound.getIntOr("progress", 0);
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tagCompound, HolderLookup.Provider provider) {
        progress  = crystalData == null ? 0 : (int) ((crystalData.getAmount() / (float) getRclPerCrystal()) * 100);
        tagCompound.putInt("progress", progress);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (crystalData != null) {
            components.set(MachinesModule.ITEM_CRYSTALIZER_DATA.get(), new CrystalizerData(crystalData.getAmount()));
            components.set(CoreModule.ITEM_LCD_DATA.get(), crystalData.getLCD());
        }

        energyStorage.collectImplicitComponents(components);
        items.collectImplicitComponents(components);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        super.applyImplicitComponents(input);
        CrystalizerData cdata = input.get(MachinesModule.ITEM_CRYSTALIZER_DATA.get());
        LCD lcd = input.get(CoreModule.ITEM_LCD_DATA.get());
        if (cdata != null && lcd != null) {
            FluidStack stack = LiquidCrystalData.makeLiquidCrystalStack(cdata.amount(), lcd);
            crystalData = LiquidCrystalData.fromStack(stack);
        }
        energyStorage.applyImplicitComponents(input.get(ITEM_ENERGY.get()));
        items.applyImplicitComponents(input.get(ITEM_INVENTORY.get()));
    }

    private static int getRclPerCrystal() {
        return CrystallizerConfig.RCL_PER_CRYSTAL.get();
    }

    // Client side
    public int getProgress() {
        return progress;
    }
}
