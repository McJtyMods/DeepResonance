package mcjty.deepresonance.modules.machines.block;

import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.modules.machines.MachinesModule;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

import static mcjty.lib.api.container.DefaultContainerProvider.container;

public class ValveTileEntity extends TickingTileEntity {

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(0)
            .playerSlots(10, 70));

    @Cap(type = CapType.CONTAINER)
    private final Lazy<MenuProvider> screenHandler = Lazy.of(() -> new DefaultContainerProvider<GenericContainer>("Valve")
            .containerSupplier(container(MachinesModule.VALVE_CONTAINER, CONTAINER_FACTORY,this))
            .setupSync(this));

    private final DualTankHook tankHook = new DualTankHook(this, Direction.UP, Direction.DOWN);

    private int progress = 0;

    @GuiValue
    public static final Value<?, Float> VALUE_MINPURITY = Value.create("minPurity", Type.FLOAT, ValveTileEntity::getMinPurity, ValveTileEntity::setMinPurity);
    private float minPurity = 1.0f;

    @GuiValue
    public static final Value<?, Float> VALUE_STRENGTH = Value.create("minStrength", Type.FLOAT, ValveTileEntity::getMinStrength, ValveTileEntity::setMinStrength);
    private float minStrength = 1.0f;

    @GuiValue
    public static final Value<?, Float> VALUE_EFFICIENCY = Value.create("minEfficiency", Type.FLOAT, ValveTileEntity::getMinEfficiency, ValveTileEntity::setMinEfficiency);
    private float minEfficiency = 1.0f;

    @GuiValue
    public static final Value<?, Integer> VALUE_MAXMB = Value.create("maxMb", Type.INTEGER, ValveTileEntity::getMaxMb, ValveTileEntity::setMaxMb);
    private int maxMb = 0;

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
            if (data.getPurity() < minPurity) {
                return;
            }
            if (data.getStrength() < minStrength) {
                return;
            }
            if (data.getEfficiency() < minEfficiency) {
                return;
            }

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
        return maxMb;
    }

    public void setMaxMb(int maxMb) {
        this.maxMb = maxMb;
        setChanged();
    }

    public float getMinEfficiency() {
        return minEfficiency;
    }

    public void setMinEfficiency(float minEfficiency) {
        this.minEfficiency = minEfficiency;
        setChanged();
    }

    public float getMinPurity() {
        return minPurity;
    }

    public void setMinPurity(float minPurity) {
        this.minPurity = minPurity;
        setChanged();
    }

    public float getMinStrength() {
        return minStrength;
    }

    public void setMinStrength(float minStrength) {
        this.minStrength = minStrength;
        setChanged();
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        tagCompound.putInt("progress", progress);

        tagCompound.putFloat("minPurity", minPurity);
        tagCompound.putFloat("minStrength", minStrength);
        tagCompound.putFloat("minEfficiency", minEfficiency);
        tagCompound.putInt("maxMb", maxMb);

        super.saveAdditional(tagCompound);
    }

    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);

        progress = tagCompound.getInt("progress");

        minPurity = tagCompound.getFloat("minPurity");
        minStrength = tagCompound.getFloat("minStrength");
        minEfficiency = tagCompound.getFloat("minEfficiency");
        maxMb = tagCompound.getInt("maxMb");
    }
}
