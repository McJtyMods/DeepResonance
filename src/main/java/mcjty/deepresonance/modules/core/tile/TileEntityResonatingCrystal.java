package mcjty.deepresonance.modules.core.tile;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import elec332.core.api.info.IInfoDataAccessorBlock;
import elec332.core.api.info.IInfoProvider;
import elec332.core.api.info.IInformation;
import elec332.core.api.info.InfoMod;
import elec332.core.api.registration.RegisteredTileEntity;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.api.crystal.ICrystalModifier;
import mcjty.deepresonance.api.radiation.IWorldRadiationManager;
import mcjty.deepresonance.modules.core.block.BlockCrystal;
import mcjty.deepresonance.modules.core.util.CrystalHelper;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.radiation.util.RadiationHelper;
import mcjty.deepresonance.util.AbstractTileEntity;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 18-1-2020
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@RegisteredTileEntity("resonating_crystal")
public class TileEntityResonatingCrystal extends AbstractTileEntity implements ITickableTileEntity, IInfoProvider {

    private static final Set<Capability<? extends ICrystalModifier>> MODIFIERS = Sets.newHashSet();
    private static final int RADIATION_COOLDOWN = 22;

    // The total maximum RF you can get out of a crystal with the following characteristics:
    //    * S: Strength (0-100%)
    //    * P: Purity (0-100%)
    //    * E: Efficiency (0-100%)
    // Is equal to:
    //    * MaxRF = FullMax * (S/100) * ((P+30)/130)
    // The RF/tick you can get out of a crystal with the above characteristics is:
    //    * RFTick = FullRFTick * (E/100.1) * ((P+2)/102) + 1           (the divide by 100.1 is to make sure we don't go above 20000)

    private float strength = 1.0f;
    private float power = 1.0f;         // Default 1% power
    private float efficiency = 1.0f;    // Default 1%
    private float purity = 1.0f;        // Default 1% purity

    private int radiationCooldown = 0;
    private Set<ICrystalModifier> modifiers;
    private LazyOptional<TileEntityResonatingCrystal> reference;
    private float crystalPowerDrain = -1;    // Calculated value that contains the power/tick that is drained for this crystal.
    private int powerProvided = -1;         // Calculated value that contains the RF/tick for this crystal.

    public static void registerModifier(Capability<? extends ICrystalModifier> type) {
        MODIFIERS.add(type);
    }

    public float getStrength() {
        return strength;
    }

    public void setStrength(float strength) {
        this.strength = strength;
        markDirtyClient();
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        boolean oldempty = isEmpty();
        this.power = power;
        markDirty();
        boolean newempty = isEmpty();
        if (oldempty != newempty) {
            getModifiers().forEach(mod -> mod.onPowerChanged(newempty));
            markDirtyClient();
        }
    }

    public float getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(float efficiency) {
        this.efficiency = efficiency;
        markDirtyClient();
    }

    public float getPurity() {
        return purity;
    }

    public void setPurity(float purity) {
        this.purity = purity;
        markDirtyClient();
    }

    public boolean isEmpty() {
        return CrystalHelper.isEmpty(getPower());
    }

    @Override
    public void tick() {
        getModifiers().forEach(ICrystalModifier::tick);
    }

    public int providePower(float percentage, boolean simulate) {
        percentage = Math.min(percentage, 1);
        if (isEmpty()) {
            return 0;
        }
        float power = getPower();
        if (power < crystalPowerDrain) {
            if (!simulate) {
                setPower(0);
            }
            return 0;
        }
        if (crystalPowerDrain < 0 || powerProvided < 0) {
            powerProvided = CrystalHelper.getRfPerTick(getEfficiency(), getPurity());

            float totalPower = CrystalHelper.getTotalPower(getStrength(), getPurity());
            float ticks = totalPower / powerProvided;
            crystalPowerDrain = 100.0f / ticks;
        }
        if (!simulate) {
            setPower(power - crystalPowerDrain);
            radiationCooldown--;
            if (radiationCooldown <= 0) {
                spreadRadiation();
                radiationCooldown = RADIATION_COOLDOWN;
            }
        }
        float mod = 1;
        for (ICrystalModifier modifier : getModifiers()) {
            mod *= modifier.getPowerModifier(percentage, simulate);
        }
        return (int) (powerProvided * Math.min(mod, 100));
    }

