package com.mw.nullcore.core.managers;

import com.mw.nullcore.NullCore;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.Profiler;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class LockableFabric implements SimpleResourceReloadListener<Map<ResourceLocation, LockableManager.LockableEntry>> {
    private final LockableManager manager = new LockableManager();

    @Override
    public ResourceLocation getFabricId() {
        return NullCore.path("lockable");
    }

    @Override
    public CompletableFuture<Map<ResourceLocation, LockableManager.LockableEntry>> load(ResourceManager manager, Executor executor) {
        return CompletableFuture.supplyAsync(()-> this.manager.prepare(manager, Profiler.get()));
    }

    @Override
    public CompletableFuture<Void> apply(Map<ResourceLocation, LockableManager.LockableEntry> allLockable, ResourceManager manager, Executor executor) {
        return CompletableFuture.runAsync(()-> this.manager.apply(allLockable, manager, Profiler.get()));
    }
}