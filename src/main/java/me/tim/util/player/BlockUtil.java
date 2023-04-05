package me.tim.util.player;

import me.tim.Statics;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;

import java.util.ArrayList;

public class BlockUtil {
    private final ArrayList<Vec3i> facings;
    
    private BlockPos pos;
    private Vec3i facing;

    public BlockUtil(BlockPos pos, Vec3i facing) {
        this.pos = pos;
        this.facing = facing;
        
        this.facings = new ArrayList<>();
        for (EnumFacing value : EnumFacing.values()) {
            this.facings.add(value.getDirectionVec());
        }
        this.facings.add(new Vec3i(-1, 0, -1));
        this.facings.add(new Vec3i(1, 0, -1));
        this.facings.add(new Vec3i(-1, 0, 1));
        this.facings.add(new Vec3i(1, 0, 1));
    }

    public BlockUtil() {
        this(null, null);
    }

    public void findPos(BlockPos pos) {
        for (Vec3i facing : this.facings) {
            if (!Statics.getWorld().isAirBlock(pos.add(facing))) {
                this.pos = pos.add(facing);
                this.facing = facing;
            }
        }
    }

    public void reset() {
        this.pos = null;
        this.facing = null;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public Vec3i getFacing() {
        return facing;
    }

    public void setFacing(Vec3i facing) {
        this.facing = facing;
    }
}
