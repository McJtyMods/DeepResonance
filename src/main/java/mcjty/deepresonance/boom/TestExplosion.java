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

    @Override
    protected void damageEntities(float radius, float power) {
        if(!this.getWorld().isRemote) {
            radius *= 2.0F;
            BlockLoc minCoord = this.getLocation().copy();
            minCoord.add(-radius - 1.0F);
            BlockLoc maxCoord = this.getLocation().copy();
            maxCoord.add(radius + 1.0F);
            List allEntities = this.getWorld().getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox((double) minCoord.xCoord, (double) minCoord.yCoord, (double) minCoord.zCoord, (double) maxCoord.xCoord, (double) maxCoord.yCoord, (double) maxCoord.zCoord));
            Iterator i$ = allEntities.iterator();

            while(i$.hasNext()) {
                Entity entity = (Entity)i$.next();
                double distance = entity.getDistance((double) this.getLocation().xCoord, (double) this.getLocation().yCoord, (double) this.getLocation().zCoord) / (double)radius;
                if(distance <= 1.0D) {
                    double xDifference = entity.posX - (double)this.getLocation().xCoord;
                    double yDifference = entity.posY - (double)this.getLocation().yCoord;
                    double zDifference = entity.posZ - (double)this.getLocation().zCoord;
                    double d1 = (double)MathHelper.sqrt_double(xDifference * xDifference + yDifference * yDifference + zDifference * zDifference);
                    xDifference /= d1;
                    yDifference /= d1;
                    zDifference /= d1;
                    double density = (double) this.getWorld().getBlockDensity(Vec3.createVectorHelper((double) this.getLocation().xCoord, (double)this.getLocation().yCoord, (double)this.getLocation().zCoord), entity.boundingBox);
                    double d2 = (1.0D - distance) * density;
                    int damage = (int)((d2 * d2 + d2) / 2.0D * 8.0D * (double)power + 1.0D);
                    entity.attackEntityFrom(DamageSource.setExplosionSource(this), (float)damage);
                    entity.motionX += xDifference * d2;
                    entity.motionY += yDifference * d2;
                    entity.motionZ += zDifference * d2;
                }
            }
        }
    }
}
