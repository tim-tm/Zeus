package me.tim.util.player;

import me.tim.Statics;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BlockUtil {
    private BlockPos pos;
    private EnumFacing enumFacing;

    public BlockUtil(BlockPos pos, EnumFacing enumFacing) {
        this.pos = pos;
        this.enumFacing = enumFacing;
    }

    public BlockUtil() {
        this(null, null);
    }

    public void find(BlockPos pos1) {
        for (int x = -1; x <= 1; x += 2) {
            if (!Statics.getWorld().isAirBlock(pos1.add(x, 0, 0))) {
                this.pos = pos1.add(x, 0, 0);
                this.enumFacing = x < 0 ? EnumFacing.EAST : EnumFacing.WEST;
            }
        }

        if (!Statics.getWorld().isAirBlock(pos1.add(0, -1, 0))) {
            this.pos = pos1.add(0, -1, 0);
            this.enumFacing = EnumFacing.UP;
        }

        for (int z = -1; z <= 1; z += 2) {
            if (!Statics.getWorld().isAirBlock(pos1.add(0, 0, z))) {
                this.pos = pos1.add(0, 0, z);
                this.enumFacing = z < 0 ? EnumFacing.SOUTH : EnumFacing.NORTH;
            }
        }
    }

    public void reset() {
        this.pos = null;
        this.enumFacing = null;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public EnumFacing getEnumFacing() {
        return enumFacing;
    }

    public void setEnumFacing(EnumFacing enumFacing) {
        this.enumFacing = enumFacing;
    }
}
