package mcjty.deepresonance.modules.machines.tile;

import com.google.common.collect.Lists;
import elec332.core.ElecCore;
import elec332.core.inventory.BasicItemHandler;
import elec332.core.item.AbstractItemBlock;
import elec332.core.util.BlockProperties;
import elec332.core.util.FMLHelper;
import elec332.core.util.NBTTypes;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.api.infusion.InfusionBonus;
import mcjty.deepresonance.api.laser.ILens;
import mcjty.deepresonance.api.laser.ILensMirror;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.client.gui.LaserGui;
import mcjty.deepresonance.modules.machines.util.InfusionBonusRegistry;
import mcjty.deepresonance.util.AbstractPoweredTileEntity;
import mcjty.deepresonance.util.RegisteredContainer;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.SlotDefinition;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Created by Elec332 on 28-7-2020
 */
public class TileEntityLaser extends AbstractPoweredTileEntity implements ITickableTileEntity {

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

    private static final RegisteredContainer<GenericContainer, LaserGui, TileEntityLaser> container = new RegisteredContainer<GenericContainer, LaserGui, TileEntityLaser>("laser", 3, factory -> {
        factory.playerSlots(10, 70);
        factory.slot(SlotDefinition.container(), ContainerFactory.CONTAINER_CONTAINER, SLOT_CRYSTAL, 154, 48);
        factory.slot(SlotDefinition.container(), ContainerFactory.CONTAINER_CONTAINER, SLOT_CATALYST, 21, 8);
        factory.slot(SlotDefinition.container(), ContainerFactory.CONTAINER_CONTAINER, SLOT_ACTIVE_CATALYST, 21, 48);
    }) {

        @Override
        public Object createGui(TileEntityLaser tile, GenericContainer container, PlayerInventory inventory) {
            return new LaserGui(tile, container, inventory);
        }

    };

