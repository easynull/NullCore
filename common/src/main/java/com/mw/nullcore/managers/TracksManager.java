package com.mw.nullcore.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mw.nullcore.client.audio.Track;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.mw.nullcore.NullCore.LOG;

public final class TracksManager extends SimplePreparableReloadListener<Map<ResourceLocation, HashSet<Track>>> {
    public static final Map<ResourceLocation, HashSet<Track>> selfTracks = new HashMap<>();

    @Override
    public Map<ResourceLocation, HashSet<Track>> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, HashSet<Track>> allTracks = new HashMap<>();
        Map<ResourceLocation, Resource> resources = manager.listResources("level", id -> id.getPath().endsWith("tracks.json"));
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            try (InputStream stream = entry.getValue().open()) {
                String jsonContent = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                Map<ResourceLocation, HashSet<Track>> parsedTracks = parseTracksJson(jsonContent);

                parsedTracks.forEach((id, tracks) -> allTracks.merge(id, tracks, (oldTracks, newTracks) -> {
                    HashSet<Track> merged = new HashSet<>(oldTracks);
                    merged.addAll(newTracks);
                    return merged;
                }));
            } catch (Exception e) {
                LOG.error("Failed to load tracks from {}: {}", entry.getKey(), e.getMessage());
            }
        }
        return allTracks;
    }

    @Override
    public void apply(@NotNull Map<ResourceLocation, HashSet<Track>> allTracks, ResourceManager manager, ProfilerFiller profiler) {
        selfTracks.clear();
        selfTracks.putAll(allTracks);
    }

    public static Map<ResourceLocation, HashSet<Track>> parseTracksJson(String json) {
        Map<ResourceLocation, HashSet<Track>> tracks = new HashMap<>();
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (root.has("dimensions")) {
                JsonObject dimensions = root.getAsJsonObject("dimensions");
                for (Map.Entry<String, JsonElement> entry : dimensions.entrySet()) {
                    ResourceLocation id = ResourceLocation.tryParse(entry.getKey());
                    if (id == null) {
                        LOG.warn("Invalid dim ID: {}", entry.getKey());
                        continue;
                    }
                    HashSet<Track> selfTracks = parseTrackList(entry.getValue().getAsJsonArray());
                    tracks.put(id, selfTracks);
                }
            }
            if (root.has("structures")) {
                JsonObject structures = root.getAsJsonObject("structures");
                for (Map.Entry<String, JsonElement> entry : structures.entrySet()) {
                    ResourceLocation id = ResourceLocation.tryParse(entry.getKey());
                    if (id == null) {
                        LOG.warn("Invalid structure ID: {}", entry.getKey());
                        continue;
                    }
                    HashSet<Track> selfTracks = parseTrackList(entry.getValue().getAsJsonArray());
                    tracks.put(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath()), selfTracks);
                }
            }
            if (root.has("mobs")) {
                JsonObject structures = root.getAsJsonObject("mobs");
                for (Map.Entry<String, JsonElement> entry : structures.entrySet()) {
                    ResourceLocation id = ResourceLocation.tryParse(entry.getKey());
                    if (id == null) {
                        LOG.warn("Invalid entity ID: {}", entry.getKey());
                        continue;
                    }
                    HashSet<Track> selfTracks = parseTrackList(entry.getValue().getAsJsonArray());
                    tracks.put(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath()), selfTracks);
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to parse tracks JSON: {}", e.getMessage());
        }
        return tracks;
    }

    private static HashSet<Track> parseTrackList(JsonArray array) {
        HashSet<Track> tracks = new HashSet<>();
        for (JsonElement element : array) {
            try {
                DataResult<Track> result = Track.codec.parse(JsonOps.INSTANCE, element);
                if (result.result().isPresent()) {
                    tracks.add(result.result().get());
                }
            } catch (Exception e) {
                LOG.warn("Failed to parse track element: {}", element, e);
            }
        }
        return tracks;
    }
}
