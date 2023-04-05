package me.tim.features.event;

import me.tim.features.event.api.Event;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class EventCollide extends Event {
    private final Block block;
    private BlockPos blockPos;
    private AxisAlignedBB axisAlignedBB;

    public EventCollide(Block block, BlockPos blockPos, AxisAlignedBB axisAlignedBB) {
        this.block = block;
        this.blockPos = blockPos;
        this.axisAlignedBB = axisAlignedBB;
    }

    public Block getBlock() {
        return block;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public AxisAlignedBB getAxisAlignedBB() {
        return axisAlignedBB;
    }

    public void setAxisAlignedBB(AxisAlignedBB axisAlignedBB) {
        this.axisAlignedBB = axisAlignedBB;
    }
}
