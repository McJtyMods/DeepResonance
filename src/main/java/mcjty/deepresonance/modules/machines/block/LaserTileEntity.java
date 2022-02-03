package mcjty.deepresonance.modules.machines.block;

import com.google.common.collect.Lists;
import mcjty.deepresonance.api.infusion.InfusionBonus;
import mcjty.deepresonance.api.laser.ILens;
import mcjty.deepresonance.api.laser.ILensMirror;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.data.InfusingBonus;
import mcjty.deepresonance.modules.machines.data.InfusionBonusRegistry;
import mcjty.deepresonance.modules.machines.util.config.LaserConfig;
import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.deepresonance.modules.tank.data.TankBlob;
import mcjty.deepresonance.util.DeepResonanceFluidHelper;
import mcjty.deepresonance.util.LiquidCrystalData;
import mcjty.deepresonance.util.TranslationHelper;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.Collection;

import static mcjty.deepresonance.modules.machines.data.InfusionBonusRegistry.COLOR_RED;
import static mcjty.deepresonance.modules.machines.data.InfusionBonusRegistry.COLOR_YELLOW;
import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.container.GenericItemHandler.notslot;
import static mcjty.lib.container.GenericItemHandler.yes;
import static mcjty.lib.container.SlotDefinition.generic;

public class LaserTileEntity extends TickingTileEntity {

    public static final int SLOT_CRYSTAL = 0;
    public static final int SLOT_CATALYST = 1;
    private static final int SLOT_ACTIVE_CATALYST = 2;

    private final Collection<BlockPos> laserBeam = Lists.newArrayList();
    private int crystalCountdown = 0;
    private int lensCountdown = 0;
    private int progressCounter = 0;

    // Transient
    private int tickCounter = 10;

    @GuiValue
    private float crystalLiquid = 0;

    private int color = 0;          // 0 means not active, > 0 means a color laser

    private float efficiency = 0;
    private InfusingBonus activeBonus = InfusingBonus.EMPTY;
    private LazyOptional<ILens> lens;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(3)
            .slot(generic().in().out(), SLOT_CRYSTAL, 154, 48)
            .slot(generic().in().out(), SLOT_CATALYST, 21, 8)
            .slot(generic().out(), SLOT_ACTIVE_CATALYST, 21, 48)
            .playerSlots(10, 70));

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .itemValid((slot, stack) -> {
                if (slot == SLOT_CRYSTAL) {
                    return isCrystalItem(stack.getItem());
                }
                if (slot == SLOT_CATALYST) {
                    return !isCrystalItem(stack.getItem());
                }
                return false;
            })
            .insertable(notslot(SLOT_ACTIVE_CATALYST))
            .extractable(yes())
            .build();

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, LaserConfig.POWER_MAXIMUM.get(), LaserConfig.POWER_PER_TICK_IN.get());

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Laser")
            .containerSupplier(container(MachinesModule.LASER_CONTAINER, CONTAINER_FACTORY, this))
            .itemHandler(() -> items)
            .energyHandler(() -> energyStorage)
            .setupSync(this));

    public LaserTileEntity() {
        super(MachinesModule.TYPE_LASER.get());
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder().tileEntitySupplier(LaserTileEntity::new).infoShift(TooltipBuilder.key(TranslationHelper.getTooltipKey("laser")))) {

            @Override
            public RotationType getRotationType() {
                return RotationType.HORIZROTATION;
            }

            @Override
            protected void createBlockStateDefinition(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
                super.createBlockStateDefinition(builder);
                builder.add();
            }

        };
    }

    // @todo 1.16
