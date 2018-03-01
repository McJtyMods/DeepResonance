package mcjty.deepresonance.blocks.crystals;

import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.collector.EnergyCollectorTileEntity;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.deepresonance.radiation.SuperGenerationConfiguration;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.SoundTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class ResonatingCrystalTileEntity extends GenericTileEntity implements ITickable {

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
    private int cooldown = 0;           // In microticks
    private int resistance = 0;         // Current maximum cooldown when a pulse is received (microticks)
    private float instability = 0;      // Current instability
    private int pulses = 0;             // Number of EMP pulses since last tick

    private float powerPerTick = -1;    // Calculated value that contains the power/tick that is drained for this crystal.
    private int rfPerTick = -1;         // Calculated value that contains the RF/tick for this crystal.

    private boolean glowing = false;

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

    public float getPower() {
        return power;
    }

    public float getEfficiency() {
        return efficiency;
    }

    public float getPurity() {
        return purity;
    }

    public boolean isGlowing() {
        return glowing;
    }


    // We enqueue crystals for processing later
    public static Set<ResonatingCrystalTileEntity> todoCrystals = new HashSet<>();


    public void setStrength(float strength) {
        this.strength = strength;
        markDirtyClient();
    }

    public boolean isEmpty() {
        return power < EnergyCollectorTileEntity.CRYSTAL_MIN_POWER;
    }

    public void setPower(float power) {
        boolean oldempty = isEmpty();
        this.power = power;
        markDirty();
        boolean newempty = isEmpty();
        if (oldempty != newempty) {
            if (newempty) {
                resistance = SuperGenerationConfiguration.maxResistance;
            }
            markDirtyClient();
        }
    }

    public ResonatingCrystalTileEntity() {
        resistance = SuperGenerationConfiguration.maxResistance;
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            todoCrystals.add(this);
        }
    }

    public void realUpdate() {
        markDirtyQuick();

        // Handle the next 1000 microticks
        int microTicksLeft = 1000;

        if (purity > 20) {
            System.out.println("Cool=" + cooldown + ", Pulses=" + pulses + ", Resist=" + resistance);
        }

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
            if (resistance > SuperGenerationConfiguration.maxResistance) {
                resistance = SuperGenerationConfiguration.maxResistance;
            }
            cooldown = 0;
        } else {
            cooldown -= microTicksLeft;
        }

        if (instability > 0) {
            // We're currently having some instability issues
            handleInstability();
        }
    }

    private void handleInstability() {
        // The 'instability' value accumulates. Every time we handle instability there is a chance
        // we act on it. Not acting on instability is actually a bad thing as it means the instability will
        // stay and possibly be augmented in the near future
        if (world.rand.nextFloat() < SuperGenerationConfiguration.instabilityHandlingChance) {
            // We handle the instability. How much do we handle?
            float tohandle = world.rand.nextFloat() * instability;
            instability -= tohandle;
            if (tohandle > SuperGenerationConfiguration.instabilityExplosionThresshold) {
                SoundTools.playSound(world, SoundEvents.ENTITY_GENERIC_EXPLODE, pos.getX(), pos.getY(), pos.getZ(), 1.0, 1.0);
                setPower(0);
//                ResonatingCrystalBlock.explode(world, pos, true);
            } else if (tohandle > SuperGenerationConfiguration.instabilityBigDamageThresshold) {
                // Damage crystal
                setPower(Math.max(0, power-10));
                setPurity(Math.max(1, purity-10));
            } else if (tohandle > SuperGenerationConfiguration.instabilitySmallDamageThresshold) {
                // Damage crystal
                setPower(Math.max(0, power-1));
                setPurity(Math.max(1, purity-1));
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
            resistance = (int) ((resistance - 1000 * (1.0f-badness)) / 10.0f);
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
        if (glowing && power > 0) {
            // If we're not glowing (not active) we ignore pulses
            pulses++;
            markDirtyQuick();
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        boolean oldempty = isEmpty();
        super.onDataPacket(net, packet);
        boolean newempty = isEmpty();
        if (oldempty != newempty) {
            getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());
        }
    }

    public void setEfficiency(float efficiency) {
        this.efficiency = efficiency;
        markDirtyClient();
    }

    public void setPurity(float purity) {
        this.purity = purity;
        markDirtyClient();
    }

    public void setGlowing(boolean glowing) {
        if (this.glowing == glowing) {
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

    @Override
    public boolean setOwner(EntityPlayer player) {
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

    public float getPowerPerTick() {
        if (powerPerTick < 0) {
            float totalRF = ResonatingCrystalTileEntity.getTotalPower(strength, purity);
            float numticks = totalRF / ResonatingCrystalTileEntity.getRfPerTick(efficiency, purity);
//            float numticks = totalRF / getRfPerTick();
            powerPerTick = 100.0f / numticks;
        }
        return powerPerTick;
    }

    public static float getTotalPower(float strength, float purity) {
        return 1000.0f * ConfigMachines.Power.maximumKiloRF * strength / 100.0f * (purity + 30.0f) / 130.0f;
    }

    public int getRfPerTick() {
        if (rfPerTick == -1) {
            rfPerTick = ResonatingCrystalTileEntity.getRfPerTick(efficiency, purity);
        }

        // If we are super generating then we modify the RF here. To see that we're doing this we
        // can basically check our resistance value

        // resistance 1: factor 20
        // resistance MAX: factor 1


        if (resistance < SuperGenerationConfiguration.maxResistance) {
            float factor = ((SuperGenerationConfiguration.maxResistance - resistance) * 19.0f / SuperGenerationConfiguration.maxResistance) + 1.0f;
            System.out.println("rfPerTick = " + rfPerTick + ", factor = " + factor);
            return (int) (rfPerTick * factor);
        }

        return rfPerTick;
    }


    public static int getRfPerTick(float efficiency, float purity) {
        return (int) (ConfigMachines.Power.maximumRFPerTick * efficiency / 100.1f * (purity + 2.0f) / 102.0f + 1);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        cooldown = tagCompound.getInteger("cool");
        if (tagCompound.hasKey("resist")) {
            resistance = tagCompound.getInteger("resist");
            if (resistance == 0) {
                resistance = SuperGenerationConfiguration.maxResistance;
            }
        } else {
            resistance = SuperGenerationConfiguration.maxResistance;
        }
        instability = tagCompound.getFloat("instability");
        pulses = tagCompound.getInteger("pulses");
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        strength = tagCompound.getFloat("strength");
        power = tagCompound.getFloat("power");
        efficiency = tagCompound.getFloat("efficiency");
        purity = tagCompound.getFloat("purity");
        glowing = tagCompound.getBoolean("glowing");
        byte version = tagCompound.getByte("version");
        if (version < (byte) 2) {
            // We have to convert the power.
            power *= 20.0f;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("cool", cooldown);
        tagCompound.setInteger("resist", resistance);
        tagCompound.setFloat("instability", instability);
        tagCompound.setInteger("pulses", pulses);
        return tagCompound;
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        tagCompound.setFloat("strength", strength);
        tagCompound.setFloat("power", power);
        tagCompound.setFloat("efficiency", efficiency);
        tagCompound.setFloat("purity", purity);
        tagCompound.setBoolean("glowing", glowing);
        tagCompound.setByte("version", (byte) 2);      // Legacy support to support older crystals.
    }

    public static void spawnCrystal(EntityPlayer player, World world, BlockPos pos, int purity, int strength, int efficiency, int power) {
        world.setBlockState(pos, ModBlocks.resonatingCrystalBlock.getStateFromMeta(0), 3);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ResonatingCrystalTileEntity) {
            ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) te;
            resonatingCrystalTileEntity.setPurity(purity);
            resonatingCrystalTileEntity.setStrength(strength);
            resonatingCrystalTileEntity.setEfficiency(efficiency);
            resonatingCrystalTileEntity.setPower(power);

            float radPurity = resonatingCrystalTileEntity.getPurity();
            float radRadius = DRRadiationManager.calculateRadiationRadius(resonatingCrystalTileEntity.getStrength(), resonatingCrystalTileEntity.getEfficiency(), radPurity);
            float radStrength = DRRadiationManager.calculateRadiationStrength(resonatingCrystalTileEntity.getStrength(), radPurity);
            Logging.message(player, "Crystal would produce " + radStrength + " radiation with a radius of " + radRadius);
        }
    }

    // Special == 0, normal
    // Special == 1, average random
    // Special == 2, best random
    // Special == 3, best non-overcharged
    // Special == 4, almost depleted
    public static void spawnRandomCrystal(World world, Random random, BlockPos pos, int special) {
        world.setBlockState(pos, ModBlocks.resonatingCrystalBlock.getStateFromMeta(0), 3);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ResonatingCrystalTileEntity) {
            ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) te;
            if (special >= 5) {
                resonatingCrystalTileEntity.setStrength(1);
                resonatingCrystalTileEntity.setPower(.05f);
                resonatingCrystalTileEntity.setEfficiency(1);
                resonatingCrystalTileEntity.setPurity(100);
            } else if (special >= 3) {
                resonatingCrystalTileEntity.setStrength(100);
                resonatingCrystalTileEntity.setPower(100);
                resonatingCrystalTileEntity.setEfficiency(100);
                resonatingCrystalTileEntity.setPurity(special == 4 ? 1 : 100);
            } else {
                resonatingCrystalTileEntity.setStrength(getRandomSpecial(random, special) * 3.0f + 0.01f);
                resonatingCrystalTileEntity.setPower(getRandomSpecial(random, special) * 60.0f + 0.2f);
                resonatingCrystalTileEntity.setEfficiency(getRandomSpecial(random, special) * 3.0f + 0.1f);
                resonatingCrystalTileEntity.setPurity(getRandomSpecial(random, special) * 10.0f + 5.0f);
            }
        }
    }

    public static void spawnRandomCrystal(World world, Random random, BlockPos pos, float str, float pow, float eff, float pur) {
        world.setBlockState(pos, ModBlocks.resonatingCrystalBlock.getStateFromMeta(0), 3);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ResonatingCrystalTileEntity) {
            ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) te;
            resonatingCrystalTileEntity.setStrength(Math.min(100.0f, random.nextFloat() * str * 3.0f + 0.01f));
            resonatingCrystalTileEntity.setPower(Math.min(100.0f, random.nextFloat() * pow * 60.0f + 0.2f));
            resonatingCrystalTileEntity.setEfficiency(Math.min(100.0f, random.nextFloat() * eff * 3.0f + 0.1f));
            resonatingCrystalTileEntity.setPurity(Math.min(100.0f, random.nextFloat() * pur * 10.0f + 5.0f));
        }
    }

    private static float getRandomSpecial(Random random, int special) {
        return special == 0 ? random.nextFloat() :
                special == 1 ? .5f : 1.0f;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }
}
