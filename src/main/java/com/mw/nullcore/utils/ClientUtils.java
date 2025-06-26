package com.mw.nullcore.utils;

import com.mw.nullcore.core.blocks.type.InventoryBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class ClientUtils {
    private final static Minecraft mc = Minecraft.getInstance();
    public static int clientTick;

    public static void onTicker(ClientTickEvent.Post event) {
        if (!mc.isPaused()) clientTick++;
    }

    public static void forParticleSpawn(Level level, ParticleOptions particle, float pX, float pY, float pZ, float xSpeed, float ySpeed, float zSpeed, int count) {
        RandomSource rand = level.random;
        for (int i = 0; i < count; i++) {
            double oX = (rand.nextDouble() - 0.5) * 0.5;
            double oY = (rand.nextDouble() - 0.5) * 0.5;
            double oZ = (rand.nextDouble() - 0.5) * 0.5;
            level.addParticle(particle, pX + oX, pY + oY, pZ + oZ, xSpeed, ySpeed, zSpeed);
        }
    }

    public static void forParticleSpawn(Level level, ParticleOptions particle, float pX, float pY, float pZ, int count) {
        forParticleSpawn(level, particle, pX, pY, pZ, 0, 0, 0, count);
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

    public static void inFromInventory(InventoryBlockEntity inv, Player player) {
        for (int i = inv.inventory.getContainerSize() - 1; i >= 0; i--) {
            ItemStack stack = inv.inventory.getItem(i);
            if (!stack.isEmpty()) {
                ItemStack copy = stack.copy();
                player.getInventory().placeItemBackInInventory(copy);
                inv.inventory.setItem(i, ItemStack.EMPTY);
                inv.getLevel().gameEvent(null, GameEvent.BLOCK_CHANGE, inv.getBlockPos());
                break;
            }
        }
    }
}
