package com.mw.nullcore.core.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mw.nullcore.NullCore;
import com.mw.nullcore.Utils;
import com.mw.nullcore.core.builders.CommandBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.mw.nullcore.NullCore.ID;
import static com.mw.nullcore.Utils.Reader.addValueToJson;
import static com.mw.nullcore.Utils.Reader.parseJsonValue;

public final class ConfigManager {
    private static Initialize INSTANCE;
    public static Map<String, Set<Initialize.Unit<?>>> variables = new HashMap<>();

    public static void register(String modid, Path configDir, Runnable registers, boolean createCommand) {
        Path configPath = configDir.resolve(modid + ".json");
        INSTANCE = new Initialize(configPath, modid);
        registers.run();
        INSTANCE.load();
        if (createCommand) {
            CommandBuilder.builder(modid)
                    .requires(0)
                    .then(Commands.literal("config")
                            .then(Commands.argument("unit", StringArgumentType.string())
                                    .suggests((ctx, builder) -> suggestCommandUnits(modid, builder))
                                    .then(Commands.argument("value", StringArgumentType.string())
                                            .executes(ctx -> setCommandValue(ctx.getSource(), StringArgumentType.getString(ctx, "unit"), StringArgumentType.getString(ctx, "value")))
                                    ))).register();
        }
    }

    public static void register(String modid, Path configDir, Runnable registers) {
        register(modid, configDir, registers, false);
    }

    public static <V> Initialize.Unit<V> create(String key, String comment, V defaultValue) {
        Initialize.Unit<V> unit = Initialize.Unit.of(key, comment, defaultValue);
        INSTANCE.config.put(key, unit);
        variables.computeIfAbsent(INSTANCE.modid, k -> new HashSet<>()).add(unit);
        return unit;
    }

    public static <V> Initialize.Unit<V> getUnit(String key) {
        return (Initialize.Unit<V>) INSTANCE.config.get(key);
    }

    public static <V> void set(Initialize.Unit<V> unit, V value) {
        if (unit != null) {
            INSTANCE.config.put(unit.name(), unit);
            unit.set(value);
            INSTANCE.save();
        }
    }

    public static <V> void set(String key, V value) {
        set(getUnit(key), value);
    }

    public static CompletableFuture<Suggestions> suggestCommandUnits(String modid, SuggestionsBuilder builder) {
        variables.get(modid).forEach(r -> builder.suggest(r.name()));
        return builder.buildFuture();
    }

    public static <V> int setCommandValue(CommandSourceStack source, String unit, String value) {
        Initialize.Unit<V> un = getUnit(unit);
        if (un == null || un.value == un.parseValue(value)) {
            source.sendFailure(Component.translatable("message.nullcore.commandset.fail"));
            return 0;
        }
        set(un, un.parseValue(value));
        source.sendSuccess(()-> Component.translatable("message.nullcore.commandset.success"), false);
        return 1;
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
            List<String> keys = new ArrayList<>(config.keySet());
            for (String key : keys) {
                Unit<?> unit = config.get(key);
                json.addProperty("comment_" + key, unit.comment());
                addValueToJson(json, key, unit.get());
            }
            try {
                String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(json);
                jsonString = jsonString.replaceAll("(\": [^,]+,\\n)(\\s+\"comment_)", "$1  \n$2");
                Files.writeString(configPath, jsonString);
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

            public String comment(){
                return comment;
            }

            public String name(){
                return name;
            }

            @SuppressWarnings("unchecked")
            public V parseValue(String value) {
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
                } else if (get() instanceof List<?>) {
                    return (V) Arrays.asList(value.split("\\s*,\\s*"));
                }
                return null;
            }
        }
    }
}
