package mcjty.deepresonance.modules.core.block;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.util.CrystalConfig;
import mcjty.deepresonance.setup.Registration;
import mcjty.deepresonance.util.Crystal;
import mcjty.deepresonance.util.ItemDataHelper;
import mcjty.deepresonance.modules.core.util.CrystalHelper;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ResonatingCrystalTileEntity extends GenericTileEntity {

    // The total maximum RF you can get out of a crystal with the following characteristics:
    //    * S: Strength (0-100%)
    //    * P: Purity (0-100%)
    //    * E: Efficiency (0-100%)
    // Is equal to:
    //    * MaxRF = FullMax * (S/100) * ((P+30)/130)
    // The RF/tick you can get out of a crystal with the above characteristics is:
    //    * RFTick = FullRFTick * (E/100.1) * ((P+2)/102) + 1           (the divide by 100.1 is to make sure we don't go above 20000)

    private float powerPerTick = -1;    // Calculated value that contains the power/tick that is drained for this crystal.
    private int rfPerTick = -1;         // Calculated value that contains the RF/tick for this crystal.

    private boolean glowing = false;

    public ResonatingCrystalTileEntity(BlockPos pos, BlockState state) {
        super(CoreModule.TYPE_RESONATING_CRYSTAL.get(), pos, state);
    }

    public double getStrength() {
        Crystal data = getData(Registration.CRYSTAL_DATA);
        return data.strength();
    }

    public double getPower() {
        Crystal data = getData(Registration.CRYSTAL_DATA);
        return data.power();
    }

    public double getEfficiency() {
        Crystal data = getData(Registration.CRYSTAL_DATA);
        return data.efficiency();
    }

    public double getPurity() {
        Crystal data = getData(Registration.CRYSTAL_DATA);
        return data.purity();
    }

    public boolean isGlowing() {
        return glowing;
    }

    public void setStrength(double strength) {
        Crystal data = getData(Registration.CRYSTAL_DATA);
        data = data.withStrength(strength);
        setData(Registration.CRYSTAL_DATA, data);
    }

    public boolean isEmpty() {
        return getPower() < mcjty.deepresonance.util.Constants.CRYSTAL_MIN_POWER;
    }

    public void setPower(double power) {
        boolean oldempty = isEmpty();
        this.setPower(power);
        boolean newempty = isEmpty();
        if (oldempty != newempty) {
            if (level != null) {
                if (getBlockState().getBlock() instanceof ResonatingCrystalBlock crystalBlock) {
                    level.setBlock(worldPosition, crystalBlock.getEmpty().defaultBlockState(), BlockResonatingPlate.UPDATE_ALL_IMMEDIATE);
                }
            }
            setChanged();
        }
    }

    public float getPowerPerTick() {
        if (powerPerTick < 0) {
            double totalRF = ResonatingCrystalTileEntity.getTotalPower(getStrength(), getPurity());
            double numticks = totalRF / ResonatingCrystalTileEntity.getRfPerTick(getEfficiency(), getPurity());
//            float numticks = totalRF / getRfPerTick();
            powerPerTick = (float)(100.0 / numticks);
        }
        return powerPerTick;
    }

    public static double getTotalPower(double strength, double purity) {
        return 1000.0 * CrystalConfig.MAX_POWER_STORED.get() * strength / 100.0 * (purity + 30.0) / 130.0;
    }

    public int getRfPerTick() {
        if (rfPerTick == -1) {
            rfPerTick = ResonatingCrystalTileEntity.getRfPerTick(getEfficiency(), getPurity());
        }
        return rfPerTick;
    }

    public static int getRfPerTick(double efficiency, double purity) {
        return (int) (CrystalConfig.MAX_POWER_TICK.get() * efficiency / 100.1 * (purity + 2.0) / 102.0 + 1);
    }

    public void setEfficiency(double efficiency) {
        Crystal data = getData(Registration.CRYSTAL_DATA);
        data = data.withEfficiency(efficiency);
        setData(Registration.CRYSTAL_DATA, data);
    }

    public void setPurity(double purity) {
        Crystal data = getData(Registration.CRYSTAL_DATA);
        data = data.withPurity(purity);
        setData(Registration.CRYSTAL_DATA, data);
    }

    public void setGlowing(boolean glowing) {
        if (this.glowing == glowing) {
            return;
        }
        this.glowing = glowing;
        markDirtyClient();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider provider) {
        boolean oldempty = isEmpty();
        super.onDataPacket(net, packet, provider);
        boolean newempty = isEmpty();
        if (oldempty != newempty) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tagCompound, HolderLookup.Provider provider) {
        glowing = tagCompound.getBoolean("glowing");
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tagCompound, HolderLookup.Provider provider) {
        tagCompound.putBoolean("glowing", glowing);
    }

    @Override
    public void loadAdditional(@Nonnull CompoundTag tagCompound, HolderLookup.Provider provider) {
        super.loadAdditional(tagCompound, provider);
        glowing = tagCompound.getBoolean("glowing");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound, HolderLookup.Provider provider) {
        super.saveAdditional(tagCompound, provider);
        tagCompound.putBoolean("glowing", glowing);
    }

    // Special == 0, normal
    // Special == 1, average random
    // Special == 2, best random
    // Special == 3, best non-overcharged
    // Special == 4, almost depleted
    public static void spawnRandomCrystal(Level world, Random random, BlockPos pos, int special) {
        world.setBlock(pos, CoreModule.RESONATING_CRYSTAL_GENERATED.block().get().defaultBlockState(), Block.UPDATE_ALL);
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof ResonatingCrystalTileEntity crystal) {
            if (special >= 5) {
                crystal.setStrength(1);
                crystal.setPower(.05f);
                crystal.setEfficiency(1);
                crystal.setPurity(100);
            } else if (special >= 3) {
                crystal.setStrength(100);
                crystal.setPower(100);
                crystal.setEfficiency(100);
                crystal.setPurity(special == 4 ? 1 : 100);
            } else {
                crystal.setStrength(getRandomSpecial(random, special) * 3.0f + 0.01f);
                crystal.setPower(getRandomSpecial(random, special) * 60.0f + 0.2f);
                crystal.setEfficiency(getRandomSpecial(random, special) * 3.0f + 0.1f);
                crystal.setPurity(getRandomSpecial(random, special) * 10.0f + 5.0f);
            }
        }
    }

    public static void spawnRandomCrystal(Level world, Random random, BlockPos pos, float str, float pow, float eff, float pur) {
        world.setBlock(pos, CoreModule.RESONATING_CRYSTAL_GENERATED.block().get().defaultBlockState(), Block.UPDATE_ALL);
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof ResonatingCrystalTileEntity crystal) {
            crystal.setStrength(Math.min(100.0f, random.nextFloat() * str * 3.0f + 0.01f));
            crystal.setPower(Math.min(100.0f, random.nextFloat() * pow * 60.0f + 0.2f));
            crystal.setEfficiency(Math.min(100.0f, random.nextFloat() * eff * 3.0f + 0.1f));
            crystal.setPurity(Math.min(100.0f, random.nextFloat() * pur * 10.0f + 5.0f));
        }
    }

    private static float getRandomSpecial(Random random, int special) {
        if (special == 0) {
            return random.nextFloat();
        } else if (special == 1) {
            return .5f;
        } else {
            return 1.0f;
        }
    }


}
