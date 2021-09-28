package mcjty.deepresonance.modules.machines.tile;

import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.tank.util.DualTankHook;
import mcjty.deepresonance.util.DeepResonanceFluidHelper;
import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.generic;

public class TileEntityPurifier extends GenericTileEntity implements ITickableTileEntity {

    public static final int SLOT = 0;

    private final DualTankHook tankHook = new DualTankHook(this, Direction.UP, Direction.DOWN).allowDuplicates().setTimeout(10);

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(generic().out(), CONTAINER_CONTAINER, SLOT, 64, 24)
            .playerSlots(10, 70));

    private final NoDirectionItemHander items = createItemHandler();
    private final LazyOptional<AutomationFilterItemHander> itemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items));

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Purifier")
            .containerSupplier((windowId,player) -> new GenericContainer(MachinesModule.PURIFIER_CONTAINER.get(), windowId, CONTAINER_FACTORY.get(), getBlockPos(), TileEntityPurifier.this))
            .itemHandler(() -> items));


    private int timeToGo = 0;
    private ILiquidCrystalData processing = null;

    public TileEntityPurifier() {
        super(MachinesModule.TYPE_PURIFIER.get());
    }


    @Override
    public void tick() {
        if (level.isClientSide()) {
            return;
        }
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
            if (!DeepResonanceFluidHelper.isValidLiquidCrystalStack(tankHook.getTank1().drain(1, IFluidHandler.FluidAction.SIMULATE))) {
                timeToGo = 20; //Wait 1 second before re-trying
                return;
            }
            processing = DeepResonanceFluidHelper.readCrystalDataFromStack(tankHook.getTank1().drain(MachinesModule.purifierConfig.rclPerPurify.get(), IFluidHandler.FluidAction.EXECUTE));
            timeToGo = MachinesModule.purifierConfig.ticksPerPurify.get();
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
            if (level.random.nextInt(purify) == 0) {
                consumeFilter();
            }
        }
        timeToGo = -1;
        setChanged();
    }

    private void consumeFilter() {
        ItemStack stack = new ItemStack(CoreModule.SPENT_FILTER_ITEM.get());
        if (true) { //TODO: Auto-eject upgrade
            // @todo 1.16
//            ejector.eject(getLevel(), getPos(), Direction.Plane.HORIZONTAL, stack);
            stack = ItemStack.EMPTY;
        }
        items.setStackInSlot(SLOT, stack);
    }

    private int doPurify(@Nonnull ILiquidCrystalData fluidData) {
        if (!tankHook.tank2Present()) {
            return -1; //Wait
        }
        IFluidHandler outputTank = tankHook.getTank2();
        if (outputTank.fill(fluidData.toFluidStack(), IFluidHandler.FluidAction.SIMULATE) != fluidData.getAmount()) {
            return -1; //Wait
        }
        float purity = fluidData.getPurity();
        float maxPurityToAdd = MachinesModule.purifierConfig.addedPurity.get() / 100.0f;
        float addedPurity = maxPurityToAdd;
        float maxPurity = (MachinesModule.purifierConfig.maxPurity.get() + .1f) / 100.0f;
        maxPurity *= fluidData.getQuality();
        if (purity + addedPurity > maxPurity) {
            addedPurity = maxPurity - purity;
            if (addedPurity < 0.0001f) {
                outputTank.fill(fluidData.toFluidStack(), IFluidHandler.FluidAction.EXECUTE);
                return 100000;
            }
        }

        purity += addedPurity;
        fluidData.setPurity(purity);
        outputTank.fill(fluidData.toFluidStack(), IFluidHandler.FluidAction.EXECUTE);
        return (int) ((maxPurityToAdd - addedPurity) * 40 / maxPurityToAdd + 1);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted") //Shaddap
    private boolean hasFilter() {
        return items.getStackInSlot(SLOT).getItem() == CoreModule.FILTER_MATERIAL_ITEM.get();
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        tagCompound.putInt("timeToGo", timeToGo);
        if (processing != null) {
            CompoundNBT tag = new CompoundNBT();
            processing.toFluidStack().writeToNBT(tag);
            tagCompound.put("processing", tag);
        }

        return super.save(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        timeToGo = tagCompound.getInt("timeToGo");
        if (tagCompound.contains("processing")) {
            processing = DeepResonanceFluidHelper.readCrystalDataFromStack(FluidStack.loadFluidStackFromNBT(tagCompound.getCompound("processing")));
        } else {
            processing = null;
        }

        super.read(tagCompound);
    }

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() == CoreModule.FILTER_MATERIAL_ITEM.get();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
        }
        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return screenHandler.cast();
        }
        return super.getCapability(cap, facing);
    }
}
