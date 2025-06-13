package com.mw.nullcore.utils;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class ClientUtils {
    private final static Minecraft mc = Minecraft.getInstance();
    public static int ticks;

    public static void onTicker(ClientTickEvent.Post event){
        if (!mc.isPaused()) ticks++;
    }
}
