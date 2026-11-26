package mcjty.deepresonance.modules.machines.block;

import mcjty.deepresonance.modules.tank.util.TankTransfers;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
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
import mcjty.rftoolsbase.tools.ManualHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import mcjty.lib.varia.Lazy;
import mcjty.lib.varia.FluidStack;

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

    public static BaseBlock createBlock(Block.Properties properties) {
        return new BaseBlock(
                new BlockBuilder()
                        .properties(properties)
                        .tileEntitySupplier(ValveTileEntity::new)
                .manualEntry(ManualHelper.create("deepresonance:machines/valve"))
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

        Storage<FluidVariant> top = tankHook.getTank1();
        Storage<FluidVariant> bottom = tankHook.getTank2();

        int amount = ValveConfig.RCL_PER_OPERATION.get();
        int maxMb = getMaxMb();
        if (maxMb > 0) {
            amount = Math.min(amount, maxMb - TankTransfers.contents(bottom).getAmount());
        }
        if (amount <= 0) {
            return;
        }
        FluidStack stack = TankTransfers.simulateExtract(top, amount);
        if (!LiquidCrystalData.isValidLiquidCrystalStack(stack)) {
            return;
        }
        ILiquidCrystalData data = LiquidCrystalData.fromStack(stack);
        if (data.getPurity() >= getMinPurity() && data.getStrength() >= getMinStrength()
                && data.getEfficiency() >= getMinEfficiency()) {
            TankTransfers.move(top, bottom, stack);
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
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tagCompound = new CompoundTag();
        tagCompound.putInt("progress", progress);
        output.store(MapCodec.assumeMapUnsafe(CompoundTag.CODEC), tagCompound);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tagCompound = input.read(MapCodec.assumeMapUnsafe(CompoundTag.CODEC)).orElseGet(CompoundTag::new);
        progress = tagCompound.getIntOr("progress", 0);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        super.applyImplicitComponents(input);
        ValveData valveData = input.get(MachinesModule.ITEM_VALVE_DATA.get());
        if  (valveData != null) {
            setData(MachinesModule.VALVE_DATA, valveData);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(MachinesModule.ITEM_VALVE_DATA.get(), getData(MachinesModule.VALVE_DATA));
    }
}
