package com.mw.nullcore.core.events;

import com.mw.nullcore.NullCore;
import com.mw.nullcore.core.entities.NullEntities;
import com.mw.nullcore.core.render.AdvancedItemRender;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@EventBusSubscriber(modid = NullCore.ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class RegisterRenders {

    @SubscribeEvent
    static void registers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(NullEntities.advancedItem.get(), AdvancedItemRender::new);
    }
}
