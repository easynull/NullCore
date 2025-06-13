package com.mw.nullcore.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Vector3f;

public final class MthUtils {
    public static final RandomSource random = RandomSource.createNewThreadLocalInstance();

    public static boolean chance(float chance) {
        return random.nextFloat() < Mth.clamp(chance, 0, (byte)1);
    }

    public static float normalAngle(float angle) {
        angle %= 360.0F;
        if (angle > 180.0F) angle -= 360.0F;
        if (angle < -180.0F) angle += 360.0F;
        return angle;
    }

    public static Vector3f lerpVec3f(float delta, BlockPos start, BlockPos end) {
        return new Vector3f(Mth.lerp(delta, start.getX(), end.getX()), Mth.lerp(delta, start.getY(), end.getY()), Mth.lerp(delta, start.getZ(), end.getZ()));
    }

    public static String formatRealTime(long gameTime) {
        long totalSeconds = gameTime / 20;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
