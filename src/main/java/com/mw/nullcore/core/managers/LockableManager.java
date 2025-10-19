package com.mw.nullcore.core.managers;

import com.google.gson.*;
import com.mw.nullcore.NullCore;
import com.mw.nullcore.core.NcUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.mw.nullcore.core.NcUtils.Client.hasAdvancement;

public final class LockableManager extends SimplePreparableReloadListener<Map<ResourceLocation, LockableManager.LockableEntry>> {
    public static final Map<ResourceLocation, LockableEntry> selfLockable = new HashMap<>();

    @Override
    public Map<ResourceLocation, LockableEntry> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, LockableEntry> lockable = new HashMap<>();
        Map<ResourceLocation, Resource> resources = manager.listResources("level", id -> id.getPath().endsWith("lockable.json"));

        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            try (InputStream stream = entry.getValue().open()) {
                String jsonContent = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                Map<ResourceLocation, LockableEntry> parsedData = parseLockable(jsonContent);
                lockable.putAll(parsedData);
            } catch (Exception e) {
                NullCore.LOGGER.info("Failed to load lockable from {}: {}", entry.getKey(), e.getMessage());
            }
        }
        return lockable;
    }

    @Override
    public void apply(Map<ResourceLocation, LockableEntry> allLockable, ResourceManager manager, ProfilerFiller profiler) {
        selfLockable.clear();
        selfLockable.putAll(allLockable);
    }

    public static Map<ResourceLocation, LockableEntry> parseLockable(String json) {
        Map<ResourceLocation, LockableEntry> lockable = new HashMap<>();
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                ResourceLocation id = ResourceLocation.tryParse(entry.getKey());
                if (id == null) {
                    NullCore.LOGGER.warn("Invalid resource ID: {}", entry.getKey());
                    continue;
                }

                if (entry.getValue().isJsonObject()) {
                    JsonObject entryObj = entry.getValue().getAsJsonObject();
                    LockableEntry lockableEntry = parseLockableEntry(entryObj);
                    lockable.put(id, lockableEntry);
                }
            }

        } catch (JsonParseException e) {
            NullCore.LOGGER.info("Failed to parse lockable JSON: {}", e.getMessage());
        }
        return lockable;
    }

    private static LockableEntry parseLockableEntry(JsonObject json) {
        boolean lock = false;
        if(json.has("lock")){
            JsonObject lockObj = json.getAsJsonObject("lock");
            lock = lockObj.getAsBoolean();
        }
        RequiredCondition required = null;
        if (json.has("required")) {
            JsonObject requiredObj = json.getAsJsonObject("required");
            ResourceLocation advancement = requiredObj.has("advancement") ? ResourceLocation.tryParse(requiredObj.get("advancement").getAsString()) : null;
            required = new RequiredCondition(advancement);
        }
        Component message = null;
        SoundEvent soundMessage = null;
        if (json.has("message")) {
            JsonElement messageElem = json.get("message");
            JsonObject ms = messageElem.getAsJsonObject();
            if (ms.has("text")) {
                String text = ms.get("text").getAsString();
                message = Component.translatable(text);
                if (ms.has("color")) {
                    JsonElement color = ms.get("color");
                    message = message.copy().withColor(color.getAsJsonPrimitive().isNumber() ? color.getAsInt() : NcUtils.Color.hexPack(color.getAsString()));
                }
            }
            if (ms.has("sound")) {
                ResourceLocation soundId = ResourceLocation.tryParse(ms.get("sound").getAsString());
                if (soundId != null) {
                    soundMessage = BuiltInRegistries.SOUND_EVENT.getValue(soundId);
                }
            }
        }
        Set<Holder<MobEffect>> effects = null;
        if (json.has("effects")) {
            effects = new HashSet<>();
            JsonArray effectArray = json.getAsJsonArray("effects");
            for (JsonElement element : effectArray) {
                ResourceLocation effectId = ResourceLocation.tryParse(element.getAsString());
                if (effectId != null) {
                    Holder<MobEffect> effect = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(BuiltInRegistries.MOB_EFFECT.getValue(effectId));
                    effects.add(effect);
                }
            }
        }
        Set<Item> ignoreItems = null;
        if (json.has("ignore")) {
            ignoreItems = new HashSet<>();
            JsonArray ignoreArray = json.getAsJsonArray("ignore");
            for (JsonElement element : ignoreArray) {
                ResourceLocation itemId = ResourceLocation.tryParse(element.getAsString());
                if (itemId != null) {
                    Item item = BuiltInRegistries.ITEM.getValue(itemId);
                    ignoreItems.add(item);
                }
            }
        }
        return new LockableEntry(lock, required, message, soundMessage, effects, ignoreItems);
    }

    public static boolean canBlocked(ServerPlayer player, InteractionHand hand, BlockPos pos, ServerLevel level) {
        for (var id : LockableManager.selfLockable.keySet()) {
            if (NcUtils.Level.isStructure(level, pos, id) || level.getBiome(pos).is(id)) {
                LockableEntry entry = LockableManager.selfLockable.get(id);
                ItemStack itemInHand = player.getItemInHand(hand);
                if (!entry.lock()) {
                    RequiredCondition required = entry.required();
                    if (required == null) return false;
                    if (!hasAdvancement(player, required.advancement())) {
                        if (entry.ignoreItems() != null && !entry.ignoreItems().contains(itemInHand.getItem())) {
                            sendLockMessage(player, entry, level);
                            return true;
                        }
                    } else {
                        return false;
                    }
                } else {
                    if (entry.ignoreItems() != null && entry.ignoreItems().contains(itemInHand.getItem())) {
                        return false;
                    }
                    sendLockMessage(player, entry, level);
                    return true;
                }
            }
        }
        return false;
    }

    private static void sendLockMessage(Player player, LockableEntry entry, Level level) {
        NcUtils.Client.sendMessage(player, entry.message());
        if (entry.soundMessage() != null) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), entry.soundMessage(), SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }

    public record LockableEntry(boolean lock, RequiredCondition required, Component message, SoundEvent soundMessage, Set<Holder<MobEffect>> effects, Set<Item> ignoreItems) {}
    public record RequiredCondition(ResourceLocation advancement) {}
}
