package com.mw.nullcore.utils;

import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Objects;

public final class WorldUtils {

    public static RegistryAccess getRegistryAccess() {
        return Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer()).registryAccess();
    }

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

    public static List<ResourceLocation> getStructuresAt(ServerLevel level, BlockPos pos){
        Registry<Structure> structureRegistry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
        return level.structureManager().getAllStructuresAt(pos).keySet().stream().map(structureRegistry::getKey).filter(Objects::nonNull).toList();
    }

    public static boolean isPosInStructure(ServerLevel level, BlockPos pos, ResourceLocation structureId) {
        Registry<Structure> registry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
        Structure structure = registry.getValue(structureId);
        if (structure == null) return false;
        return level.structureManager().getStructureAt(pos, structure).isValid();
    }

    public static List<Entity> getEntities(Level level, BlockPos startPos, double radius) {
        return level.getEntities(null, new AABB(startPos.getX() - radius, startPos.getY() - radius, startPos.getZ() - radius, startPos.getX() + radius, startPos.getY() + radius, startPos.getZ() + radius));
    }
}
