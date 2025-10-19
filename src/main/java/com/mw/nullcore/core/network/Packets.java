package com.mw.nullcore.core.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.mw.nullcore.NullCore.ID;
import static com.mw.nullcore.core.network.NetworkHandler.registerClientChannel;

@EventBusSubscriber(modid = ID, bus = EventBusSubscriber.Bus.MOD)
public final class Packets {
    @SubscribeEvent
    public static void init(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registerClientChannel(registrar, StagerPacket.TYPE, StagerPacket.CODEC);
    }
}
