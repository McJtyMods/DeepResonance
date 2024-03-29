package mcjty.deepresonance.modules.machines.block;

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
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.container.GenericItemHandler.match;
import static mcjty.lib.container.SlotDefinition.specific;

public class PurifierTileEntity extends TickingTileEntity {

    public static final int SLOT = 0;

    private final DualTankHook tankHook = new DualTankHook(this, Direction.UP, Direction.DOWN).allowDuplicates().setTimeout(10);

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(specific(CoreModule.FILTER_MATERIAL_ITEM.get()).in().out(), SLOT, 64, 24)
            .playerSlots(10, 70));

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .itemValid(match(CoreModule.FILTER_MATERIAL_ITEM))
            .build();

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Purifier")
            .containerSupplier(container(MachinesModule.PURIFIER_CONTAINER, CONTAINER_FACTORY,this))
            .itemHandler(() -> items)
            .setupSync(this));

    // Cache for the inventory used to put the spent filter material in.
    private final InventoryLocator inventoryLocator = new InventoryLocator();

    private int timeToGo = 0;
    private ILiquidCrystalData processing = null;

    public PurifierTileEntity() {
        super(MachinesModule.TYPE_PURIFIER.get());
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(
                new BlockBuilder()
                        .tileEntitySupplier(PurifierTileEntity::new)
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
            if (!LiquidCrystalData.isValidLiquidCrystalStack(tankHook.getTank1().drain(1, IFluidHandler.FluidAction.SIMULATE))) {
                timeToGo = 20; //Wait 1 second before re-trying
                return;
            }
            processing = LiquidCrystalData.fromStack(tankHook.getTank1().drain(PurifierConfig.RCL_PER_PURIFY.get(), IFluidHandler.FluidAction.EXECUTE));
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
        int tryAmount = outputTank.fill(fluidData.getFluidStack(), IFluidHandler.FluidAction.SIMULATE);
        if (tryAmount != fluidData.getAmount()) {
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
                processing = null;
                return 100000;
            }
        }

        purity += addedPurity;
        fluidData.setPurity(purity);
        outputTank.fill(fluidData.getFluidStack(), IFluidHandler.FluidAction.EXECUTE);
        processing = null;
        return (int) ((maxPurityToAdd - addedPurity) * 40 / maxPurityToAdd + 1);
    }

    private boolean hasFilter() {
        return items.getStackInSlot(SLOT).getItem() == CoreModule.FILTER_MATERIAL_ITEM.get();
    }

    @Override
    public void saveAdditional(@Nonnull CompoundNBT tagCompound) {
        tagCompound.putInt("timeToGo", timeToGo);
        if (processing != null) {
            CompoundNBT tag = new CompoundNBT();
            processing.getFluidStack().writeToNBT(tag);
            tagCompound.put("processing", tag);
        }

        super.saveAdditional(tagCompound);
    }

    @Override
    public void load(CompoundNBT tagCompound) {
        timeToGo = tagCompound.getInt("timeToGo");
        if (tagCompound.contains("processing")) {
            processing = LiquidCrystalData.fromStack(FluidStack.loadFluidStackFromNBT(tagCompound.getCompound("processing")));
        } else {
            processing = null;
        }

        super.load(tagCompound);
    }

}
