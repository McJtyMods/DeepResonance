package mcjty.deepresonance.modules.machines.block;

import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.util.config.ValveConfig;
import mcjty.deepresonance.modules.tank.util.DualTankHook;
import mcjty.deepresonance.util.DeepResonanceFluidHelper;
import mcjty.deepresonance.util.TranslationHelper;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class ValveTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(0));

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Laser")
            .containerSupplier((windowId, player) -> new GenericContainer(MachinesModule.VALVE_CONTAINER, windowId, CONTAINER_FACTORY, this)));

    private final DualTankHook tankHook = new DualTankHook(this, Direction.UP, Direction.DOWN);

    private int progress = 0;

    private float minPurity = 1.0f;
    private float minStrength = 1.0f;
    private float minEfficiency = 1.0f;
    private int maxMb = 0;

    public ValveTileEntity() {
        super(MachinesModule.TYPE_VALVE.get());
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(
                new BlockBuilder()
                        .tileEntitySupplier(ValveTileEntity::new)
                        .infoShift(TooltipBuilder.key(TranslationHelper.getTooltipKey("valve")))) {

            @Override
            public RotationType getRotationType() {
                return RotationType.NONE;
            }

            @Override
            protected void createBlockStateDefinition(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
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
    public void tick() {
        if (level != null && !level.isClientSide()) {
            tickServer();
        }
    }

    private void tickServer() {
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
        if (bottom.fill(DeepResonanceFluidHelper.makeLiquidCrystalStack(amt), IFluidHandler.FluidAction.SIMULATE) == amt) {
            ILiquidCrystalData data = DeepResonanceFluidHelper.readCrystalDataFromStack(fluidStack);
            if (data == null) {
                return;
            }
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

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT tagCompound) {
        tagCompound.putInt("progress", progress);

        tagCompound.putFloat("minPurity", minPurity);
        tagCompound.putFloat("minStrength", minStrength);
        tagCompound.putFloat("minEfficiency", minEfficiency);
        tagCompound.putInt("maxMb", maxMb);

        return super.save(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);

        progress = tagCompound.getInt("progress");

        minPurity = tagCompound.getFloat("minPurity");
        minStrength = tagCompound.getFloat("minStrength");
        minEfficiency = tagCompound.getFloat("minEfficiency");
        maxMb = tagCompound.getInt("maxMb");
    }

    public static final Key<Double> PARAM_PURITY = new Key<>("purity", Type.DOUBLE);
    public static final Key<Double> PARAM_STRENGTH = new Key<>("strength", Type.DOUBLE);
    public static final Key<Double> PARAM_EFFICIENCY = new Key<>("efficiency", Type.DOUBLE);
    public static final Key<Integer> PARAM_MAXMB = new Key<>("max_mb", Type.INTEGER);
    @ServerCommand
    public static final Command<?> CMD_SETTINGS = Command.<ValveTileEntity>create("valve.settings",
            (te, player, params) -> {
                te.setMinPurity((float) (double) params.get(PARAM_PURITY));
                te.setMinStrength((float) (double) params.get(PARAM_STRENGTH));
                te.setMinEfficiency((float) (double) params.get(PARAM_EFFICIENCY));
                te.setMaxMb(params.get(PARAM_MAXMB));
            });
}
