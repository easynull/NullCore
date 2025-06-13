package com.mw.nullcore.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.text.DecimalFormat;
import java.util.List;

public final class TextUtils {
    public static void addTooltipWithKey(int button, int colorButton, List<Component> adder, Component... components) {
        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), button)) {
            for (Component cm : components) {
                if (cm != null) adder.add(cm);
            }
        } else {
            adder.add(Component.translatable("tooltip.nullcore.info", Component.translatable(InputConstants.getKey(button, 0).toString()).withColor(colorButton)));
        }
    }

    public static String formatNum(float amount) {
        String[] sufx = {"", "K", "M", "G", "T"};
        int suffixIndex = 0;
        float scaledNumber = amount;
        while (scaledNumber >= 1000 && suffixIndex < sufx.length - 1) {
            scaledNumber /= 1000;
            suffixIndex++;
        }
        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(scaledNumber) + sufx[suffixIndex];
    }
}
