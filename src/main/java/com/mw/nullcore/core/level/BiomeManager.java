package com.mw.nullcore.core.level;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;

public final class BiomeManager {
    private static final Map<ResourceLocation, Long> biomesTimes = new HashMap<>();
    private static final String pathData = "worldgen/biome_rules.json";

    public static Map<ResourceLocation, Long> loadRules(ResourceManager manager) {
        Map<ResourceLocation, Long> newRules = new HashMap<>();
        for (String namespace : manager.getNamespaces()) {
            if (namespace.equals("minecraft")) continue;
            ResourceLocation data = ResourceLocation.fromNamespaceAndPath(namespace, pathData);
            try {
                Optional<Resource> resourceOpt = manager.getResource(data);
                if (resourceOpt.isEmpty()) {
                    continue;
                }

                Resource resource = resourceOpt.get();
                try (InputStream stream = resource.open()) {
                    String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                    newRules.putAll(parseJson(json));
                }
            } catch (Exception ignored) {}
        }
        biomesTimes.clear();
        biomesTimes.putAll(newRules);
        return newRules;
    }

    private static Map<ResourceLocation, Long> parseJson(String json) throws JsonParseException {
        Map<ResourceLocation, Long> rules = new HashMap<>();
        JsonObject config = JsonParser.parseString(json).getAsJsonObject();
        JsonArray rulesArray = config.getAsJsonArray("rules");
        for (JsonElement element : rulesArray) {
            JsonObject rule = element.getAsJsonObject();
            ResourceLocation biome = ResourceLocation.tryParse(rule.get("biome").getAsString());
            if (biome == null) {
                continue;
            }
            long time = rule.get("fixed_time").getAsLong();
            rules.put(biome, time);
        }
        return rules;
    }

    public static void applyRules(Map<ResourceLocation, Long> newRules) {
        biomesTimes.clear();
        biomesTimes.putAll(newRules);
    }

    public static OptionalLong getBiomeTime(ResourceLocation id) {
        if (!biomesTimes.containsKey(id)) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(biomesTimes.get(id));
    }
}
