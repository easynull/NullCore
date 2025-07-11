package com.mw.nullcore.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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

    public static void forEachCircle(BlockPos center, int radius, int verticalRadius, Consumer<BlockPos> action) {
        int radiusSq = radius * radius;
        for(int x = -radius; x <= radius; ++x) {
            for(int y = 0; y <= verticalRadius; ++y) {
                for(int z = -radius; z <= radius; ++z) {
                    if (x * x + y * y + z * z <= radiusSq) {
                        action.accept(center.offset(x, y, z));
                    }
                }
            }
        }
    }

    private static void forEachDiamond(BlockPos center, int radius, int height, Consumer<BlockPos> action) {
        if (radius <= 0 || height <= 0) return;
        for (int y = 0; y <= height; y++) {
            int currentRadius = radius * (height - y) / height;
            for (int x = -currentRadius; x <= currentRadius; x++) {
                for (int z = -currentRadius; z <= currentRadius; z++) {
                    if (Math.abs(x) + Math.abs(z) <= currentRadius) {
                        action.accept(center.offset(x, y, z));
                        if (y != 0) {
                            action.accept(center.offset(x, -y, z));
                        }
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
}
