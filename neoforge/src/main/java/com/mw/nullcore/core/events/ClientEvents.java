package com.mw.nullcore.core.events;

import com.mw.nullcore.NullCore;
import com.mw.nullcore.Utils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import static com.mw.nullcore.NullCore.ID;

@EventBusSubscriber(modid = ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {
    public static void tickClient(ClientTickEvent.Post event) {
        Utils.Client.tickClient();
    }

    @SubscribeEvent
    public static void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
        NullCore.registerEntityRenderers(event::registerEntityRenderer);
    }
}
