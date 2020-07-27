package mcjty.deepresonance.modules.machines.tile;

import com.google.common.base.Preconditions;
import elec332.core.api.registration.RegisteredTileEntity;
import elec332.core.inventory.BasicItemHandler;
import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.fluids.LiquidCrystalData;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.client.PurifierGui;
import mcjty.deepresonance.modules.tank.util.DualTankHook;
import mcjty.deepresonance.setup.FluidRegister;
import mcjty.deepresonance.util.AbstractTileEntity;
import mcjty.deepresonance.util.RegisteredContainer;
import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.SlotDefinition;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 27-7-2020
 */
@RegisteredTileEntity("purifier")
public class PurifierTileEntity extends AbstractTileEntity implements ITickableTileEntity {

    public static final int SLOT = 0;

    private final DualTankHook tankHook = new DualTankHook(this, Direction.UP, Direction.DOWN).allowDuplicates().setTimeout(10);
    private final BasicItemHandler itemHandler = new BasicItemHandler(1) {

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.getItem() == CoreModule.FILTER_MATERIAL_ITEM.get();
        }

    };
    private static final RegisteredContainer<GenericContainer, PurifierGui, PurifierTileEntity> container = new RegisteredContainer<GenericContainer, PurifierGui, PurifierTileEntity>("purifier", 0, factory -> {
        factory.playerSlots(10, 70);
        factory.slot(SlotDefinition.specific(new ItemStack(CoreModule.FILTER_MATERIAL_ITEM.get())), ContainerFactory.CONTAINER_CONTAINER, SLOT, 64, 24);
    }) {

        @Override
        public Object createGui(PurifierTileEntity tile, GenericContainer container, PlayerInventory inventory) {
            return new PurifierGui(tile, container, inventory);
        }

    }.modifyContainer((container, tile) -> container.itemHandler(() -> tile.itemHandler));
    private final LazyOptional<INamedContainerProvider> screenHandler = container.build(this);
    private final LazyOptional<IItemHandler> inventory = LazyOptional.of(() -> itemHandler);

    private int timeToGo = 0;
    private ILiquidCrystalData processing = null;

    @Override
    public void tick() {
        if (Preconditions.checkNotNull(world).isRemote) {
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
            if (!FluidRegister.isValidLiquidCrystalStack(tankHook.getTank1().drain(1, IFluidHandler.FluidAction.SIMULATE))) {
                timeToGo = 20; //Wait 1 second before re-trying
                return;
            }
            processing = LiquidCrystalData.fromStack(tankHook.getTank1().drain(MachinesModule.purifierConfig.rclPerPurify.get(), IFluidHandler.FluidAction.EXECUTE));
            timeToGo = MachinesModule.purifierConfig.ticksPerPurify.get();
        }
        markDirty();
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
            if (Preconditions.checkNotNull(world).rand.nextInt(purify) == 0) {
                consumeFilter();
            }
        }
        timeToGo = -1;
    }

    private void consumeFilter() { //TODO: Auto-eject upgrade
        itemHandler.setStackInSlot(SLOT, new ItemStack(CoreModule.SPENT_FILTER_ITEM.get()));
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
        return itemHandler.getStackInSlot(SLOT).getItem() == CoreModule.FILTER_MATERIAL_ITEM.get();
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putInt("timeToGo", timeToGo);
        if (processing != null) {
            CompoundNBT tag = new CompoundNBT();
            processing.toFluidStack().writeToNBT(tag);
            tagCompound.put("processing", tag);
        }

        itemHandler.writeToNBT(tagCompound);
        return super.write(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        timeToGo = tagCompound.getInt("timeToGo");
        if (tagCompound.hasUniqueId("processing")) {
            processing = LiquidCrystalData.fromStack(FluidStack.loadFluidStackFromNBT(tagCompound.getCompound("processing")));
        } else {
            processing = null;
        }

        itemHandler.deserializeNBT(tagCompound);
        super.read(tagCompound);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, inventory);
        }
        return CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY.orEmpty(cap, screenHandler);
    }

}
