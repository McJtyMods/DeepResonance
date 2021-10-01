package mcjty.deepresonance.modules.radiation.manager;

import mcjty.deepresonance.api.radiation.IRadiationSource;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.radiation.util.RadiationShieldRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by McJty
 */
class RadiationSource implements IRadiationSource, INBTSerializable<CompoundNBT> {

    private float radius;
    private float maxStrength;              // Roughly an indication of the amount of ticks.
    private float strength;
    private QuadTree radiationTree;

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getMaxStrength() {
        return maxStrength;
    }

    public void setMaxStrength(float maxStrength) {
        this.maxStrength = maxStrength;
    }

    public float getStrength() {
        return strength;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

    public QuadTree getRadiationTree(World world, int centerX, int centerY, int centerZ) {
        if (radiationTree == null) {
            radiationTree = new QuadTree((int) (centerX - radius - 1), (int) (centerY - radius - 1), (int) (centerZ - radius - 1), (int) (centerX + radius + 1), (int) (centerY + radius + 1), (int) (centerZ + radius + 1));
            BlockPos.Mutable pos = new BlockPos.Mutable();
            for (int x = (int) (centerX - radius); x < centerX + radius; x++) {
                for (int y = (int) (centerY - radius); y < centerY + radius; y++) {
                    for (int z = (int) (centerZ - radius); z < centerZ + radius; z++) {
                        pos.set(x, y, z);
                        BlockState block = world.getBlockState(pos);
                        float blocker = RadiationShieldRegistry.getBlocker(block);
                        if (blocker < 0.99f) {
                            radiationTree.addBlocker(pos, blocker);
                        }
                    }
                }
            }
        }
        return radiationTree;
    }

    // Update radiation for this radiation source
    // @param ticks is the amount of ticks to update for.
    @Override
    public void update(float radius, float maxStrenght, int ticks) {
        this.maxStrength = maxStrenght;
        this.radius = radius;
        float toadd = (float) (maxStrenght * RadiationModule.config.strengthGrowthFactor.get() * ticks);
        if ((strength + toadd) > maxStrenght) {
            toadd = maxStrenght - strength;
            if (toadd < 0) {
                toadd = 0;
            }
        }
        strength += toadd;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT ret = new CompoundNBT();
        ret.putFloat("radius", radius);
        ret.putFloat("maxStrength", maxStrength);
        ret.putFloat("strength", strength);
        return ret;
    }

    @Override
    public void deserializeNBT(CompoundNBT tagCompound) {
        this.radius = tagCompound.getFloat("radius");
        this.maxStrength = tagCompound.getFloat("maxStrength");
        this.strength = tagCompound.getFloat("strength");
    }

}
