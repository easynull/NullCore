package com.mw.nullcore.events;

import com.mw.nullcore.NullCore;
import com.mw.nullcore.Utils;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import static com.mw.nullcore.NullCore.ID;

@EventBusSubscriber(modid = ID, bus = EventBusSubscriber.Bus.MOD)
public final class ClientEvents {
    public static void tickClient(ClientTickEvent.Post event) {
        if (!Minecraft.getInstance().isPaused()){
            Utils.clientTick++;
        }
    }

    @SubscribeEvent
    public static void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
        NullCore.registerEntityRenderers(event::registerEntityRenderer);
    }
}
