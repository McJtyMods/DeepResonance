package mcjty.deepresonance.modules.core.block;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.client.ModelLoaderCoreModule;
import mcjty.deepresonance.modules.core.util.CrystalConfig;
import mcjty.deepresonance.modules.core.util.CrystalHelper;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.util.Constants;

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

    private double strength = 1.0;
    private double power = 1.0;         // Default 1% power
    private double efficiency = 1.0;    // Default 1%
    private double purity = 1.0;        // Default 1% purity

    private float powerPerTick = -1;    // Calculated value that contains the power/tick that is drained for this crystal.
    private int rfPerTick = -1;         // Calculated value that contains the RF/tick for this crystal.

    private boolean glowing = false;

    public ResonatingCrystalTileEntity() {
        super(CoreModule.TYPE_RESONATING_CRYSTAL.get());
    }

    public double getStrength() {
        return strength;
    }

    public double getPower() {
        return power;
    }

    public double getEfficiency() {
        return efficiency;
    }

    public double getPurity() {
        return purity;
    }

    public boolean isGlowing() {
        return glowing;
    }


    // We enqueue crystals for processing later
    public static Set<ResonatingCrystalTileEntity> todoCrystals = new HashSet<>();

    public void setStrength(double strength) {
        this.strength = strength;
        setChanged();
    }

    public boolean isEmpty() {
        return power < mcjty.deepresonance.util.Constants.CRYSTAL_MIN_POWER;
    }

    public void setPower(double power) {
        boolean oldempty = isEmpty();
        this.power = power;
        setChanged();
        boolean newempty = isEmpty();
        if (oldempty != newempty) {
            if (level != null) {
                if (getBlockState().getBlock() instanceof ResonatingCrystalBlock) {
                    ResonatingCrystalBlock crystalBlock = (ResonatingCrystalBlock) getBlockState().getBlock();
                    level.setBlock(worldPosition, crystalBlock.getEmpty().defaultBlockState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
                }
            }
            setChanged();
        }
    }

    public float getPowerPerTick() {
        if (powerPerTick < 0) {
            double totalRF = ResonatingCrystalTileEntity.getTotalPower(strength, purity);
            double numticks = totalRF / ResonatingCrystalTileEntity.getRfPerTick(efficiency, purity);
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
            rfPerTick = ResonatingCrystalTileEntity.getRfPerTick(efficiency, purity);
        }

        // If we are super generating then we modify the RF here. To see that we're doing this we
        // can basically check our resistance value

        // resistance 1: factor 20
        // resistance MAX: factor 1


//        if (resistance < SuperGenerationConfiguration.maxResistance) {
//            float factor = ((SuperGenerationConfiguration.maxResistance - resistance) * 19.0f / SuperGenerationConfiguration.maxResistance) + 1.0f;
//            System.out.println("rfPerTick = " + rfPerTick + ", factor = " + factor);
//            return (int) (rfPerTick * factor);
//        }

        return rfPerTick;
    }

    public static int getRfPerTick(double efficiency, double purity) {
        return (int) (CrystalConfig.MAX_POWER_TICK.get() * efficiency / 100.1 * (purity + 2.0) / 102.0 + 1);
    }


//    @Override
//    protected void tickServer() {
//        todoCrystals.add(this);
//    }

    public void setEfficiency(double efficiency) {
        this.efficiency = efficiency;
        setChanged();
    }

    public void setPurity(double purity) {
        this.purity = purity;
        setChanged();
    }

    public void setGlowing(boolean glowing) {
        if (this.glowing == glowing) {
            return;
        }
        this.glowing = glowing;
        markDirtyClient();
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        boolean oldempty = isEmpty();
        boolean oldVeryPure = CrystalHelper.isVeryPure(getPurity());
        super.onDataPacket(net, packet);
        boolean newempty = isEmpty();
        if (oldempty != newempty || oldVeryPure != CrystalHelper.isVeryPure(getPurity())) {
            requestModelDataUpdate();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    @Override
    protected void loadInfo(CompoundNBT tagCompound) {
        super.loadInfo(tagCompound);
        if (tagCompound.contains("Info")) {
            CompoundNBT info = tagCompound.getCompound("Info");
            strength = info.getDouble("strength");
            power = info.getDouble("power");
            efficiency = info.getDouble("efficiency");
            purity = info.getDouble("purity");
        }
    }

    @Override
    protected void saveInfo(CompoundNBT tagCompound) {
        super.saveInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        info.putDouble("strength", strength);
        info.putDouble("power", power);
        info.putDouble("efficiency", efficiency);
        info.putDouble("purity", purity);
    }

    @Override
    public void load(@Nonnull CompoundNBT tagCompound) {
        super.load(tagCompound);
        glowing = tagCompound.getBoolean("glowing");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundNBT tagCompound) {
        super.saveAdditional(tagCompound);
        tagCompound.putBoolean("glowing", glowing);
    }

    @Override
    public void loadClientDataFromNBT(CompoundNBT tagCompound) {
        glowing = tagCompound.getBoolean("glowing");
    }

    @Override
    public void saveClientDataToNBT(CompoundNBT tagCompound) {
        tagCompound.putBoolean("glowing", glowing);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        IModelData tileData = new ModelDataMap.Builder().build();
        tileData.setData(ModelLoaderCoreModule.POWER, getPower());
        tileData.setData(ModelLoaderCoreModule.PURITY, getPurity());
        return tileData;
    }


    // Special == 0, normal
    // Special == 1, average random
    // Special == 2, best random
    // Special == 3, best non-overcharged
    // Special == 4, almost depleted
    public static void spawnRandomCrystal(World world, Random random, BlockPos pos, int special) {
        world.setBlock(pos, CoreModule.RESONATING_CRYSTAL_GENERATED.get().defaultBlockState(), Constants.BlockFlags.DEFAULT);
        TileEntity te = world.getBlockEntity(pos);
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
        world.setBlock(pos, CoreModule.RESONATING_CRYSTAL_GENERATED.get().defaultBlockState(), Constants.BlockFlags.DEFAULT);
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof ResonatingCrystalTileEntity) {
            ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) te;
            resonatingCrystalTileEntity.setStrength(Math.min(100.0f, random.nextFloat() * str * 3.0f + 0.01f));
            resonatingCrystalTileEntity.setPower(Math.min(100.0f, random.nextFloat() * pow * 60.0f + 0.2f));
            resonatingCrystalTileEntity.setEfficiency(Math.min(100.0f, random.nextFloat() * eff * 3.0f + 0.1f));
            resonatingCrystalTileEntity.setPurity(Math.min(100.0f, random.nextFloat() * pur * 10.0f + 5.0f));
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
