package com.mw.nullcore.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.text.DecimalFormat;
import java.util.*;

public final class TextUtils {
    /**
     * @param button takes the button code and is responsible for calling a specific tooltip(components) when pressed
     * @param colorButton Responsible for the display color of the button in the main text
     */
    public static void addTooltipWithKey(int button, int colorButton, List<Component> adder, Component... components) {
        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), button)) {
            for (Component cm : components) {
                if (cm != null) adder.add(cm);
            }
        } else {
            adder.add(Component.translatable("tooltip.nullcore.info", Component.translatable(InputConstants.getKey(button, 0).toString()).withColor(colorButton)));
        }
    }

    /**
     * @param amount automatically converts to a shortened form of the number
     * @return 1000 == 1К; 1000000 == 1M; 1000000000 == 1G; 1000000000000 = 1T...
     */
    public static String formatNum(float amount) {
        String[] sufx = {"", "K", "M", "B", "T", "Qa", "Qt", "Sx", "Sp", "Oc", "N", "D"};
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
