package com.mw.nullcore.core.managers;

import com.mw.nullcore.NullCore;
import com.mw.nullcore.client.audio.Track;
import com.mw.nullcore.managers.TracksManager;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.Profiler;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class TracksFabric implements SimpleResourceReloadListener<Map<ResourceLocation, HashSet<Track>>> {
    private final TracksManager manager = new TracksManager();

    @Override
    public ResourceLocation getFabricId() {
        return NullCore.defaultLoc("tracks");
    }

    @Override
    public CompletableFuture<Map<ResourceLocation, HashSet<Track>>> load(ResourceManager manager, Executor executor) {
        return CompletableFuture.supplyAsync(()-> this.manager.prepare(manager, Profiler.get()));
    }

    @Override
    public CompletableFuture<Void> apply(Map<ResourceLocation, HashSet<Track>> allTracks, ResourceManager manager, Executor executor) {
        return CompletableFuture.runAsync(()-> this.manager.apply(allTracks, manager, Profiler.get()));
    }
}