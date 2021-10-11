package mcjty.deepresonance.modules.core.block;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.client.ModelLoaderCoreModule;
import mcjty.deepresonance.modules.core.util.CrystalHelper;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class ResonatingCrystalTileEntity extends GenericTileEntity implements ITickableTileEntity {

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

    public ResonatingCrystalTileEntity() {
        super(CoreModule.TYPE_RESONATING_CRYSTAL.get());
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
        return power < mcjty.deepresonance.util.Constants.CRYSTAL_MIN_POWER;
    }

    public void setPower(float power) {
        boolean oldempty = isEmpty();
        this.power = power;
        setChanged();
        boolean newempty = isEmpty();
        if (oldempty != newempty) {
            markDirtyClient();
        }
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
        return 1000.0f * CoreModule.crystalConfig.MAX_POWER_STORED.get() * strength / 100.0f * (purity + 30.0f) / 130.0f;
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

    public static int getRfPerTick(float efficiency, float purity) {
        return (int) (CoreModule.crystalConfig.MAX_POWER_TICK.get() * efficiency / 100.1f * (purity + 2.0f) / 102.0f + 1);
    }


    @Override
    public void tick() {
        if (!level.isClientSide()) {
            todoCrystals.add(this);
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
        if (level != null) {
            markDirtyClient();
        } else {
            setChanged();
        }
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
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);

        strength = tagCompound.getFloat("strength");
        power = tagCompound.getFloat("power");
        efficiency = tagCompound.getFloat("efficiency");
        purity = tagCompound.getFloat("purity");
    }


    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        tagCompound.putFloat("strength", strength);
        tagCompound.putFloat("power", power);
        tagCompound.putFloat("efficiency", efficiency);
        tagCompound.putFloat("purity", purity);
        return super.save(tagCompound);
    }

    // @todo 1.16
//    @Override
//    public void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorBlock hitData) {
//        DecimalFormat decimalFormat = new DecimalFormat("#.#");
//        decimalFormat.setRoundingMode(RoundingMode.DOWN);
//        CompoundNBT tag = hitData.getData();
//        float power = tag.getFloat("power");
//        BlockCrystal.addBasicInformation(information::addInformation, tag, power, information.getProviderType() == InfoMod.WAILA);
//        getModifiers().forEach(mod -> {
//            if (mod instanceof IInfoProvider) {
//                ((IInfoProvider) mod).addInformation(information, hitData);
//            }
//        });
//        if (information.isDebugMode() == Boolean.TRUE) { //Debug, no translation
//            information.addInformation("Power: " + decimalFormat.format(power) + "%");
//        } else if (information.getProviderType() == InfoMod.TOP) {
//            information.addInformation(new StringTextComponent("Power: " + decimalFormat.format(power) + "%").applyTextStyle(TextFormatting.YELLOW));
//            IProbeInfo probeInfo = (IProbeInfo) information.getInformationComponent();
//            probeInfo.progress((int) power, 100, probeInfo.defaultProgressStyle()
//                    .suffix("%")
//                    .width(40)
//                    .height(10)
//                    .showText(false)
//                    .filledColor(0xffff0000)
//                    .alternateFilledColor(0xff990000));
//        }
//    }
//
//    @Override
//    public void gatherInformation(@Nonnull CompoundNBT tag, @Nonnull ServerPlayerEntity player, @Nonnull IInfoDataAccessorBlock hitData) {
//        tag.putFloat("strength", getStrength());
//        tag.putFloat("efficiency", getEfficiency());
//        tag.putFloat("purity", getPurity());
//        tag.putFloat("power", getPower());
//        getModifiers().forEach(mod -> {
//            if (mod instanceof IInfoProvider) {
//                ((IInfoProvider) mod).gatherInformation(tag, player, hitData);
//            }
//        });
//    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        IModelData tileData = new ModelDataMap.Builder().build();
        tileData.setData(ModelLoaderCoreModule.POWER, getPower());
        tileData.setData(ModelLoaderCoreModule.PURITY, getPurity());
        return tileData;
    }

}
