package com.mw.nullcore.data;

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

import static com.mw.nullcore.NullCore.LOGGER;

public final class TracksManager extends SimplePreparableReloadListener<Map<ResourceLocation, HashSet<Track>>> {
    public static final Map<ResourceLocation, HashSet<Track>> selfTracks = new HashMap<>(); //TODO: Perhaps I will split the map into structures and dimensions :)

    @Override
    protected Map<ResourceLocation, HashSet<Track>> prepare(ResourceManager manager, ProfilerFiller profiler) {
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
                LOGGER.error("Failed to load tracks from {}: {}", entry.getKey(), e.getMessage());
            }
        }
        return allTracks;
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, HashSet<Track>> allTracks, ResourceManager manager, ProfilerFiller profiler) {
        selfTracks.clear();
        selfTracks.putAll(allTracks);
    }

    private static Map<ResourceLocation, HashSet<Track>> parseTracksJson(String json) {
        Map<ResourceLocation, HashSet<Track>> tracks = new HashMap<>();
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (root.has("worlds")) {
                JsonObject worlds = root.getAsJsonObject("worlds");
                for (Map.Entry<String, JsonElement> entry : worlds.entrySet()) {
                    ResourceLocation worldId = ResourceLocation.tryParse(entry.getKey());
                    if (worldId == null) {
                        LOGGER.warn("Invalid world ID: {}", entry.getKey());
                        continue;
                    }
                    HashSet<Track> worldTracks = parseTrackList(entry.getValue().getAsJsonArray());
                    tracks.put(worldId, worldTracks);
                }
            }
            if (root.has("structures")) {
                JsonObject structures = root.getAsJsonObject("structures");
                for (Map.Entry<String, JsonElement> entry : structures.entrySet()) {
                    ResourceLocation structureId = ResourceLocation.tryParse(entry.getKey());
                    if (structureId == null) {
                        LOGGER.warn("Invalid structure ID: {}", entry.getKey());
                        continue;
                    }
                    HashSet<Track> structureTracks = parseTrackList(entry.getValue().getAsJsonArray());
                    tracks.put(ResourceLocation.fromNamespaceAndPath(structureId.getNamespace(), structureId.getPath()), structureTracks);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to parse tracks JSON: {}", e.getMessage());
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
                LOGGER.warn("Failed to parse track element: {}", element, e);
            }
        }
        return tracks;
    }
}
