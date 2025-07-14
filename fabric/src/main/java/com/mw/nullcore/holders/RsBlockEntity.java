package com.mw.nullcore.holders;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashMap;
import java.util.Map;

public final class RsBlockEntity {
    private final String modid;
    private final Map<ResourceLocation, BlockEntityType<?>> types = new HashMap<>();

    private RsBlockEntity(String modid) {
        this.modid = modid;
    }

    public static RsBlockEntity create(String modid) {
        return new RsBlockEntity(modid);
    }

    public <T extends BlockEntity> BlockEntityType<T> registerType(String id, FabricBlockEntityTypeBuilder.Factory<? extends T> factory, Block... blocks){
        BlockEntityType reg = FabricBlockEntityTypeBuilder.create(factory, blocks).build();
        types.put(ResourceLocation.fromNamespaceAndPath(modid, id), reg);
        return reg;
    }

    public void registerAll() {
        types.forEach((id, supplier) -> Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, supplier));
    }
}
