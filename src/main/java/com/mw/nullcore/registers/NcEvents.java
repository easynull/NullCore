package com.mw.nullcore.registers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mw.nullcore.client.render.NcShaders;
import com.mw.nullcore.client.render.ShyBlockRender;
import com.mw.nullcore.client.render.ShyItemRender;
import com.mw.nullcore.client.render.ShyModel;
import com.mw.nullcore.core.NcUtils;
import com.mw.nullcore.core.managers.BiomeRulesManager;
import com.mw.nullcore.core.managers.LockableManager;
import com.mw.nullcore.core.managers.TracksManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

import static com.mw.nullcore.NullCore.ID;

@EventBusSubscriber(modid = ID, bus = EventBusSubscriber.Bus.MOD)
public final class NcEvents {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    private static void onRegistryRenders(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(NcEntities.SHYITEM.get(), ShyItemRender::new);
        event.registerEntityRenderer(NcEntities.SHYBLOCK.get(), ShyBlockRender::new);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    private static void onRegistryProviders(final RegisterParticleProvidersEvent event) {
        NcParticles.init();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    private static void onRegistryShaders(final RegisterShadersEvent event) {
        event.registerShader(NcShaders.SCREENPARTICLE);
    }

    public static void onRegistryResource(final AddServerReloadListenersEvent event) {
        event.addListener(ResourceLocation.fromNamespaceAndPath(ID, "biome_rules"), new BiomeRulesManager());
        event.addListener(ResourceLocation.fromNamespaceAndPath(ID, "tracks"), new TracksManager());
        event.addListener(ResourceLocation.fromNamespaceAndPath(ID, "lockable"), new LockableManager());
    }
}
