package com.mw.nullcore.core.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface ServerChannel<P extends CustomPacketPayload> extends CustomPacketPayload {
    void handleServer(P packet, ServerPlayNetworking.Context ctx);
}