//    @Override
//    protected void dropInventory() {
//        for (int i = 0; i < 2; i++) { //Don't drop item that is being processed
//            WorldHelper.dropStack(getLevel(), getPos(), itemHandler.getStackInSlot(i));
//        }
//        itemHandler.clear();
//    }

    private static boolean isCrystalItem(Item item) {
        return item == CoreModule.RESONATING_CRYSTAL_GENERATED_ITEM.get() ||
                item == CoreModule.RESONATING_CRYSTAL_GENERATED_EMPTY_ITEM.get() ||
                item == CoreModule.RESONATING_CRYSTAL_NATURAL_ITEM.get() ||
                item == CoreModule.RESONATING_CRYSTAL_NATURAL_EMPTY_ITEM.get();
    }

    @Override
    protected void tickServer() {
        tickCounter--;
        if (tickCounter > 0) {
            return;
        }
        tickCounter = 10;

        checkCrystal();

        if (powerLevel == 0) {
            changeColor(0);
            return;
        }

        ItemStack stack = items.getStackInSlot(SLOT_CATALYST);
        InfusingBonus bonus = getInfusingBonus(stack);
        if (bonus == null) {
            changeColor(0);
            return;
        }

        if (getCurrentPower() < LaserConfig.RFUSE_PER_CATALYST.get()) {
            changeColor(0);
            return;
        }

        if (crystalLiquid < LaserConfig.RCL_PER_CATALYST.get()) {
            changeColor(0);
            return;
        }

        BlockPos tankCoordinate = findLens();
        if (tankCoordinate != null) {
            changeColor(bonus.getColor());
        } else {
            changeColor(0);
            return;
        }

        progressCounter--;
        setChanged();
        if (progressCounter > 0) {
            return;
        }
        progressCounter = LaserConfig.TICKS10_PER_CATALYST.get();

        infuseLiquid(tankCoordinate, bonus);
    }

    public static InfusingBonus getInfusingBonus(ItemStack item) {
        if (item.isEmpty()) {
            return null;
        }
        return InfusionBonusRegistry.getInfusionBonus(item);
    }


    private boolean validRCLTank(TankTileEntity tank) {
        return tank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).map(handler -> {
            return DeepResonanceFluidHelper.isLiquidCrystal(handler.getFluidInTank(0).getFluid());
        }).orElse(false);
    }

    private BlockPos findLens() {
        if (!LevelTools.isLoaded(level, worldPosition)) {
            return null;
        }
        BlockState state = level.getBlockState(worldPosition);
        Direction direction = OrientationTools.getOrientationHoriz(state);
        BlockPos shouldBeAir = worldPosition.relative(direction);
        if (!level.getBlockState(shouldBeAir).isAir()) {
            return null;
        }
        BlockPos shouldBeLens = shouldBeAir.relative(direction);
        Block lensBlock = level.getBlockState(shouldBeLens).getBlock();
        if (!(lensBlock instanceof LensBlock)) {
            return null;
        }
        Direction lensDirection = OrientationTools.getOrientationHoriz(level.getBlockState(shouldBeLens));
        if (lensDirection != direction) {
            return null;
        }

        return shouldBeLens.relative(direction);
    }


    private void infuseLiquid(BlockPos tankCoordinate, InfusingBonus bonus) {
        // We consume stuff even if the tank does not have enough liquid. Player has to be careful
        items.decrStackSize(SLOT_CATALYST, 1);
        energyStorage.consumeEnergy(LaserConfig.RFUSE_PER_CATALYST.get());
        crystalLiquid -= LaserConfig.RCL_PER_CATALYST.get();

        TileEntity te = level.getBlockEntity(tankCoordinate);
        if (te instanceof TankTileEntity) {
            TankTileEntity tileTank = (TankTileEntity) te;
            if (validRCLTank(tileTank)) {
                tileTank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(handler -> {
                    FluidStack stack = handler.drain(1000 * TankBlob.TANK_BUCKETS, IFluidHandler.FluidAction.SIMULATE);
                    if (!stack.isEmpty()) {
                        stack = handler.drain(1000 * TankBlob.TANK_BUCKETS, IFluidHandler.FluidAction.EXECUTE);
                        LiquidCrystalData fluidData = LiquidCrystalData.fromStack(stack);
                        float factor = (float) LaserConfig.RCL_PER_CATALYST.get() / stack.getAmount();
                        float purity = bonus.getPurityModifier().modify(fluidData.getPurity(), fluidData.getQuality(), factor);
                        float strength = bonus.getStrengthModifier().modify(fluidData.getStrength(), fluidData.getQuality(), factor);
                        float efficiency = bonus.getEfficiencyModifier().modify(fluidData.getEfficiency(), fluidData.getQuality(), factor);
                        fluidData.setPurity(purity);
                        fluidData.setStrength(strength);
                        fluidData.setEfficiency(efficiency);
                        FluidStack newStack = fluidData.getFluidStack();
                        if (Math.abs(purity) < 0.01) {
                            newStack.setAmount(newStack.getAmount()-200);
                            if (newStack.getAmount() < 0) {
                                newStack.setAmount(0);
                            }
                        }
                        if (newStack.getAmount() > 0) {
                            handler.fill(newStack, IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                });
            }
        }
    }

    private void changeColor(int newcolor) {
        if (newcolor != color) {
            color = newcolor;
            int mcolor = color;
            if (color == COLOR_YELLOW) {
                mcolor = COLOR_RED;
            } else if (color == 0) {
                mcolor = 0;    // Off
            }
            level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(LaserBlock.COLOR, mcolor), 3);
            markDirty();
        }
    }

    public int getColor() {
        return color;
    }

    private void checkCrystal() {
        ItemStack stack = items.getStackInSlot(SLOT_CRYSTAL);
        if (!stack.isEmpty()) {
            CompoundNBT tagCompound = stack.getOrCreateTag().getCompound(CoreModule.TILE_DATA_TAG);
            float strength = tagCompound.contains("strength") ? tagCompound.getFloat("strength") / 100.0f : 0;
            int toAdd = (int) (LaserConfig.MIN_CRYSTAL_LIQUID_PER_CRYSTAL.get() + strength * (LaserConfig.MAX_CRYSTAL_LIQUID_PER_CRYSTAL.get() - LaserConfig.MIN_CRYSTAL_LIQUID_PER_CRYSTAL.get()));
            float amt = crystalLiquid + toAdd;
            if (amt > LaserConfig.CRYSTAL_LIQUID_MAXIMUM.get()) {
                return;
            }
            stack.shrink(1);
            crystalLiquid = amt;
            setChanged();
        }
    }

    private void checkLens() {
        if (lens != null && lens.isPresent()) {
            return;
        } else if (lens != null) {
            lens = null;
            laserBeam.clear();
        }
        Direction facing = level.getBlockState(getBlockPos()).getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockPos pos = getBlockPos();
        Collection<BlockPos> laser = Lists.newArrayList();
        int c = 1;
        while (c < 8) {
            pos = pos.relative(facing);
            laser.add(pos);
            TileEntity tile = level.getBlockEntity(pos);
            if (tile != null) {
                LazyOptional<ILens> lens = tile.getCapability(MachinesModule.LENS_CAPABILITY, facing);
                if (lens.isPresent()) {
                    this.lens = lens;
                    this.laserBeam.addAll(laser);
                    setChanged();
                    return;
                }
                LazyOptional<ILensMirror> mirror = tile.getCapability(MachinesModule.LENS_MIRROR_CAPABILITY, facing);
                if (mirror.isPresent()) {
                    facing = mirror.orElseThrow(NullPointerException::new).bounceLaser(facing);
                }
            }
            c++;
        }
    }

    @Override
    public void saveClientDataToNBT(CompoundNBT tagCompound) {
        ListNBT list = new ListNBT();
        for (BlockPos pos : laserBeam) {
            list.add(NBTUtil.writeBlockPos(pos));
        }
        tagCompound.put("laserBeam", list);
        tagCompound.putString("bonus", InfusionBonusRegistry.toString(activeBonus));
    }

    @Override
    public void loadClientDataFromNBT(CompoundNBT tagCompound) {
        laserBeam.clear();
        ListNBT list = tagCompound.getList("laserBeam", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            laserBeam.add(NBTUtil.readBlockPos(list.getCompound(i)));
        }
        activeBonus = InfusionBonusRegistry.fromString(tagCompound.getString("bonus"));
    }

    @Override
    public void saveAdditional(@Nonnull CompoundNBT tagCompound) {
        tagCompound.putInt("progress", progressCounter);
        tagCompound.putFloat("liquid", crystalLiquid);
        tagCompound.putFloat("efficiency", efficiency);
        tagCompound.putString("bonus", InfusionBonusRegistry.toString(activeBonus));
        super.saveAdditional(tagCompound);
    }

    @Override
    public void load(CompoundNBT tagCompound) {
        progressCounter = tagCompound.getInt("progress");
        crystalLiquid = tagCompound.getFloat("liquid");
        efficiency = tagCompound.getFloat("efficiency");
        activeBonus = InfusionBonusRegistry.fromString(tagCompound.getString("bonus"));
        super.load(tagCompound);
    }

    public int getMaxPower() {
        return energyStorage.getMaxEnergyStored();
    }

    public int getCurrentPower() {
        return energyStorage.getEnergyStored();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getBlockPos().getX() - 10, getBlockPos().getY() - 10, getBlockPos().getZ() - 10, getBlockPos().getX() + 10, getBlockPos().getY() + 10, getBlockPos().getZ() + 10);
    }

    // Client side
    public float getCrystalLiquid() {
        return crystalLiquid;
    }

    // Client side
    public InfusionBonus getActiveBonus() {
        return activeBonus;
    }

    // Client
    public Collection<BlockPos> getLaserBeam() {
        return laserBeam;
    }

}
