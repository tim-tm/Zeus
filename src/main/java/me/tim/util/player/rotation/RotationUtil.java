package me.tim.util.player.rotation;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import me.tim.Statics;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import net.optifine.reflect.Reflector;

import javax.vecmath.Vector2f;
import java.util.List;

public class RotationUtil {
    public static Vector2f getRotations(Entity e) {
        double d0 = e.posX - Statics.getPlayer().posX;
        double d1 = e.posY - (Statics.getPlayer().posY + (Statics.getPlayer().getEyeHeight() - (e.height / 2)));
        double d2 = e.posZ - Statics.getPlayer().posZ;
        double d3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
        float yaw = (float) (MathHelper.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(d1, d3) * 180.0D / Math.PI));

        return new Vector2f(yaw, pitch);
    }

    public static Vector2f getRotations(Vec3 vec3) {
        double d0 = vec3.xCoord - Statics.getPlayer().posX;
        double d1 = vec3.yCoord - (Statics.getPlayer().posY + (Statics.getPlayer().getEyeHeight()));
        double d2 = vec3.zCoord - Statics.getPlayer().posZ;
        double d3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
        float yaw = (float) (MathHelper.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(d1, d3) * 180.0D / Math.PI));

        return new Vector2f(yaw, pitch);
    }

    public static Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * ((float) Math.PI / 180F));
        float f3 = MathHelper.sin(-pitch * ((float) Math.PI / 180F));
        return new Vec3(f1 * f2, f3, f * f2);
    }

    public static Entity rayCast(float range, Rotation rotation) {
        double d0 = range;
        double d1 = d0;
        Vec3 vec3 = Statics.getPlayer().getPositionEyes(Statics.getTimer().renderPartialTicks);
        boolean flag = false;
        int i = 3;

        if (d0 > 3.0D) {
            flag = true;
        }

        Vec3 vec31 = getVectorForRotation(rotation.getPitch(), rotation.getYaw());
        Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
        Entity pointedEntity = null;
        Vec3 vec33 = null;
        float f = 1.0F;
        List<Entity> list = Statics.getWorld().getEntitiesInAABBexcluding(Statics.getPlayer(), Statics.getPlayer().getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
            public boolean apply(Entity p_apply_1_) {
                return p_apply_1_.canBeCollidedWith();
            }
        }));
        double d2 = d1;

        for (int j = 0; j < list.size(); ++j) {
            Entity entity1 = list.get(j);
            float f1 = entity1.getCollisionBorderSize();
            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
            MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

            if (axisalignedbb.isVecInside(vec3)) {
                if (d2 >= 0.0D) {
                    pointedEntity = entity1;
                    vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                    d2 = 0.0D;
                }
            } else if (movingobjectposition != null) {
                double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                if (d3 < d2 || d2 == 0.0D) {
                    boolean flag1 = false;

                    if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                        flag1 = Reflector.callBoolean(entity1, Reflector.ForgeEntity_canRiderInteract);
                    }

                    if (!flag1 && entity1 == Statics.getPlayer().ridingEntity) {
                        if (d2 == 0.0D) {
                            pointedEntity = entity1;
                            vec33 = movingobjectposition.hitVec;
                        }
                    } else {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition.hitVec;
                        d2 = d3;
                    }
                }
            }
        }

        return pointedEntity;
    }

    public static Block rayCastBlock(float range, float yaw, float pitch) {
        Vec3 vec3 = Statics.getPlayer().getPositionEyes(1);
        Vec3 vec31 = getVectorForRotation(pitch, yaw);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);
        MovingObjectPosition pos = Statics.getWorld().rayTraceBlocks(vec3, vec32, false, false, true);
        return Statics.getWorld().getBlockState(pos.getBlockPos()).getBlock();
    }
}
