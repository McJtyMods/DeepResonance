package mcjty.deepresonance.modules.machines.tile;

import com.google.common.base.Preconditions;
import elec332.core.inventory.BasicItemHandler;
import elec332.core.inventory.ItemEjector;
import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.client.gui.PurifierGui;
import mcjty.deepresonance.modules.tank.util.DualTankHook;
import mcjty.deepresonance.util.DeepResonanceFluidHelper;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.container.SlotDefinition.generic;

/**
 * Created by Elec332 on 27-7-2020
 */
public class TileEntityPurifier extends AbstractTileEntity implements ITickableTileEntity {

    public static final int SLOT = 0;

    private final DualTankHook tankHook = new DualTankHook(this, Direction.UP, Direction.DOWN).allowDuplicates().setTimeout(10);
    private final ItemEjector ejector = new ItemEjector();

    private static final RegisteredContainer<GenericContainer, PurifierGui, TileEntityPurifier> container = new RegisteredContainer<GenericContainer, PurifierGui, TileEntityPurifier>("purifier", 1, factory -> {
        factory.playerSlots(10, 70);
        factory.slot(generic(), ContainerFactory.CONTAINER_CONTAINER, SLOT, 64, 24);
    }) {

        @Override
        public Object createGui(TileEntityPurifier tile, GenericContainer container, PlayerInventory inventory) {
            return new PurifierGui(tile, container, inventory);
        }

    };

    private int timeToGo = 0;
    private ILiquidCrystalData processing = null;

    public TileEntityPurifier() {
        super(MachinesModule.TYPE_PURIFIER.get(), new BasicItemHandler(1) {

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() == CoreModule.FILTER_MATERIAL_ITEM.get();
            }

            @Override
            protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
                return 1;
            }

        });
    }

    @Nullable
    @Override
    protected LazyOptional<INamedContainerProvider> createScreenHandler() {
        return container.build(this);
    }

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
            if (!DeepResonanceFluidHelper.isValidLiquidCrystalStack(tankHook.getTank1().drain(1, IFluidHandler.FluidAction.SIMULATE))) {
                timeToGo = 20; //Wait 1 second before re-trying
                return;
            }
            processing = DeepResonanceFluidHelper.readCrystalDataFromStack(tankHook.getTank1().drain(MachinesModule.purifierConfig.rclPerPurify.get(), IFluidHandler.FluidAction.EXECUTE));
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
        markDirty();
    }

    private void consumeFilter() {
        ItemStack stack = new ItemStack(CoreModule.SPENT_FILTER_ITEM.get());
        if (true) { //TODO: Auto-eject upgrade
            ejector.eject(getLevel(), getPos(), Direction.Plane.HORIZONTAL, stack);
            stack = ItemStack.EMPTY;
        }
        itemHandler.setStackInSlot(SLOT, stack);
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

        return super.write(tagCompound);
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

}
