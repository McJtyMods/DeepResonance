package mcjty.deepresonance.modules.machines.block;

import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.util.config.PurifierConfig;
import mcjty.deepresonance.modules.tank.util.DualTankHook;
import mcjty.deepresonance.util.DeepResonanceFluidHelper;
import mcjty.deepresonance.util.TranslationHelper;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.InventoryLocator;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

import static mcjty.lib.container.SlotDefinition.generic;

public class PurifierTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public static final int SLOT = 0;

    private final DualTankHook tankHook = new DualTankHook(this, Direction.UP, Direction.DOWN).allowDuplicates().setTimeout(10);

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(generic().in().out(), SLOT, 64, 24)
            .playerSlots(10, 70));

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final NoDirectionItemHander items = createItemHandler();

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Purifier")
            .containerSupplier((windowId,player) -> new GenericContainer(MachinesModule.PURIFIER_CONTAINER, windowId, CONTAINER_FACTORY, this))
            .itemHandler(() -> items));

    // Cache for the inventory used to put the spent filter material in.
    private InventoryLocator inventoryLocator = new InventoryLocator();

    private int timeToGo = 0;
    private ILiquidCrystalData processing = null;

    public PurifierTileEntity() {
        super(MachinesModule.TYPE_PURIFIER.get());
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(
                new BlockBuilder()
                        .tileEntitySupplier(PurifierTileEntity::new)
                        .infoShift(TooltipBuilder.key(TranslationHelper.getTooltipKey("purifier")))) {

            @Override
            public RotationType getRotationType() {
                return RotationType.HORIZROTATION;
            }
        };
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
            processing = DeepResonanceFluidHelper.readCrystalDataFromStack(tankHook.getTank1().drain(PurifierConfig.RCL_PER_PURIFY.get(), IFluidHandler.FluidAction.EXECUTE));
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
            if (level.random.nextInt(purify) == 0) {
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
        IFluidHandler outputTank = tankHook.getTank2();
        if (outputTank.fill(fluidData.getFluidStack(), IFluidHandler.FluidAction.SIMULATE) != fluidData.getAmount()) {
            return -1; //Wait
        }
        double purity = fluidData.getPurity();
        double maxPurityToAdd = PurifierConfig.ADDED_PURITY.get() / 100.0;
        double addedPurity = maxPurityToAdd;
        double maxPurity = (PurifierConfig.MAX_PURITY.get() + .1) / 100.0;
        maxPurity *= fluidData.getQuality();
        if (purity + addedPurity > maxPurity) {
            addedPurity = maxPurity - purity;
            if (addedPurity < 0.0001) {
                outputTank.fill(fluidData.getFluidStack(), IFluidHandler.FluidAction.EXECUTE);
                return 100000;
            }
        }

        purity += addedPurity;
        fluidData.setPurity(purity);
        outputTank.fill(fluidData.getFluidStack(), IFluidHandler.FluidAction.EXECUTE);
        return (int) ((maxPurityToAdd - addedPurity) * 40 / maxPurityToAdd + 1);
    }

    private boolean hasFilter() {
        return items.getStackInSlot(SLOT).getItem() == CoreModule.FILTER_MATERIAL_ITEM.get();
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT tagCompound) {
        tagCompound.putInt("timeToGo", timeToGo);
        if (processing != null) {
            CompoundNBT tag = new CompoundNBT();
            processing.getFluidStack().writeToNBT(tag);
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
        };
    }
}
