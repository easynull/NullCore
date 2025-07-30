package com.mw.nullcore.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class NetworkHandler {
    public static <T extends DualChannel<T>> void registerDualChannel(PayloadRegistrar registrar, CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec) {
        registrar.playBidirectional(type, codec, (packet, context) -> {
                    if (context.flow().isClientbound()) packet.handleClient(packet, context);
                    else packet.handleServer(packet, context);
                }
        );
    }
    public static <T extends ClientChannel<T>> void registerClientChannel(PayloadRegistrar registrar, CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec) {
        registrar.playToClient(type, codec, (packet, context) -> packet.handleClient(packet, context));
    }
    public static <T extends ServerChannel<T>> void registerServerChannel(PayloadRegistrar registrar, CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec) {
        registrar.playToServer(type, codec, (packet, context) -> packet.handleServer(packet, context));
    }
}
