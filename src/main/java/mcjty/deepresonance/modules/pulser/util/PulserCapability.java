package mcjty.deepresonance.modules.pulser.util;

import com.google.common.base.Preconditions;
import elec332.core.api.info.IInfoDataAccessorBlock;
import elec332.core.api.info.IInfoProvider;
import elec332.core.api.info.IInformation;
import mcjty.deepresonance.api.crystal.ICrystalModifier;
import mcjty.deepresonance.modules.core.tile.TileEntityResonatingCrystal;
import mcjty.deepresonance.modules.pulser.PulserModule;
import mcjty.lib.varia.SoundTools;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by Elec332 on 31-7-2020
 */
public class PulserCapability implements ICrystalModifier, INBTSerializable<CompoundNBT>, IInfoProvider {

    // These values are used during super powergen
    private int cooldown;           // In microticks
    private int resistance;         // Current maximum cooldown when a pulse is received
    private float instability;      // Current instability
    private int pulses;             // Number of EMP pulses since last tick

    private TileEntityResonatingCrystal crystal;

    public PulserCapability() {
        cooldown = 0;
        resistance = PulserModule.pulserCrystalConfig.MAX_RESISTANCE.get();
        instability = 0;
        pulses = 0;
    }

    @Override
    public void setCrystal(TileEntityResonatingCrystal crystal) {
        this.crystal = crystal;
    }

    @Override
    public void onPowerChanged(boolean isEmpty) {
        if (isEmpty) {
            resistance = PulserModule.pulserCrystalConfig.MAX_RESISTANCE.get();
        }
    }

    @Override
    public float getRadiationModifier() {
        float maxResistance = PulserModule.pulserCrystalConfig.MAX_RESISTANCE.get();
        if (resistance < maxResistance) {
            return maxResistance / resistance;
        }
        return 1;
    }

    public int getResistance() {
        return resistance;
    }

    public int getCooldown() {
        return cooldown;
    }

    public float getInstability() {
        return instability;
    }

    @Override
    public void tick() {
        crystal.markDirtyQuick();

        // Handle the next 1000 microticks
        int microTicksLeft = 1000;

        // Handle pulses
        while (pulses > 0) {
            pulses--;

            // We can delay the pulse until after the cooldown has finished
            if (cooldown <= microTicksLeft) {
                // We have enough ticks left to go after the cooldown
                microTicksLeft -= cooldown;
                cooldown = 0;
            } else {
                // Not enough ticks left
                cooldown -= microTicksLeft;
                microTicksLeft = 0;
            }

            // Actually handle the pulse
            handleSinglePulse();
        }

        // No more pulses. Just handle cooldown and resistance
        if (cooldown < microTicksLeft) {
            // We have less then 1 tick of cooldown. So that means we only
            // have to increase resistance for the actual cooldown period
            resistance += (microTicksLeft - cooldown) / 12;
            if (resistance > PulserModule.pulserCrystalConfig.MAX_RESISTANCE.get()) {
                resistance = PulserModule.pulserCrystalConfig.MAX_RESISTANCE.get();
            }
            cooldown = 0;
        } else {
            cooldown -= microTicksLeft;
        }

        if (getInstability() > 0) {
            // We're currently having some instability issues
            handleInstability();
        }
    }

