package mcjty.deepresonance.boom;

import elec332.core.explosion.Elexplosion;
import elec332.core.util.BlockLoc;
import elec332.core.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Elec332 on 13-8-2015.
 */
public class TestExplosion extends Elexplosion{
    public TestExplosion(World world, Entity entity, double x, double y, double z, float size) {
        super(world, entity, x, y, z, size);
    }

    @Override
    protected void preExplode() {
        damageEntities(getRadius(), 4.0f);
    }

    protected void doExplode() {
        if(!this.getWorld().isRemote) {
            for(int x = (int)(-this.getRadius()); (float)x < this.getRadius(); ++x) {
                for(int y = (int)(-this.getRadius()); (float)y < this.getRadius(); ++y) {
                    for(int z = (int)(-this.getRadius()); (float)z < this.getRadius(); ++z) {
                        BlockLoc targetPosition = this.getLocation().copy().translate(new BlockLoc(x, y, z));
                        double dist = this.getLocation().distance(targetPosition);
                        if(dist < (double)this.getRadius()) {
                            Block block = WorldHelper.getBlockAt(this.getWorld(), targetPosition);
                            if(block != null && !block.isAir(this.getWorld(), targetPosition.xCoord, targetPosition.yCoord, targetPosition.zCoord) && block != Blocks.bedrock && (dist < (double)(this.getRadius() - 1.0F) || (double)this.getWorld().rand.nextFloat() > 0.7D)) {
                                block.onBlockExploded(getWorld(), targetPosition.xCoord, targetPosition.yCoord, targetPosition.zCoord, this);
                            }
                        }
                    }
                }
            }
        }

    }
}
