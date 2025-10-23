package mcjty.deepresonance.modules.machines.block;

import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.data.ValveData;
import mcjty.deepresonance.modules.machines.util.config.ValveConfig;
import mcjty.deepresonance.modules.tank.util.DualTankHook;
import mcjty.deepresonance.util.LiquidCrystalData;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.bindings.Value;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.typed.Type;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.container;

public class ValveTileEntity extends TickingTileEntity {

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(0)
            .playerSlots(10, 70));

    @Cap(type = CapType.CONTAINER)
    private static final Function<ValveTileEntity, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<GenericContainer>("Valve")
            .containerSupplier(container(MachinesModule.VALVE_CONTAINER, CONTAINER_FACTORY, be))
            .data(MachinesModule.VALVE_DATA, ValveData.STREAM_CODEC, ValveData.CODEC)
            .setupSync(be);

    private final DualTankHook tankHook = new DualTankHook(this, Direction.UP, Direction.DOWN);

    private int progress = 0;

    @GuiValue
    public static final Value<?, Float> VALUE_MINPURITY = Value.create("minPurity", Type.FLOAT, ValveTileEntity::getMinPurity, ValveTileEntity::setMinPurity);
    
    @GuiValue
    public static final Value<?, Float> VALUE_STRENGTH = Value.create("minStrength", Type.FLOAT, ValveTileEntity::getMinStrength, ValveTileEntity::setMinStrength);

    @GuiValue
    public static final Value<?, Float> VALUE_EFFICIENCY = Value.create("minEfficiency", Type.FLOAT, ValveTileEntity::getMinEfficiency, ValveTileEntity::setMinEfficiency);

    @GuiValue
    public static final Value<?, Integer> VALUE_MAXMB = Value.create("maxMb", Type.INTEGER, ValveTileEntity::getMaxMb, ValveTileEntity::setMaxMb);

    public ValveTileEntity(BlockPos pos, BlockState state) {
        super(MachinesModule.TYPE_VALVE.get(), pos, state);
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(
                new BlockBuilder()
                        .tileEntitySupplier(ValveTileEntity::new)
                        .info(TooltipBuilder.key("message.deepresonance.shiftmessage"))
                        .infoShift(TooltipBuilder.header())) {

            @Override
            public RotationType getRotationType() {
                return RotationType.NONE;
            }

            @Override
            protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
                super.createBlockStateDefinition(builder);
                builder.add();
            }
        };
    }

    @Override
    protected boolean needsRedstoneMode() {
        return true;
    }

    @Override
    public void tickServer() {
        if (!isMachineEnabled()) {
            return;
        }

        progress--;
        setChanged();
        if (progress > 0) {
            return;
        }
        progress = ValveConfig.TICKS_PER_OPERATION.get();

        if (!tankHook.checkTanks()) {
            return;
        }

        IFluidHandler top = tankHook.getTank1();
        IFluidHandler bottom = tankHook.getTank2();

        int rcl = ValveConfig.RCL_PER_OPERATION.get();
        FluidStack fluidStack = top.drain(rcl, IFluidHandler.FluidAction.SIMULATE);
        if (fluidStack.isEmpty()) {
            return;
        }
        int amt = fluidStack.getAmount();
        if (bottom.fill(LiquidCrystalData.makeLiquidCrystalStack(amt), IFluidHandler.FluidAction.SIMULATE) == amt) {
            ILiquidCrystalData data = LiquidCrystalData.fromStack(fluidStack);
            if (data.getPurity() < getMinPurity()) {
                return;
            }
            if (data.getStrength() < getMinStrength()) {
                return;
            }
            if (data.getEfficiency() < getMinEfficiency()) {
                return;
            }

            int maxMb = getMaxMb();
            if (maxMb > 0) {
                // We have to check maximum volume
                int fluidAmount = bottom.getFluidInTank(0).getAmount();
                if (fluidAmount < maxMb) {
                    int toDrain = Math.min(maxMb - fluidAmount, rcl);
                    fluidStack = top.drain(toDrain, IFluidHandler.FluidAction.EXECUTE);
                    bottom.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                }
            } else {
                fluidStack = top.drain(rcl, IFluidHandler.FluidAction.EXECUTE);
                bottom.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }

    public int getMaxMb() {
        return getData(MachinesModule.VALVE_DATA).maxMb();
    }

    public void setMaxMb(int maxMb) {
        setData(MachinesModule.VALVE_DATA, getData(MachinesModule.VALVE_DATA).withMaxMb(maxMb));
    }

    public float getMinEfficiency() {
        return getData(MachinesModule.VALVE_DATA).minEfficiency();
    }

    public void setMinEfficiency(float minEfficiency) {
        setData(MachinesModule.VALVE_DATA, getData(MachinesModule.VALVE_DATA).withMinEfficiency(minEfficiency));
    }

    public float getMinPurity() {
        return getData(MachinesModule.VALVE_DATA).minPurity();
    }

    public void setMinPurity(float minPurity) {
        setData(MachinesModule.VALVE_DATA, getData(MachinesModule.VALVE_DATA).withMinPurity(minPurity));
    }

    public float getMinStrength() {
        return getData(MachinesModule.VALVE_DATA).minStrength();
    }

    public void setMinStrength(float minStrength) {
        setData(MachinesModule.VALVE_DATA, getData(MachinesModule.VALVE_DATA).withMinStrength(minStrength));
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound, HolderLookup.Provider provider) {
        super.saveAdditional(tagCompound, provider);
        tagCompound.putInt("progress", progress);
    }

    @Override
    public void loadAdditional(CompoundTag tagCompound, HolderLookup.Provider provider) {
        super.loadAdditional(tagCompound, provider);
        progress = tagCompound.getInt("progress");
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        ValveData valveData = input.get(MachinesModule.ITEM_VALVE_DATA);
        if  (valveData != null) {
            setData(MachinesModule.VALVE_DATA, valveData);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(MachinesModule.ITEM_VALVE_DATA, getData(MachinesModule.VALVE_DATA));
    }
}
