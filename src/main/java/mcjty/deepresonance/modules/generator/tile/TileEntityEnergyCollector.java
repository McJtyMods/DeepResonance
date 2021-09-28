package mcjty.deepresonance.modules.generator.tile;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import mcjty.deepresonance.modules.core.tile.TileEntityResonatingCrystal;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 30-7-2020
 */
public class TileEntityEnergyCollector extends AbstractTileEntityGeneratorComponent {

    private final Set<BlockPos> crystals;
    private final Set<LazyOptional<TileEntityResonatingCrystal>> crystalRefs;
    private boolean updateRefs = false;

    public TileEntityEnergyCollector() {
        super(GeneratorModule.TYPE_ENERGY_COLLECTOR.get());
        this.crystals = Sets.newHashSet();
        this.crystalRefs = Sets.newHashSet();
    }

    public void updateCrystals() {
        crystals.clear();
        crystalRefs.clear();
        if (grid != null) {
            Collection<TileEntityResonatingCrystal> crystalz = Sets.newHashSet();
            int rangeHor = GeneratorModule.collectorConfig.maxHorizontalCrystalDistance.get();
            int rangeVer = GeneratorModule.collectorConfig.maxVerticalCrystalDistance.get();
            for (int i = -rangeHor; i < rangeHor + 1; i++) {
                for (int j = -rangeHor; j < rangeHor + 1; j++) {
                    for (int k = -rangeHor; k < rangeVer + 1; k++) {
                        BlockPos pos = getBlockPos().offset(i, k, j);
                        TileEntity tile = level.getBlockEntity(pos);
                        //Also check if there are obstructions
                        if (tile instanceof TileEntityResonatingCrystal) {
                            RayTraceContext context = new RayTraceContext(
                                    new Vector3d(worldPosition.getX()+.5, worldPosition.getY()+.6, worldPosition.getZ()+.5),
                                    new Vector3d(pos.getX(), pos.getY(), pos.getZ()), RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, null);
                            BlockPos res = level.clip(context).getBlockPos();
                            if (res.equals(pos)) {
                                crystalz.add((TileEntityResonatingCrystal) tile);
                            } else {
                                System.out.println("Blocked crystal: " + pos + "  hit:" + res);
                            }
                        }
                    }
                }
            }
            //Closest first
            crystalz = crystalz.stream().sorted(Comparator.comparingDouble(t -> t.getBlockPos().distSqr(getBlockPos()))).collect(Collectors.toList());
            Iterator<TileEntityResonatingCrystal> it = crystalz.iterator();
            for (int i = 0; i < Math.min(grid.getMaxSupportedCrystals(), crystalz.size()); i++) {
                TileEntityResonatingCrystal crystal = it.next();
                crystals.add(crystal.getBlockPos().subtract(getBlockPos()));
                crystalRefs.add(crystal.getReference());
            }
        }
        markDirtyClient();
    }

    @SuppressWarnings("ConstantConditions")
    public int collectPower() {
        if (grid == null) {
            return 0;
        }
        updateRefs();
        int totalPower = 0;
        float fraction = 1;
        Collection<TileEntityResonatingCrystal> crystalTiles = Lists.newArrayList();
        for (LazyOptional<TileEntityResonatingCrystal> ref : crystalRefs) {
            TileEntityResonatingCrystal crystal = ref.orElse(null);
            if (crystal != null) {
                totalPower += crystal.providePower(1, true);
                crystalTiles.add(crystal);
            }
        }
        float maxPower = grid.getMaxPowerCollected();
        if (totalPower > maxPower) {
            fraction = maxPower / totalPower;
        }
        totalPower = 0;
        for (TileEntityResonatingCrystal crystal : crystalTiles) {
            totalPower += crystal.providePower(fraction, false);
        }
        return Math.min(totalPower, (int) maxPower);
    }

    private void updateRefs() {
        if (!updateRefs || Preconditions.checkNotNull(getLevel()).isClientSide()) {
            return;
        }
        crystalRefs.clear();
        if (crystals.isEmpty()) {
            return;
        }
        for (BlockPos pos : crystals) {
            TileEntity tile = level.getBlockEntity(getBlockPos().offset(pos));
            if (tile instanceof TileEntityResonatingCrystal) {
                crystalRefs.add(((TileEntityResonatingCrystal) tile).getReference());
            }
        }
        updateRefs = false;
    }

    @Override //No state change
    public void generatorTurnedOn(boolean on) {
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        ListNBT list = new ListNBT();
        for (BlockPos pos : crystals) {
            list.add(NBTUtil.writeBlockPos(pos));
        }
        tagCompound.put("crystals", list);
        return super.save(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        crystals.clear();
        ListNBT list = tagCompound.getList("crystals", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            crystals.add(NBTUtil.readBlockPos(list.getCompound(i)));
        }
        updateRefs = true;
    }

    public Set<BlockPos> getCrystals() {
        // @todo 1.16
//        if (!FMLHelper.getDist().isClient()) {
//            throw new UnsupportedOperationException();
//        }
        return crystals;
    }

}
