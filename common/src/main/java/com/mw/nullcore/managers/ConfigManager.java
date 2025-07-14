package com.mw.nullcore.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mw.nullcore.NullCore;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.mw.nullcore.Utils.Json.addValueToJson;
import static com.mw.nullcore.Utils.Json.parseJsonValue;

public class ConfigManager {
    private static Initialize INSTANCE;
    public static String modid = "nullcore";

    public static void initialize(Path configDir, Runnable registers) {
        Path configPath = configDir.resolve(modid + ".json");
        INSTANCE = new Initialize(configPath);
        registers.run();
        INSTANCE.load();
    }

    public static <V> Initialize.Unit<V> create(String key, String comment, V defaultValue) {
        Initialize.Unit<V> unit = Initialize.Unit.of(comment, defaultValue);
        INSTANCE.config.put(key, unit);
        return unit;
    }

    public static void load() {
        INSTANCE.load();
    }

    public static void save() {
        INSTANCE.save();
    }

    public static <V> Initialize.Unit<V> getUnit(String key) {
        return (Initialize.Unit<V>) INSTANCE.config.get(key);
    }

    public static <V> void set(String key, V value) {
        Initialize.Unit<V> unit = getUnit(key);
        if (unit != null) {
            INSTANCE.config.put(key, Initialize.Unit.of(unit.comment(), value));
            unit.set(value);
        }
    }

    public static final class Initialize {
        public final Map<String, Unit<?>> config = new LinkedHashMap<>();
        private final Path configPath;

        public Initialize(Path configPath) {
            this.configPath = configPath;
        }

        void load() {
            if (!Files.exists(configPath)) {
                save();
                return;
            }
            try {
                String content = Files.readString(configPath);
                JsonObject json = JsonParser.parseString(content).getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    String key = entry.getKey();
                    if (key.startsWith("_")) continue;

                    JsonElement element = entry.getValue();
                    if (config.containsKey(key)) {
                        updateConfig(key, element);
                    }
                }
            } catch (Exception e) {
                NullCore.LOG.error("Failed to load config: {}", e.getMessage());
            }
        }

        private void updateConfig(String key, JsonElement element) {
            Unit<?> unit = config.get(key);
            Object newValue = parseJsonValue(element, unit.get());

            if (newValue != null) {
                set(key, newValue);
            }
        }

        void save() {
            JsonObject json = new JsonObject();
            config.forEach((key, unit) -> {
                json.addProperty("_" + key + "_comment", unit.comment());
                addValueToJson(json, key, unit.get());
            });
            try {
                Files.writeString(configPath, new GsonBuilder().setPrettyPrinting().create().toJson(json));
            } catch (Exception e) {
                NullCore.LOG.error("Failed to save config: {}", e.getMessage());
            }
        }

        public static class Unit<V> {
            final String comment;
            V value;

            private Unit(String comment, V value) {
                this.value = value;
                this.comment = comment;
            }

            public static <V> Unit<V> of(String comment, V value) {
                return new Unit<>(comment, value);
            }

            public V get(){
                return value;
            }

            public void set(V value){
                this.value = value;
            }

            public String comment(){
                return comment;
            }
        }
    }
}
