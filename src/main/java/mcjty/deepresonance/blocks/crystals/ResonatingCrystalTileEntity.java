package mcjty.deepresonance.blocks.crystals;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.collector.EnergyCollectorTileEntity;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.deepresonance.radiation.SuperGenerationConfiguration;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;
import java.util.UUID;

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
    private float danger = 0;           // Current danger status
    private int pulses = 0;             // Number of EMP pulses since last tick

    private float powerPerTick = -1;    // Calculated value that contains the power/tick that is drained for this crystal.
    private int rfPerTick = -1;         // Calculated value that contains the RF/tick for this crystal.

    private boolean glowing = false;

    public int getResistance() {
        return resistance;
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
        if (!world.isRemote && power > 0) {
            boolean dirty = false;
            if (cooldown > 0) {
                // We're still cooling down
                cooldown -= 1000;   // 1000 microticks
                if (cooldown < 0) {
                    cooldown = 0;
                }
                resistance += 1;     // Slight increase in resistance here as well
                if (resistance > SuperGenerationConfiguration.maxResistance) {
                    resistance = SuperGenerationConfiguration.maxResistance;
                }
                dirty = true;
            } else if (resistance < SuperGenerationConfiguration.maxResistance) {
                // We're cool, so increase our resistance again
                resistance += SuperGenerationConfiguration.resistanceIncreasePerTick;
                if (resistance > SuperGenerationConfiguration.maxResistance) {
                    resistance = SuperGenerationConfiguration.maxResistance;
                }
                dirty = true;
            }

            // How many microticks do we have left in this tick to use for pulses
            int microticksleft = 1000;
            while (pulses > 0) {
                // When handling multiple pulses in a single tick we're going to do as if these
                // pulses arrive evenly spread in that tick (in microticks). We're also going to be
                // gentle and assume the first of these pulses will arrive after the cooldown counter
                // has gotten a chance to do its work. Obviously this only works if there is less
                // then a single tick of cooldown left
                dirty = true;
                pulses--;
                // Handle a pulse
                if (cooldown < 1000) {
                    // We can let the cooldown expire in this tick and postpone the pulse until after
                    // that. Of course we can only do that if we have enough microticks left in this tick
                    // for us to consume
                    if (microticksleft >= cooldown) {
                        microticksleft -= cooldown;
                        cooldown = 0;
                    } else {
                        cooldown -= microticksleft;
                        microticksleft = 0;
                    }
                }
                handleSinglePulse();
            }

            if (danger > 0) {
                dirty = true;
                // We're currently having some danger issues
                handleDanger();
            }

            if (dirty) {
                markDirtyQuick();
            }
        }
    }

    private void handleDanger() {
        // The 'danger' value accumulates. Every time we handle danger there is a chance
        // we act on it. Not acting on danger is actually a bad thing as it means the danger will
        // stay and possibly be augmented in the near future
        if (world.rand.nextFloat() < SuperGenerationConfiguration.dangerHandlingChance) {
            // We handle the danger. How much do we handle?
            float tohandle = world.rand.nextFloat() * danger;
            danger -= tohandle;
            if (tohandle > SuperGenerationConfiguration.dangerExplosionThresshold) {
                ResonatingCrystalBlock.explode(world, pos, true);
            } else if (tohandle > SuperGenerationConfiguration.dangerBigDamageThresshold) {
                // Damage crystal
                setPower(Math.max(0, power-10));
                setPurity(Math.max(1, purity-10));
            } else if (tohandle > SuperGenerationConfiguration.dangerSmallDamageThresshold) {
                // Damage crystal
                setPower(Math.max(0, power-1));
                setPurity(Math.max(1, purity-1));
            } // Otherwise we just got lucky
        }
    }

    private void handleSinglePulse() {
        // We got a pulse. If our cooldown is > 0 we have to do some bad things
        if (cooldown > 0) {
            // The bad things depend on how far we actually are from our current resistance value
            float badness = (float) cooldown / resistance;
            danger += badness;

            // Decrease resistance as well but not as much
            resistance = (int) (resistance - SuperGenerationConfiguration.resistanceDecreasePerPulse * (1.0f-badness));
            if (resistance < 1) {
                resistance = 1;
            }
        } else {
            // Otherwise we can decrease our resistance a bit
            resistance -= SuperGenerationConfiguration.resistanceDecreasePerPulse;
            if (resistance < 1) {
                resistance = 1;
            }
        }

        // Now we have to set the cooldown back to our resistance
        cooldown = resistance;
    }

    // A pulse is received
    public void pulse() {
        pulses++;
        markDirtyQuick();
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
            float numticks = totalRF / getRfPerTick();
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
        if (resistance < SuperGenerationConfiguration.maxResistance) {
            float factor = (float) resistance / SuperGenerationConfiguration.maxResistance;
            rfPerTick = (int) (rfPerTick / factor);
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
        danger = tagCompound.getFloat("danger");
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
        tagCompound.setFloat("danger", danger);
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
        WorldHelper.setBlockState(world, pos, ModBlocks.resonatingCrystalBlock.getStateFromMeta(0), 3);
        TileEntity te = WorldHelper.getTileAt(world, pos);
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
        WorldHelper.setBlockState(world, pos, ModBlocks.resonatingCrystalBlock.getStateFromMeta(0), 3);
        TileEntity te = WorldHelper.getTileAt(world, pos);
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
        WorldHelper.setBlockState(world, pos, ModBlocks.resonatingCrystalBlock.getStateFromMeta(0), 3);
        TileEntity te = WorldHelper.getTileAt(world, pos);
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
