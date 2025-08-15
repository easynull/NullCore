package com.mw.nullcore;

import com.mw.nullcore.client.NullConfig;
import com.mw.nullcore.core.builders.CommandBuilder;
import com.mw.nullcore.core.items.CreativeContent;
import com.mw.nullcore.core.managers.BiomeRulesFabric;
import com.mw.nullcore.core.managers.TracksFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public final class FabricNullCore implements ModInitializer {
    
    @Override
    public void onInitialize() {
        NullCore.init();
        NullCore.registerEntityRenderers(EntityRendererRegistry::register);
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new BiomeRulesFabric());
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new TracksFabric());
        ClientTickEvents.END_CLIENT_TICK.register(mc -> Utils.Client.tickClient());
        NullConfig.initialize(FabricLoader.getInstance().getConfigDir());
        CommandRegistrationCallback.EVENT.register(((dispatcher, non2, non1) -> CommandBuilder.registers(dispatcher)));
        contentsCreativeTab();
    }

    private void contentsCreativeTab(){
        for(Item item : BuiltInRegistries.ITEM) {
            if (item instanceof CreativeContent cc) {
                ItemGroupEvents.modifyEntriesEvent(cc.getCreativeTab()).register(cc::addContents);
            } else if (item instanceof BlockItem bi && bi.getBlock() instanceof CreativeContent cc){
                ItemGroupEvents.modifyEntriesEvent(cc.getCreativeTab()).register(cc::addContents);
            }
        }
    }
}