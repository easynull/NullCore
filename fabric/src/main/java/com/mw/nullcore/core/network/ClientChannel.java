package com.mw.nullcore.core.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface ClientChannel<P extends CustomPacketPayload> extends CustomPacketPayload {
    void handleClient(P packet, ClientPlayNetworking.Context ctx);
}
