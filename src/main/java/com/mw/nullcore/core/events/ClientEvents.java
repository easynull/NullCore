package com.mw.nullcore.core.events;

import com.mw.nullcore.NullCore;
import com.mw.nullcore.core.NullEntities;
import com.mw.nullcore.client.render.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = NullCore.ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {
    @SubscribeEvent
    static void renderRegisters(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(NullEntities.advancedItem.get(), AdvancedItemRender::new);
    }
}
