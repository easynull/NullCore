package com.mw.nullcore.core.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mw.nullcore.NullCore;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.mw.nullcore.Utils.Json.addValueToJson;
import static com.mw.nullcore.Utils.Json.parseJsonValue;

public final class ConfigManager {
    private static Initialize INSTANCE;
    public static Map<String, Set<Initialize.Unit<?>>> variables = new HashMap<>();

    public static void register(String modid, Path configDir, Runnable registers) {
        Path configPath = configDir.resolve(modid + ".json");
        INSTANCE = new Initialize(configPath, modid);
        registers.run();
        INSTANCE.load();
    }

    public static <V> Initialize.Unit<V> create(String key, String comment, V defaultValue) {
        Initialize.Unit<V> unit = Initialize.Unit.of(key, comment, defaultValue);
        INSTANCE.config.put(key, unit);
        variables.put(INSTANCE.modid, Set.of(unit));
        return unit;
    }

    public static <V> Initialize.Unit<V> getUnit(String key) {
        return (Initialize.Unit<V>) INSTANCE.config.get(key);
    }

    public static <V> void set(String key, V value) {
        Initialize.Unit<V> unit = getUnit(key);
        if (unit != null) {
            INSTANCE.config.put(key, Initialize.Unit.of(key, unit.comment(), value));
            unit.set(value);
        }
    }

    public static final class Initialize {
        public final Map<String, Unit<?>> config = new LinkedHashMap<>();
        private final Path configPath;
        public final String modid;

        public Initialize(Path configPath, String modid) {
            this.configPath = configPath;
            this.modid = modid;
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
            final String comment, name;
            V value;

            private Unit(String name, String comment, V value) {
                this.value = value;
                this.name = name;
                this.comment = comment;
            }

            public static <V> Unit<V> of(String name, String comment, V value) {
                return new Unit<>(name, comment, value);
            }

            public V get(){
                return value;
            }

            public void set(V value){
                this.value = value;
            }

            public void setParse(String value){
                this.value = parseValue(value);
            }

            public String comment(){
                return comment;
            }

            public String name(){
                return name;
            }

            @SuppressWarnings("unchecked")
            private V parseValue(String value) {
                if (get() instanceof Boolean) {
                    return (V) Boolean.valueOf(value);
                } else if (get() instanceof Byte) {
                    return (V) Byte.valueOf(value);
                } else if (get() instanceof Integer) {
                    return (V) Integer.valueOf(value);
                } else if (get() instanceof Float) {
                    return (V) Float.valueOf(value);
                } else if (get() instanceof Double) {
                    return (V) Double.valueOf(value);
                } else if (get() instanceof String) {
                    return (V) value;
                }
                return null;
            }
        }
    }
}
