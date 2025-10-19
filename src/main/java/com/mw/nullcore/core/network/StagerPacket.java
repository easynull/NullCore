package com.mw.nullcore.core.network;

import com.mw.nullcore.NullCore;
import com.mw.nullcore.client.audio.TrackerTicker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record StagerPacket(ResourceLocation stage) implements ClientChannel<StagerPacket> {
    public static final Type<StagerPacket> TYPE = new Type<>(NullCore.path("stager"));
    public static final StreamCodec<FriendlyByteBuf, StagerPacket> CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, StagerPacket::stage, StagerPacket::new);

    @Override
    public @NotNull Type<StagerPacket> type() {
        return TYPE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleClient(StagerPacket packet, IPayloadContext ctx) {
        TrackerTicker.currentStage = stage;
    }
}
