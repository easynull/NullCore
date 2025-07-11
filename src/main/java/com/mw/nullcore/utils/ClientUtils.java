package com.mw.nullcore.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.jetbrains.annotations.NotNull;

public final class ClientUtils {
    private final static @NotNull Minecraft mc = Minecraft.getInstance();
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

    public static void setSafeScreen(Screen screen){
        if(mc.level == null) return;
        if(mc.player == null) return;
        if(!mc.level.isClientSide()) return;
        mc.setScreen(screen);
    }
}
