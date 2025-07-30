package com.mw.nullcore.core.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface ClientChannel<P extends CustomPacketPayload> extends CustomPacketPayload {
    void handleClient(P packet, IPayloadContext ctx);
}
