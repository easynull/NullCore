package com.mw.nullcore.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public final class BlockUtils {
    public static void forEachCube(BlockPos center, int radius, Consumer<BlockPos> action) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    action.accept(center.offset(x, y, z));
                }
            }
        }
    }

    public static void forEachSphere(BlockPos center, int radius, Consumer<BlockPos> action) {
        int radiusSq = radius * radius;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radiusSq) {
                        action.accept(center.offset(x, y, z));
                    }
                }
            }
        }
    }

    public static void forEachNeighbor(BlockPos pos, Consumer<BlockPos> action) {
        for (Direction dir : Direction.values()) {
            action.accept(pos.relative(dir));
        }
    }

    public static ToIntFunction<BlockState> lightLit(int value, int baseValue) {
        return state -> state.getValue(BlockStateProperties.LIT) ? value : baseValue;
    }

    public static boolean isFluid(BlockState state) {
        return state.getFluidState().isSource();
    }

    public static Direction[] getHorizontals() {
        HashSet<Direction> check = new HashSet<>();
        for (Direction face : Direction.values()) {
            if (face == Direction.NORTH || face == Direction.EAST || face == Direction.SOUTH || face == Direction.WEST) {
                check.add(face);
            }
        }
        return check.toArray(new Direction[0]);
    }
}