    private void handleInstability() {
        // The 'instability' value accumulates. Every time we handle instability there is a chance
        // we act on it. Not acting on instability is actually a bad thing as it means the instability will
        // stay and possibly be augmented in the near future
        World world = Preconditions.checkNotNull(crystal.getWorld());
        if (world.getRandom().nextFloat() < PulserModule.pulserCrystalConfig.INSTABILITY_HANDLING_CHANCE.get()) {
            // We handle the instability. How much do we handle?
            float tohandle = world.getRandom().nextFloat() * getInstability();
            instability -= tohandle;
            if (tohandle > PulserModule.pulserCrystalConfig.INSTABILITY_EXPLOSION_THRESHOLD.get()) {
                SoundTools.playSound(world, SoundEvents.ENTITY_GENERIC_EXPLODE, crystal.getPos().getX(), crystal.getPos().getY(), crystal.getPos().getZ(), 1.0, 1.0);
                crystal.setPower(0);
//                ResonatingCrystalBlock.explode(world, pos, true);
            } else if (tohandle > PulserModule.pulserCrystalConfig.INSTABILITY_BIG_DAMAGE_THRESHOLD.get()) {
                // Damage crystal
                crystal.setPower(Math.max(0, crystal.getPower() - 10));
                crystal.setPurity(Math.max(1, crystal.getPurity() - 10));
            } else if (tohandle > PulserModule.pulserCrystalConfig.INSTABILITY_SMALL_DAMAGE_THRESHOLD.get()) {
                // Damage crystal
                crystal.setPower(Math.max(0, crystal.getPower() - 1));
                crystal.setPurity(Math.max(1, crystal.getPurity() - 1));
            } // Otherwise we just got lucky
        }
    }

    private void handleSinglePulse() {
        // We got a pulse. If our cooldown is > 0 we have to do some bad things
        if (cooldown > 0) {
            // The bad things depend on how far we actually are from our current resistance value. We do a
            // down cap of cooldown to 10 to make sure we have a minimum badness
            // @todo config for min cap
            float badness = (float) Math.min(cooldown, 10) / resistance;
            instability += badness;

            // Decrease resistance as well but not as much
            // @todo config the /10?
            resistance = (int) ((resistance - 1000 * (1 - badness)) / 10.0f);
            if (resistance < 1) {
                resistance = 1;
            }
        } else {
            // Otherwise we can decrease our resistance a bit
            resistance -= 120;// @todo SuperGenerationConfiguration.resistanceDecreasePerPulse;
            if (resistance < 1) {
                resistance = 1; // @todo cap?
            }
        }

        // Now we have to set the cooldown back to our resistance
        cooldown = resistance;
    }

    @Override
    public float getPowerModifier(float percentage, boolean simulate) {
        // resistance 1: factor 20
        // resistance MAX: factor 1
        double maxResistance = PulserModule.pulserCrystalConfig.MAX_RESISTANCE.get();
        if (getResistance() < maxResistance) {
            double factor = ((maxResistance - getResistance()) * 19.0f / maxResistance) + 1.0f;
            return (float) factor;
        }
        return 1;
    }

    // A pulse is received
    public void pulse() {
        if (crystal.getPower() > 0) {
            // If we're not glowing (not active) we ignore pulses
            pulses++;
            crystal.markDirtyQuick();
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tagCompound = new CompoundNBT();
        tagCompound.putInt("cool", cooldown);
        tagCompound.putInt("resist", resistance);
        tagCompound.putFloat("instability", instability);
        tagCompound.putInt("pulses", pulses);
        return tagCompound;
    }

    @Override
    public void deserializeNBT(CompoundNBT tagCompound) {
        cooldown = tagCompound.getInt("cool");
        resistance = tagCompound.getInt("resist");
        if (resistance <= 0) {
            resistance = PulserModule.pulserCrystalConfig.MAX_RESISTANCE.get();
        }
        instability = tagCompound.getFloat("instability");
        pulses = tagCompound.getInt("pulses");
    }

    @Override
    public void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorBlock hitData) {
        CompoundNBT tag = hitData.getData();
        if (information.isDebugMode() == Boolean.TRUE) {
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            decimalFormat.setRoundingMode(RoundingMode.DOWN);
            information.addInformation("Instability: " + decimalFormat.format(tag.getFloat("instability")));
            information.addInformation("Resistance: " + tag.getInt("resistance"));
            information.addInformation("Cooldown: " + tag.getInt("cooldown"));
        }
    }

    @Override
    public void gatherInformation(@Nonnull CompoundNBT tag, @Nonnull ServerPlayerEntity player, @Nonnull IInfoDataAccessorBlock hitData) {
        tag.putFloat("instability", getInstability());
        tag.putInt("resistance", getResistance());
        tag.putInt("cooldown", getCooldown());
    }

}
