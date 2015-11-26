package mcjty.deepresonance.blocks.crystals;

import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.lib.entity.GenericTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Random;
import java.util.UUID;

public class ResonatingCrystalTileEntity extends GenericTileEntity {

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

    private float powerPerTick = -1;    // Calculated value that contains the power/tick that is drained for this crystal.
    private int rfPerTick = -1;         // Calculated value that contains the RF/tick for this crystal.

    private boolean glowing = false;

    @Override
    public boolean canUpdate() {
        return false;
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
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void setPower(float power) {
        this.power = power;
        markDirty();
        // Don't do block update. We query power on demand from the tooltip of the crystal.
//        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void setEfficiency(float efficiency) {
        this.efficiency = efficiency;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void setPurity(float purity) {
        this.purity = purity;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void setGlowing(boolean glowing) {
        if (this.glowing == glowing) {
            return;
        }
        this.glowing = glowing;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
        return rfPerTick;
    }

    public static int getRfPerTick(float efficiency, float purity) {
        return (int) (ConfigMachines.Power.maximumRFPerTick * efficiency / 100.1f * (purity + 2.0f) / 102.0f + 1);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
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
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
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

    // Special == 0, normal
    // Special == 1, average random
    // Special == 2, best random
    // Special == 3, best non-overcharged
    public static void spawnRandomCrystal(World world, Random random, int x, int y, int z, int special) {
        world.setBlock(x, y, z, ModBlocks.resonatingCrystalBlock, 0, 3);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ResonatingCrystalTileEntity) {
            ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) te;
            if (special >= 3) {
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

    private static float getRandomSpecial(Random random, int special) {
        return special == 0 ? random.nextFloat() :
                special == 1 ? .5f : 1.0f;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }
}
