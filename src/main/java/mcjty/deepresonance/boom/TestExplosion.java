package mcjty.deepresonance.boom;

import elec332.core.explosion.Elexplosion;
import elec332.core.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.*;
import net.minecraft.world.World;

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
                        BlockPos targetPosition = this.getLocation().add(x, y, z);
                        double dist = Math.sqrt(getLocation().distanceSq(targetPosition));
                        if(dist < (double)this.getRadius()) {
                            Block block = WorldHelper.getBlockAt(this.getWorld(), targetPosition);
                            if(block != null && !block.isAir(this.getWorld(), targetPosition) && block.getBlockHardness(getWorld(), targetPosition) > 0 && (dist < (double)(this.getRadius() - 1.0F) || (double)this.getWorld().rand.nextFloat() > 0.7D)) {
                                block.onBlockExploded(getWorld(), targetPosition, this);
                            }
                        }
                    }
                }
            }
        }

    }

    @Override
    protected void damageEntities(float radius, float power) {
        if(!this.getWorld().isRemote) {
            radius *= 2.0F;
            final float minRadius = -radius - 1.0F, maxRadius = radius + 1.0F;
            BlockPos minCoord = this.getLocation();
            minCoord.add(minRadius, minRadius, minRadius);
            BlockPos maxCoord = this.getLocation();
            maxCoord.add(maxRadius, maxRadius, maxRadius);
            List allEntities = this.getWorld().getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB((double) minCoord.getX(), (double) minCoord.getY(), (double) minCoord.getZ(), (double) maxCoord.getX(), (double) maxCoord.getY(), (double) maxCoord.getZ()));

            for (Object allEntity : allEntities) {
                Entity entity = (Entity) allEntity;
                double distance = entity.getDistance((double) this.getLocation().getX(), (double) this.getLocation().getY(), (double) this.getLocation().getZ()) / (double) radius;
                if (distance <= 1.0D) {
                    double xDifference = entity.posX - (double) this.getLocation().getX();
                    double yDifference = entity.posY - (double) this.getLocation().getY();
                    double zDifference = entity.posZ - (double) this.getLocation().getZ();
                    double d1 = (double) MathHelper.sqrt_double(xDifference * xDifference + yDifference * yDifference + zDifference * zDifference);
                    xDifference /= d1;
                    yDifference /= d1;
                    zDifference /= d1;
                    double density = (double) this.getWorld().getBlockDensity(new Vec3((double) this.getLocation().getX(), (double) this.getLocation().getY(), (double) this.getLocation().getZ()), entity.getEntityBoundingBox());
                    double d2 = (1.0D - distance) * density;
                    int damage = (int) ((d2 * d2 + d2) / 2.0D * 8.0D * (double) power + 1.0D);
                    entity.attackEntityFrom(DamageSource.setExplosionSource(this), (float) damage);
                    entity.motionX += xDifference * d2;
                    entity.motionY += yDifference * d2;
                    entity.motionZ += zDifference * d2;
                }
            }
        }
    }
}
