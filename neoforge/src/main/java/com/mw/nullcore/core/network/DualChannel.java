package com.mw.nullcore.core.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface DualChannel<P extends CustomPacketPayload> extends ClientChannel<P>, ServerChannel<P> {}
