package com.mw.nullcore.managers;

import com.mw.nullcore.NullCore;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.Profiler;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class BiomeRulesFabric implements SimpleResourceReloadListener<Map<ResourceLocation, BiomeRulesManager.BiomeRules>> {
    private final BiomeRulesManager manager = new BiomeRulesManager();

    @Override
    public ResourceLocation getFabricId() {
        return NullCore.defaultLoc("biome_rules");
    }

    @Override
    public CompletableFuture<Map<ResourceLocation, BiomeRulesManager.BiomeRules>> load(ResourceManager manager, Executor executor) {
        return CompletableFuture.supplyAsync(()-> this.manager.prepare(manager, Profiler.get()));
    }

    @Override
    public CompletableFuture<Void> apply(Map<ResourceLocation, BiomeRulesManager.BiomeRules> allRules, ResourceManager manager, Executor executor) {
        return CompletableFuture.runAsync(()-> this.manager.apply(allRules, manager, Profiler.get()));
    }
}
