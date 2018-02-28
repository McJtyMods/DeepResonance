package mcjty.deepresonance.boom;

import elec332.core.explosion.Elexplosion;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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

    @Override
    public void explode() {
        if(!this.getWorld().isRemote) {
            for(int x = (int)(-this.getRadius()); x < this.getRadius(); ++x) {
                for(int y = (int)(-this.getRadius()); y < this.getRadius(); ++y) {
                    for(int z = (int)(-this.getRadius()); z < this.getRadius(); ++z) {
                        BlockPos targetPosition = this.getLocation().add(x, y, z);
                        double dist = Math.sqrt(getLocation().distanceSq(targetPosition));
                        if(dist < this.getRadius()) {
                            Block block = this.getWorld().getBlockState(targetPosition).getBlock();
                            IBlockState state = this.getWorld().getBlockState(targetPosition);
                            if(block != null && !block.isAir(state, this.getWorld(), targetPosition) && block.getBlockHardness(state, getWorld(), targetPosition) > 0 && (dist < (this.getRadius() - 1.0F) || this.getWorld().rand.nextFloat() > 0.7D)) {
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
            List allEntities = this.getWorld().getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(minCoord.getX(), minCoord.getY(), minCoord.getZ(), maxCoord.getX(), maxCoord.getY(), maxCoord.getZ()));

            for (Object allEntity : allEntities) {
                Entity entity = (Entity) allEntity;
                double distance = entity.getDistance(this.getLocation().getX(), this.getLocation().getY(), this.getLocation().getZ()) / radius;
                if (distance <= 1.0D) {
                    double xDifference = entity.posX - this.getLocation().getX();
                    double yDifference = entity.posY - this.getLocation().getY();
                    double zDifference = entity.posZ - this.getLocation().getZ();
                    double d1 = Math.sqrt(xDifference * xDifference + yDifference * yDifference + zDifference * zDifference);
                    xDifference /= d1;
                    yDifference /= d1;
                    zDifference /= d1;
                    double density = this.getWorld().getBlockDensity(new Vec3d(this.getLocation().getX(), this.getLocation().getY(), this.getLocation().getZ()), entity.getEntityBoundingBox());
                    double d2 = (1.0D - distance) * density;
                    int damage = (int) ((d2 * d2 + d2) / 2.0D * 8.0D * power + 1.0D);
                    //@todo
//                    entity.attackEntityFrom(DamageSource.setExplosionSource(this), (float) damage);
                    entity.motionX += xDifference * d2;
                    entity.motionY += yDifference * d2;
                    entity.motionZ += zDifference * d2;
                }
            }
        }
    }
}
