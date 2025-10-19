package com.mw.nullcore.core.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public record TransformPosed(Direction dir) {
    public BlockPos transform(BlockPos base, int offsetX, int offsetZ) {
        return switch (dir) {
            case NORTH -> base.offset(offsetX, 0, -offsetZ);
            case EAST -> base.offset(offsetZ, 0, offsetX);
            case SOUTH -> base.offset(-offsetX, 0, offsetZ);
            case WEST -> base.offset(-offsetZ, 0, -offsetX);
            default -> base;
        };
    }
}
