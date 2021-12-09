package mcjty.deepresonance.modules.machines.block;

import com.google.common.collect.Lists;
import mcjty.deepresonance.api.infusion.InfusionBonus;
import mcjty.deepresonance.api.laser.ILens;
import mcjty.deepresonance.api.laser.ILensMirror;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.data.InfusionBonusRegistry;
import mcjty.deepresonance.modules.machines.util.config.LaserConfig;
import mcjty.deepresonance.util.TranslationHelper;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.tileentity.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Collection;

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
    private float crystalLiquid = 0;
    private float efficiency = 0;
    private InfusionBonus activeBonus = InfusionBonus.EMPTY;
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
                    return stack.getItem() == CoreModule.RESONATING_CRYSTAL_ITEM.get();
                }
                if (slot == SLOT_CATALYST) {
                    return stack.getItem() != CoreModule.RESONATING_CRYSTAL_ITEM.get();
                }
                return false;
            })
            .insertable(notslot(SLOT_ACTIVE_CATALYST))
            .extractable(yes())
            .build();

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Laser")
            .containerSupplier(container(MachinesModule.LASER_CONTAINER, CONTAINER_FACTORY, this))
            .itemHandler(() -> items)
            .setupSync(this));

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, LaserConfig.POWER_MAXIMUM.get(), LaserConfig.POWER_PER_TICK_IN.get());

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

    @Override
    public void tickServer() {
        crystalCountdown--;
        if (crystalCountdown <= 0) {
            checkCrystal();
            crystalCountdown = 20;
        }

        lensCountdown--;
        if (lensCountdown <= 0) {
            checkLens();
            lensCountdown = 53; //Almost always separate
        }

        boolean dirty = false;
        int powerRequired = activeBonus.getPowerPerTick();
        if (powerRequired > 0) {
            int storedEnergy = energyStorage.getEnergyStored();
            int powerDraw = Math.min(storedEnergy, powerRequired);
            float eff = powerDraw / (float) powerRequired;
            energyStorage.consumeEnergy(powerDraw);
            this.efficiency += eff / activeBonus.getDuration();
            dirty = true;
        }
        float cpt = activeBonus.getCrystalLiquidCostPerTick();
        if (cpt > 0.0001) {
            if (crystalLiquid > cpt) {
                crystalLiquid -= cpt;
                dirty = true;
            } else {
                crystalLiquid = 0;
                processBonus();
            }
        }
        progressCounter--;
        if (progressCounter <= 0) {
            processBonus();
        } else if (dirty) { //Above also marks dirty, no need to do this twice
            markDirtyQuick();
        }
    }

    private void processBonus() {
        if (lens != null) {
            lens.ifPresent(l -> l.infuseFluid(activeBonus.getRclImprovedPerCatalyst(), data -> {
                double quality = data.getQuality();
                float efficiency = LaserTileEntity.this.efficiency;
                activeBonus.getEfficiencyModifier().applyModifier(data::getEfficiency, data::setEfficiency, quality, efficiency);
                activeBonus.getPurityModifier().applyModifier(data::getPurity, data::setPurity, quality, efficiency);
                activeBonus.getStrengthModifier().applyModifier(data::getStrength, data::setStrength, quality, efficiency);
            }));
        }
        activeBonus = InfusionBonus.EMPTY;
        efficiency = 0;
        if (energyStorage.getEnergyStored() > 1000 && crystalLiquid > 0 && lens != null && lens.isPresent()) {
            ItemStack stack = items.extractItem(SLOT_CATALYST, 1, true);
            InfusionBonus bonus = MachinesModule.INFUSION_BONUSES.getInfusionBonus(stack);
            if (!bonus.isEmpty()) {
                bonus = MachinesModule.INFUSION_BONUSES.getInfusionBonus(items.extractItem(SLOT_CATALYST, 1, false));
            }
            activeBonus = bonus;
        }
        progressCounter = activeBonus.getDuration();
        setChanged();
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
    public void writeClientDataToNBT(CompoundNBT tagCompound) {
        ListNBT list = new ListNBT();
        for (BlockPos pos : laserBeam) {
            list.add(NBTUtil.writeBlockPos(pos));
        }
        tagCompound.put("laserBeam", list);
    }

    @Override
    public void readClientDataFromNBT(CompoundNBT tagCompound) {
        laserBeam.clear();
        ListNBT list = tagCompound.getList("laserBeam", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            laserBeam.add(NBTUtil.readBlockPos(list.getCompound(i)));
        }
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