    public void recalculateEnergy() {
        crystalPowerDrain = powerProvided = -1;
    }

    private void spreadRadiation() {
        LazyOptional<IWorldRadiationManager> cap = Preconditions.checkNotNull(getWorld()).getCapability(RadiationModule.CAPABILITY);
        cap.ifPresent(radiationManager -> {
            float purity = getPurity();
            float strength = getStrength();
            float radius = RadiationHelper.calculateRadiationRadius(strength, getEfficiency(), purity);
            float radiationStrength = RadiationHelper.calculateRadiationStrength(strength, purity);
            for (ICrystalModifier modifier : getModifiers()) {
                radiationStrength *= modifier.getRadiationModifier();
            }
            radiationManager.getOrCreateRadiationSource(TileEntityResonatingCrystal.this.getPos()).update(radius, radiationStrength, RADIATION_COOLDOWN);
        });
    }

    private Set<ICrystalModifier> getModifiers() {
        if (modifiers == null) {
            modifiers = MODIFIERS.stream()
                    .map(this::getCapability)
                    .filter(LazyOptional::isPresent)
                    .map(o -> o.orElseThrow(NullPointerException::new))
                    .collect(Collectors.toSet());
        }
        return modifiers;
    }

    public LazyOptional<TileEntityResonatingCrystal> getReference() {
        if (reference == null) {
            reference = LazyOptional.of(() -> this);
        }
        return reference;
    }


    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        if (reference != null) {
            reference.invalidate();
        }
    }

    @Override
    public boolean setOwner(PlayerEntity player) {
        return false;
    }

    @Override
    public String getOwnerName() {
        return "";
    }

    @Override
    public UUID getOwnerUUID() {
        return null;
    }

    @Override
    public void validate() {
        super.validate();
        getModifiers().forEach(mod -> mod.setCrystal(this));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        boolean oldempty = isEmpty();
        boolean oldVeryPure = CrystalHelper.isVeryPure(getPurity());
        super.onDataPacket(net, packet);
        boolean newempty = isEmpty();
        if (oldempty != newempty || oldVeryPure != CrystalHelper.isVeryPure(getPurity())) {
            WorldHelper.markBlockForRenderUpdate(Preconditions.checkNotNull(getWorld()), getPos());
        }
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);

        strength = tagCompound.getFloat("strength");
        power = tagCompound.getFloat("power");
        efficiency = tagCompound.getFloat("efficiency");
        purity = tagCompound.getFloat("purity");
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putFloat("strength", strength);
        tagCompound.putFloat("power", power);
        tagCompound.putFloat("efficiency", efficiency);
        tagCompound.putFloat("purity", purity);
        return super.write(tagCompound);
    }

    @Override
    public void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorBlock hitData) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        CompoundNBT tag = hitData.getData();
        float power = tag.getFloat("power");
        BlockCrystal.addBasicInformation(information::addInformation, tag, power, information.getProviderType() == InfoMod.WAILA);
        getModifiers().forEach(mod -> {
            if (mod instanceof IInfoProvider) {
                ((IInfoProvider) mod).addInformation(information, hitData);
            }
        });
        if (information.isDebugMode() == Boolean.TRUE) { //Debug, no translation
            information.addInformation("Power: " + decimalFormat.format(power) + "%");
        } else if (information.getProviderType() == InfoMod.TOP) {
            information.addInformation(new StringTextComponent("Power: " + decimalFormat.format(power) + "%").applyTextStyle(TextFormatting.YELLOW));
            IProbeInfo probeInfo = (IProbeInfo) information.getInformationComponent();
            probeInfo.progress((int) power, 100, probeInfo.defaultProgressStyle()
                    .suffix("%")
                    .width(40)
                    .height(10)
                    .showText(false)
                    .filledColor(0xffff0000)
                    .alternateFilledColor(0xff990000));
        }
    }

    @Override
    public void gatherInformation(@Nonnull CompoundNBT tag, @Nonnull ServerPlayerEntity player, @Nonnull IInfoDataAccessorBlock hitData) {
        tag.putFloat("strength", getStrength());
        tag.putFloat("efficiency", getEfficiency());
        tag.putFloat("purity", getPurity());
        tag.putFloat("power", getPower());
        getModifiers().forEach(mod -> {
            if (mod instanceof IInfoProvider) {
                ((IInfoProvider) mod).gatherInformation(tag, player, hitData);
            }
        });
    }

}
