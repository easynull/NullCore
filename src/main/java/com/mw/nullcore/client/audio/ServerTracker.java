package com.mw.nullcore.client.audio;

import com.mw.nullcore.core.NcConfig;
import com.mw.nullcore.core.NcUtils;
import com.mw.nullcore.core.managers.TracksManager;
import com.mw.nullcore.core.network.StagerPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ServerTracker {
    public static void tick(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new StagerPacket(getStage(player.serverLevel(), player.blockPosition())));
    }

    private static ResourceLocation getStage(Level level, BlockPos pos) {
        for (Entity entity : NcUtils.Level.getEntities(level, pos, NcConfig.radiusEntityTrack.get())) {
            ResourceLocation entityId = EntityType.getKey(entity.getType());
            if (TracksManager.selfTracks.containsKey(entityId)) {
                return entityId;
            }
        }
        for (ResourceLocation structureId : TracksManager.selfTracks.keySet()) {
            if (level instanceof ServerLevel sl && NcUtils.Level.isStructure(sl, pos, structureId)) {
                return structureId;
            }
        }
        return level.dimension().location();
    }
}
