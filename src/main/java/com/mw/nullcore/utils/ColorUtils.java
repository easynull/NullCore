package com.mw.nullcore.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

import java.awt.*;

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

    /**
     * @param colors array[] of RGB colors for transitions between them
     * @param per duration (in ticks) of each color transition
     * @return interpolated color between the current and next point in the array
     */
    public static int arrayColor(int[] colors, float per){
        float progress = (ClientUtils.clientTick % per) / per;
        int index1 = (int) (ClientUtils.clientTick / per) % colors.length;
        int index2 = (index1 + 1) % colors.length;

        float[] argb1 = unpackRGBA(colors[index1] | 0xFF000000);
        float[] argb2 = unpackRGBA(colors[index2] | 0xFF000000);

        float red = argb1[1] + (argb2[1] - argb1[1]) * progress;
        float green = argb1[2] + (argb2[2] - argb1[2]) * progress;
        float blue = argb1[3] + (argb2[3] - argb1[3]) * progress;
        return ((int)(red * 255) << 16) | ((int)(green * 255) << 8) | (int)(blue * 255);
    }

    public static float[] interpolateColor(float speed, int... colors){
        if (speed == 0) {
            return ColorUtils.unpackRGBA(colors[0]);
        }
        float tick = (Mth.sin((ClientUtils.clientTick + RenderUtils.partialTick) * speed) * 0.5f + 0.5f);
        float segmentDuration = 1f / (colors.length - 1);
        int segment = (int)(tick / segmentDuration);
        float factor = (tick % segmentDuration) / segmentDuration;

        if (segment >= colors.length - 1) {
            segment = colors.length - 2;
            factor = 1f;
        }

        float[] startColor = ColorUtils.unpackRGBA(colors[segment]);
        float[] endColor = ColorUtils.unpackRGBA(colors[segment + 1]);

        return new float[] {Mth.lerp(factor, startColor[0], endColor[0]), Mth.lerp(factor, startColor[1], endColor[1]), Mth.lerp(factor, startColor[2], endColor[2]), Mth.lerp(factor, startColor[3], endColor[3])};
    }

    public static int getRainbowColor(float per) {
        int[] colors = new int[255];
        for (int i = 0; i < 255; i++) {
            float hue = (float) i / 255;
            colors[i] = Color.HSBtoRGB(hue, 1.0f, 1.0f) & 0xFFFFFF;
        }
        return arrayColor(colors, per);
    }

    public static int getRainbowColor() {
        return getRainbowColor(0.75f);
    }
}
