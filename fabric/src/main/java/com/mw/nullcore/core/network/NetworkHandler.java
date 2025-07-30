package com.mw.nullcore.core.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class NetworkHandler {
    public static <T extends DualChannel<T>> void registerDualChannel(CustomPacketPayload.Type<T> type) {
        registerClientChannel(type);
        registerServerChannel(type);
    }
    public static <T extends ClientChannel<T>> void registerClientChannel(CustomPacketPayload.Type<T> type) {
        ClientPlayNetworking.registerGlobalReceiver(type, (packet, context) -> packet.handleClient(packet, context));
    }
    public static <T extends ServerChannel<T>> void registerServerChannel(CustomPacketPayload.Type<T> type) {
        ServerPlayNetworking.registerGlobalReceiver(type, (packet, context) -> packet.handleServer(packet, context));
    }
}
