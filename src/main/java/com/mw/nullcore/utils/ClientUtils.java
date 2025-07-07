package com.mw.nullcore.utils;

import com.mw.nullcore.client.audio.TrackerController;
import com.mw.nullcore.client.audio.TrackerTicker;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class ClientUtils {
    private final static Minecraft mc = Minecraft.getInstance();
    public static int clientTick;

    public static void tickClient(ClientTickEvent.Post event) {
        if (!mc.isPaused()){
            clientTick++;
        }
    }

    public static void sendPacketDispatch(BlockEntity be) {
        if (be.getLevel() instanceof ServerLevel) {
            Packet<?> packet = be.getUpdatePacket();
            if (packet != null) {
                BlockPos pos = be.getBlockPos();
                ((ServerChunkCache) be.getLevel().getChunkSource()).chunkMap.getPlayers(new ChunkPos(pos), false).forEach(e -> e.connection.send(packet));
            }
        }
    }
}