    public TileEntityLaser() {
        super(MachinesModule.TYPE_LASER.get(), MachinesModule.laserConfig.powerMaximum.get(), MachinesModule.laserConfig.powerPerTickIn.get(), new BasicItemHandler(3) {

            @Override
            public boolean canExtract(int slot) {
                return true;
            }

            @Override
            public boolean canInsert(int slot, @Nonnull ItemStack stack) {
                return slot != SLOT_ACTIVE_CATALYST;
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == SLOT_CRYSTAL) {
                    return stack.getItem() == CoreModule.RESONATING_CRYSTAL_ITEM.get();
                }
                if (slot == SLOT_CATALYST) {
                    return stack.getItem() != CoreModule.RESONATING_CRYSTAL_ITEM.get();
                }
                return false;
            }

        });
    }

    @Override
    protected void dropInventory() {
        for (int i = 0; i < 2; i++) { //Don't drop item that is being processed
            WorldHelper.dropStack(getWorld(), getPos(), itemHandler.getStackInSlot(i));
        }
        itemHandler.clear();
    }

    @Override
    public void tick() {
        if (WorldHelper.isClient(getWorld())) {
            return;
        }

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
            int storedEnergy = energyHandler.getEnergyStored();
            int powerDraw = Math.min(storedEnergy, powerRequired);
            float eff = powerDraw / (float) powerRequired;
            energyHandler.consumeEnergy(powerDraw);
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
                float quality = data.getQuality();
                float efficiency = TileEntityLaser.this.efficiency;
                activeBonus.getEfficiencyModifier().applyModifier(data::getEfficiency, data::setEfficiency, quality, efficiency);
                activeBonus.getPurityModifier().applyModifier(data::getPurity, data::setPurity, quality, efficiency);
                activeBonus.getStrengthModifier().applyModifier(data::getStrength, data::setStrength, quality, efficiency);
            }));
        }
        activeBonus = InfusionBonus.EMPTY;
        efficiency = 0;
        if (energyHandler.getEnergyStored() > 1000 && crystalLiquid > 0 && lens != null && lens.isPresent()) {
            ItemStack stack = itemHandler.extractItem(SLOT_CATALYST, 1, true);
            InfusionBonus bonus = MachinesModule.INFUSION_BONUSES.getInfusionBonus(stack);
            if (!bonus.isEmpty()) {
                bonus = MachinesModule.INFUSION_BONUSES.getInfusionBonus(itemHandler.extractItem(SLOT_CATALYST, 1, false));
            }
            activeBonus = bonus;
        }
        progressCounter = activeBonus.getDuration();
        markDirtyClient();
    }

    private void checkCrystal() {
        ItemStack stack = itemHandler.getStackInSlot(SLOT_CRYSTAL);
        if (!stack.isEmpty()) {
            CompoundNBT tagCompound = stack.getOrCreateTag().getCompound(AbstractItemBlock.TILE_DATA_TAG);
            float strength = tagCompound.contains("strength") ? tagCompound.getFloat("strength") / 100.0f : 0;
            int toAdd = (int) (MachinesModule.laserConfig.minCrystalLiquidPerCrystal.get() + strength * (MachinesModule.laserConfig.maxCrystalLiquidPerCrystal.get() - MachinesModule.laserConfig.minCrystalLiquidPerCrystal.get()));
            float amt = crystalLiquid + toAdd;
            if (amt > MachinesModule.laserConfig.crystalLiquidMaximum.get()) {
                return;
            }
            stack.shrink(1);
            crystalLiquid = amt;
            markDirty();
        }
    }

    private void checkLens() {
        if (lens != null && lens.isPresent()) {
            return;
        } else if (lens != null) {
            lens = null;
            laserBeam.clear();
        }
        Direction facing = WorldHelper.getBlockState(getWorld(), getPos()).get(BlockProperties.FACING_HORIZONTAL);
        BlockPos pos = getPos();
        Collection<BlockPos> laser = Lists.newArrayList();
        int c = 1;
        while (c < 8) {
            pos = pos.offset(facing);
            laser.add(pos);
            TileEntity tile = WorldHelper.getTileAt(getWorld(), pos);
            if (tile != null) {
                LazyOptional<ILens> lens = tile.getCapability(MachinesModule.LENS_CAPABILITY, facing);
                if (lens.isPresent()) {
                    this.lens = lens;
                    this.laserBeam.addAll(laser);
                    WorldHelper.markBlockForUpdate(getWorld(), getPos());
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
    public void readClientDataFromNBT(CompoundNBT tagCompound) {
        super.readClientDataFromNBT(tagCompound);
        laserBeam.clear();
        ListNBT list = tagCompound.getList("laserBeam", NBTTypes.COMPOUND.getID());
        for (int i = 0; i < list.size(); i++) {
            laserBeam.add(NBTUtil.readBlockPos(list.getCompound(i)));
        }
        ElecCore.tickHandler.registerCall(() -> WorldHelper.markBlockForRenderUpdate(getWorld(), getPos()), getWorld());
    }

    @Override
    public void writeClientDataToNBT(CompoundNBT tagCompound) {
        super.writeClientDataToNBT(tagCompound);
        ListNBT list = new ListNBT();
        for (BlockPos pos : laserBeam) {
            list.add(NBTUtil.writeBlockPos(pos));
        }
        tagCompound.put("laserBeam", list);
    }

    @Nullable
    @Override
    protected LazyOptional<INamedContainerProvider> createScreenHandler() {
        return container.build(this);
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putInt("progress", progressCounter);
        tagCompound.putFloat("liquid", crystalLiquid);
        tagCompound.putFloat("efficiency", efficiency);
        tagCompound.putString("bonus", InfusionBonusRegistry.toString(activeBonus));
        return super.write(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        progressCounter = tagCompound.getInt("progress");
        crystalLiquid = tagCompound.getFloat("liquid");
        efficiency = tagCompound.getFloat("efficiency");
        activeBonus = InfusionBonusRegistry.fromString(tagCompound.getString("bonus"));
        super.read(tagCompound);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (!FMLHelper.getDist().isClient()) {
            throw new UnsupportedOperationException();
        }
        return new AxisAlignedBB(getPos().getX() - 10, getPos().getY() - 10, getPos().getZ() - 10, getPos().getX() + 10, getPos().getY() + 10, getPos().getZ() + 10);
    }

    public float getCrystalLiquid() {
        if (!FMLHelper.getDist().isClient()) {
            throw new UnsupportedOperationException();
        }
        return crystalLiquid;
    }

    public InfusionBonus getActiveBonus() {
        if (!FMLHelper.getDist().isClient()) {
            throw new UnsupportedOperationException();
        }
        return activeBonus;
    }

    public Collection<BlockPos> getLaserBeam() {
        if (!FMLHelper.getDist().isClient()) {
            throw new UnsupportedOperationException();
        }
        return laserBeam;
    }

}
