package com.mw.nullcore.client.particle.screen;

import com.mw.nullcore.core.items.GuiRenderable;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ParticleEmitterHandler {
    public static final Map<Item, List<GuiRenderable>> EMITTERS = new HashMap<>();

    public static void registerEmitters(Item item, GuiRenderable emitter) {
        if (EMITTERS.containsKey(item)) {
            EMITTERS.get(item).add(emitter);
        } else {
            EMITTERS.put(item, new ArrayList<>(List.of(emitter)));
        }
    }

    public static void registerEmitters(GuiRenderable emitter, Item... items) {
        for (Item item : items) {
            registerEmitters(item, emitter);
        }
    }
}
