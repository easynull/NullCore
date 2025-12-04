package com.mw.nullcore.registers;

import com.mw.nullcore.client.particle.screen.ParticleEmitterHandler;
import com.mw.nullcore.client.render.NcShaders;
import com.mw.nullcore.client.render.ShyBlockRender;
import com.mw.nullcore.client.render.ShyItemRender;
import com.mw.nullcore.core.NcUtils;
import com.mw.nullcore.core.items.CreativeContent;
import com.mw.nullcore.core.items.GuiRenderable;
import com.mw.nullcore.core.managers.BiomeRulesManager;
import com.mw.nullcore.core.managers.LockableManager;
import com.mw.nullcore.core.managers.TracksManager;
import com.mw.nullcore.core.network.StagerPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.mw.nullcore.NullCore.ID;
import static com.mw.nullcore.core.network.NetworkHandler.registerClientChannel;

@EventBusSubscriber(modid = ID, bus = EventBusSubscriber.Bus.MOD)
public final class NcEvents {
    @SubscribeEvent
    private static void onClient(final FMLClientSetupEvent event) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof GuiRenderable supplier) {
                ParticleEmitterHandler.registerEmitters(item, supplier);
            }
        }
    }

    @SubscribeEvent
    private static void onRegistryRenders(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(NcEntities.SHYITEM.get(), ShyItemRender::new);
        event.registerEntityRenderer(NcEntities.SHYBLOCK.get(), ShyBlockRender::new);
    }

//    @SubscribeEvent
//    private static void onRegistryProviders(final RegisterParticleProvidersEvent event) {
//        NcParticles.registerProvider();
//    }

    @SubscribeEvent
    private static void onRegistryShaders(final RegisterShadersEvent event) {
        event.registerShader(NcShaders.SCREENPARTICLE);
    }

    @SubscribeEvent
    private static void onRegistryPackets(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registerClientChannel(registrar, StagerPacket.TYPE, StagerPacket.CODEC);
    }

    @SubscribeEvent
    private static void addCreativeContents(final BuildCreativeModeTabContentsEvent event) {
        for (Item item : BuiltInRegistries.ITEM) {
            NcUtils.Item.instanceOf(item, CreativeContent.class, cc -> {
                if (event.getTabKey() == cc.getCreativeTab()) cc.addContents(event);
            });
        }
    }

    public static void onRegistryResource(final AddServerReloadListenersEvent event) {
        event.addListener(ResourceLocation.fromNamespaceAndPath(ID, "biome_rules"), new BiomeRulesManager());
        event.addListener(ResourceLocation.fromNamespaceAndPath(ID, "tracks"), new TracksManager());
        event.addListener(ResourceLocation.fromNamespaceAndPath(ID, "lockable"), new LockableManager());
    }
}
