package com.mw.nullcore.registers;

import com.mw.nullcore.client.render.NcShaders;
import com.mw.nullcore.client.render.ShyBlockRender;
import com.mw.nullcore.client.render.ShyItemRender;
import com.mw.nullcore.core.managers.BiomeRulesManager;
import com.mw.nullcore.core.managers.LockableManager;
import com.mw.nullcore.core.managers.TracksManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

import static com.mw.nullcore.NullCore.ID;

@EventBusSubscriber(modid = ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class NcClient {
    @SubscribeEvent
    public static void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(NcEntities.SHYITEM.get(), ShyItemRender::new);
        event.registerEntityRenderer(NcEntities.SHYBLOCK.get(), ShyBlockRender::new);
    }

    @SubscribeEvent
    public static void registerProviders(RegisterParticleProvidersEvent event) {
        NcParticles.init();
    }

    @SubscribeEvent
    public static void shaderRegistry(RegisterShadersEvent event) {
        event.registerShader(NcShaders.SCREENPARTICLE);
    }
}
