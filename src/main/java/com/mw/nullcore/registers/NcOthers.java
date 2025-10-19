package com.mw.nullcore.registers;

import com.mw.nullcore.core.managers.BiomeRulesManager;
import com.mw.nullcore.core.managers.LockableManager;
import com.mw.nullcore.core.managers.TracksManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

import static com.mw.nullcore.NullCore.ID;

public final class NcOthers {
    public static void onResourceReload(AddServerReloadListenersEvent event) {
        event.addListener(ResourceLocation.fromNamespaceAndPath(ID, "biome_rules"), new BiomeRulesManager());
        event.addListener(ResourceLocation.fromNamespaceAndPath(ID, "tracks"), new TracksManager());
        event.addListener(ResourceLocation.fromNamespaceAndPath(ID, "lockable"), new LockableManager());
    }
}
