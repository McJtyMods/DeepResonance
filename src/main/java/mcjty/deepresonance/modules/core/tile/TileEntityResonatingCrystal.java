package mcjty.deepresonance.modules.core.tile;

import com.google.common.base.Preconditions;
import elec332.core.api.registration.RegisteredTileEntity;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.modules.core.util.CrystalConfig;
import mcjty.deepresonance.modules.core.util.CrystalHelper;
import mcjty.deepresonance.util.AbstractTileEntity;
import mcjty.lib.varia.SoundTools;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Created by Elec332 on 18-1-2020
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@RegisteredTileEntity("resonating_crystal")
public class TileEntityResonatingCrystal extends AbstractTileEntity implements ITickableTileEntity {

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

    // These values are used during super powergen
    private int cooldown;           // In microticks
    private int resistance;         // Current maximum cooldown when a pulse is received
    private float instability;      // Current instability
    private int pulses;             // Number of EMP pulses since last tick

    private float powerPerTick = -1;    // Calculated value that contains the power/tick that is drained for this crystal.
    private int rfPerTick = -1;         // Calculated value that contains the RF/tick for this crystal.

    private boolean glowing = false;

    public TileEntityResonatingCrystal() {
        cooldown = 0;
        resistance = CrystalConfig.MAX_RESISTANCE.get();
        instability = 0;
        pulses = 0;
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
            if (newempty) {
                resistance = CrystalConfig.MAX_RESISTANCE.get();
            }
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

    public boolean isGlowing() {
        return glowing;
    }

    public void setGlowing(boolean glowing) {
        if (isGlowing() == glowing) {
            return;
        }
        this.glowing = glowing;
        pulses = 0;         // Clear pulses
        if (getWorld() != null) {
            markDirtyClient();
        } else {
            markDirty();
        }
    }

    public boolean isEmpty() {
        return CrystalHelper.isEmpty(getPower());
    }

    @Override
    public void tick() {
        markDirtyQuick();

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
            if (resistance > CrystalConfig.MAX_RESISTANCE.get()) {
                resistance = CrystalConfig.MAX_RESISTANCE.get();
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
        World world = Preconditions.checkNotNull(getWorld());
        if (world.getRandom().nextFloat() < CrystalConfig.INSTABILITY_HANDLING_CHANCE.get()) {
            // We handle the instability. How much do we handle?
            float tohandle = world.getRandom().nextFloat() * getInstability();
            instability -= tohandle;
            if (tohandle > CrystalConfig.INSTABILITY_EXPLOSION_THRESHOLD.get()) {
                SoundTools.playSound(world, SoundEvents.ENTITY_GENERIC_EXPLODE, pos.getX(), pos.getY(), pos.getZ(), 1.0, 1.0);
                setPower(0);
//                ResonatingCrystalBlock.explode(world, pos, true);
            } else if (tohandle > CrystalConfig.INSTABILITY_BIG_DAMAGE_THRESHOLD.get()) {
                // Damage crystal
                setPower(Math.max(0, getPower() - 10));
                setPurity(Math.max(1, getPurity() - 10));
            } else if (tohandle > CrystalConfig.INSTABILITY_SMALL_DAMAGE_THRESHOLD.get()) {
                // Damage crystal
                setPower(Math.max(0, getPower() - 1));
                setPurity(Math.max(1, getPurity() - 1));
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

    // A pulse is received
    public void pulse() {
        if (isGlowing() && getPower() > 0) {
            // If we're not glowing (not active) we ignore pulses
            pulses++;
            markDirtyQuick();
        }
    }

    public float getPowerPerTick() {
        if (powerPerTick < 0) {
            float totalRF = CrystalHelper.getTotalPower(getStrength(), getPurity());
            float numticks = totalRF / CrystalHelper.getRfPerTick(getEfficiency(), getPurity());
            powerPerTick = 100.0f / numticks;
        }
        return powerPerTick;
    }

    public int getRfPerTick() {
        if (rfPerTick == -1) {
            rfPerTick = CrystalHelper.getRfPerTick(getEfficiency(), getPurity());
        }

        // If we are super generating then we modify the RF here. To see that we're doing this we
        // can basically check our resistance value

        // resistance 1: factor 20
        // resistance MAX: factor 1
        double maxResistance = CrystalConfig.MAX_RESISTANCE.get();
        if (getResistance() < maxResistance) {
            double factor = ((maxResistance - getResistance()) * 19.0f / maxResistance) + 1.0f;
            System.out.println("rfPerTick = " + rfPerTick + ", factor = " + factor);
            return (int) (rfPerTick * factor);
        }

        return rfPerTick;
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
        cooldown = tagCompound.getInt("cool");
        resistance = tagCompound.getInt("resist");
        if (resistance <= 0) {
            resistance = CrystalConfig.MAX_RESISTANCE.get();
        }
        instability = tagCompound.getFloat("instability");
        pulses = tagCompound.getInt("pulses");
        strength = tagCompound.getFloat("strength");
        power = tagCompound.getFloat("power");
        efficiency = tagCompound.getFloat("efficiency");
        purity = tagCompound.getFloat("purity");
        glowing = tagCompound.getBoolean("glowing");
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putInt("cool", cooldown);
        tagCompound.putInt("resist", resistance);
        tagCompound.putFloat("instability", instability);
        tagCompound.putInt("pulses", pulses);
        tagCompound.putFloat("strength", strength);
        tagCompound.putFloat("power", power);
        tagCompound.putFloat("efficiency", efficiency);
        tagCompound.putFloat("purity", purity);
        tagCompound.putBoolean("glowing", glowing);
        return super.write(tagCompound);
    }

}
