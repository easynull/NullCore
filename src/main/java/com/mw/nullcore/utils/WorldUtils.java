package com.mw.nullcore.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public final class WorldUtils {
    public static void setBiome(ServerLevel level, ResourceKey<Biome> biome, BlockPos from, BlockPos to) {
        FillBiomeCommand.fill(level, from, to, getBiome(level, biome));
    }

    public static void setBiome(ServerLevel level, ResourceKey<Biome> biome, BlockPos pos) {
        setBiome(level, biome, pos, pos);
    }

    public static Holder<Biome> getBiome(Level level, ResourceKey<Biome> biome) {
        return level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(biome);
    }

    public static ResourceLocation getBiome(Level level, Biome biome) {
        return level.registryAccess().lookupOrThrow(Registries.BIOME).getKey(biome);
    }
}
