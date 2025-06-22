package com.mw.nullcore.utils;

import net.minecraft.client.Minecraft;

import java.io.FileFilter;

public final class ColorUtils {
    /**
     * Converts ARGB color to individual components (0.0-1.0)
     * @param argb ARGB color (0xAARRGGBB)
     * @return array [red, green, blue, alpha]
     */
    public static float[] unpackARGB(int argb) {
        return new float[]{((argb >> 16) & 0xFF) / 255.0f, ((argb >> 8)  & 0xFF) / 255.0f, (argb & 0xFF) / 255.0f, ((argb >> 24) & 0xFF) / 255.0f};
    }

    /**
     * Converts RGBA to individual components (0.0-1.0)
     * @param rgba RGBA rgba (0xRRGGBBAA)
     * @return array [red, green, blue, alpha]
     */
    public static float[] unpackRGBA(int rgba) {
        return new float[]{((rgba >> 24) & 0xFF) / 255.0f, ((rgba >> 16) & 0xFF) / 255.0f, ((rgba >> 8) & 0xFF) / 255.0f, (rgba & 0xFF) / 255.0f};
    }

    public static int packARGB(float red, float green, float blue, float alpha) {
        return ((int)(alpha * 255) << 24) | ((int)(red * 255) << 16) | ((int)(green * 255) << 8) | (int)(blue * 255);
    }
}
