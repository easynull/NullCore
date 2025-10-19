package com.mw.nullcore.core.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface ServerChannel<P extends CustomPacketPayload> extends CustomPacketPayload {
    void handleServer(P packet, IPayloadContext ctx);
}
