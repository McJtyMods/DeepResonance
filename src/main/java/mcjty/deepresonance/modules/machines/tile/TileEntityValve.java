package mcjty.deepresonance.modules.machines.tile;

import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.client.gui.ValveGui;
import mcjty.deepresonance.modules.tank.util.DualTankHook;
import mcjty.deepresonance.util.AbstractTileEntity;
import mcjty.deepresonance.util.DeepResonanceFluidHelper;
import mcjty.deepresonance.util.RegisteredContainer;
import mcjty.lib.bindings.DefaultValue;
import mcjty.lib.bindings.IValue;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 25-7-2020
 */
public class TileEntityValve extends AbstractTileEntity implements ITickableTileEntity {

    public static final String CMD_SETTINGS = "valve.settings";

    public static final Key<Double> PARAM_PURITY = new Key<>("purity", Type.DOUBLE);
    public static final Key<Double> PARAM_STRENGTH = new Key<>("strength", Type.DOUBLE);
    public static final Key<Double> PARAM_EFFICIENCY = new Key<>("efficiency", Type.DOUBLE);
    public static final Key<Integer> PARAM_MAXMB = new Key<>("max_mb", Type.INTEGER);

    private static final RegisteredContainer<GenericContainer, ValveGui, TileEntityValve> container = new RegisteredContainer<GenericContainer, ValveGui, TileEntityValve>("valve", 0, factory -> factory.playerSlots(10, 70)) {

        @Override
        public Object createGui(TileEntityValve tile, GenericContainer container, PlayerInventory inventory) {
            return new ValveGui(tile, container, inventory);
        }

    };
    private final DualTankHook tankHook = new DualTankHook(this, Direction.UP, Direction.DOWN);

    private int progress = 0;

    private float minPurity = 1.0f;
    private float minStrength = 1.0f;
    private float minEfficiency = 1.0f;
    private int maxMb = 0;

    public TileEntityValve() {
        super(MachinesModule.TYPE_VALVE.get());
    }

    @Nullable
    @Override
    protected LazyOptional<INamedContainerProvider> createScreenHandler() {
        return container.build(this);
    }

    @Override
    public IValue<?>[] getValues() {
        return new IValue[]{
                new DefaultValue<>(VALUE_RSMODE, this::getRSModeInt, this::setRSModeInt),
        };
    }

    @Override
    protected boolean needsRedstoneMode() {
        return true;
    }

    @Override
    public void tick() {
        if (world != null && !world.isRemote) {
            tickServer();
        }
    }

    private void tickServer() {
        if (!isMachineEnabled()) {
            return;
        }

        progress--;
        markDirty();
        if (progress > 0) {
            return;
        }
        progress = MachinesModule.valveConfig.ticksPerOperation.get();

        if (!tankHook.checkTanks()) {
            return;
        }

        IFluidHandler top = tankHook.getTank1();
        IFluidHandler bottom = tankHook.getTank2();

        int rcl = MachinesModule.valveConfig.rclPerOperation.get();
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
        markDirty();
    }

    public float getMinEfficiency() {
        return minEfficiency;
    }

    public void setMinEfficiency(float minEfficiency) {
        this.minEfficiency = minEfficiency;
        markDirty();
    }

    public float getMinPurity() {
        return minPurity;
    }

    public void setMinPurity(float minPurity) {
        this.minPurity = minPurity;
        markDirty();
    }

    public float getMinStrength() {
        return minStrength;
    }

    public void setMinStrength(float minStrength) {
        this.minStrength = minStrength;
        markDirty();
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putInt("progress", progress);

        tagCompound.putFloat("minPurity", minPurity);
        tagCompound.putFloat("minStrength", minStrength);
        tagCompound.putFloat("minEfficiency", minEfficiency);
        tagCompound.putInt("maxMb", maxMb);

        return super.write(tagCompound);
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

    @Override
    public boolean execute(PlayerEntity playerMP, String command, TypedMap params) {
        boolean rc = super.execute(playerMP, command, params);
        if (rc) {
            return true;
        }
        if (CMD_SETTINGS.equals(command)) {
            double purity = params.get(PARAM_PURITY);
            double strength = params.get(PARAM_STRENGTH);
            double efficiency = params.get(PARAM_EFFICIENCY);
            int maxMb = params.get(PARAM_MAXMB);
            setMinPurity((float) purity);
            setMinStrength((float) strength);
            setMinEfficiency((float) efficiency);
            setMaxMb(maxMb);
            return true;
        }
        return false;
    }

}
