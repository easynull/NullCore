package com.mw.nullcore.platform;

import java.util.function.Supplier;

import com.mw.nullcore.NullCore;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class FabricPlatform implements Platform {
    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <T> Supplier<T> register(Registry<? super T> registry, String name, Supplier<T> supplier) {
        T reg = Registry.register(registry, NullCore.path(name), supplier.get());
        return () -> reg;
    }

    @Override
    public void sendTo(Player player, CustomPacketPayload packet) {
        ServerPlayNetworking.send((ServerPlayer) player, packet);
    }
}
