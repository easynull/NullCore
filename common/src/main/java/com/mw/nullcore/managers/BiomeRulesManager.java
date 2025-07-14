package com.mw.nullcore.managers;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.mw.nullcore.NullCore.LOG;

public final class BiomeRulesManager extends SimplePreparableReloadListener<Map<ResourceLocation, BiomeRulesManager.BiomeRules>> {
    private static final Map<String, RuleType<?>> ruleTypes = new HashMap<>();
    private static final Map<ResourceLocation, BiomeRules> biomeRules = new HashMap<>();

    static {
        registerRuleType("fixed_time", JsonElement::getAsLong, Long.class);
        registerRuleType("rain_force", JsonElement::getAsFloat, Float.class);
        registerRuleType("has_precipitation", JsonElement::getAsBoolean, Boolean.class);
    }

    @Override
    public Map<ResourceLocation, BiomeRules> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, BiomeRules> rules = new HashMap<>();
        Map<ResourceLocation, Resource> resources = manager.listResources("level", id -> id.getPath().endsWith("biome_rules.json"));

        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            try (InputStream stream = entry.getValue().open()) {
                String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                JsonObject root = JsonParser.parseString(json).getAsJsonObject();

                for (Map.Entry<String, JsonElement> biomeEntry : root.entrySet()) {
                    ResourceLocation biomeId = ResourceLocation.tryParse(biomeEntry.getKey());
                    BiomeRules biomeRule = parseBiomeRules(biomeEntry.getValue().getAsJsonObject());
                    if (!biomeRule.isEmpty()) {
                        rules.put(biomeId, biomeRule);
                    }
                }
            } catch (Exception e) {
                LOG.error("Failed to load biome rules from {}: {}", entry.getKey(), e.getMessage());
            }
        }

        return rules;
    }

    @Override
    public void apply(Map<ResourceLocation, BiomeRules> rules, ResourceManager manager, ProfilerFiller profiler) {
        biomeRules.clear();
        biomeRules.putAll(rules);
    }

    public static BiomeRules parseBiomeRules(JsonObject biomeJson) {
        BiomeRules rules = new BiomeRules();
        for (Map.Entry<String, JsonElement> ruleEntry : biomeJson.entrySet()) {
            RuleType<?> type = ruleTypes.get(ruleEntry.getKey());
            if (type == null) {
                LOG.warn("Unknown rule type: {}", ruleEntry.getKey());
                continue;
            }
            try {
                rules.addRule(ruleEntry.getKey(), type.parse(ruleEntry.getValue()));
            } catch (Exception e) {
                LOG.warn("Invalid rule format for {}: {}", ruleEntry.getKey(), e.getMessage());
            }
        }
        return rules;
    }

    public static Optional<BiomeRules> getRules(ResourceLocation biomeId) {
        return Optional.ofNullable(biomeRules.get(biomeId));
    }

    public static <T> void registerRuleType(String key, Function<JsonElement, T> parser, Class<T> type) {
        ruleTypes.put(key, new RuleType<>(parser, type));
    }

    public static class BiomeRules {
        private final Map<String, Object> rules = new HashMap<>();

        public <T> void addRule(String key, T value) {
            rules.put(key, value);
        }

        public <T> Optional<T> getRule(String key, Class<T> type) {
            Object value = rules.get(key);
            return type.isInstance(value) ? Optional.of((T) value) : Optional.empty();
        }

        public boolean isEmpty() {
            return rules.isEmpty();
        }
    }

    private record RuleType<T>(Function<JsonElement, T> parser, Class<T> type) {
        public T parse(JsonElement json) {
            return parser.apply(json);
        }
    }
}
