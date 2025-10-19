package com.mw.nullcore.core.holders;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class OuterBlockEntity extends DeferredRegister<BlockEntityType<?>> {
    final String id;
    private OuterBlockEntity(String namespace) {
        super(Registries.BLOCK_ENTITY_TYPE, namespace);
        this.id = namespace;
    }

    public static OuterBlockEntity create(String modId){
        return new OuterBlockEntity(modId);
    }

    public <S extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<S>> registerType(String name, BlockEntityType.BlockEntitySupplier<S> supplier, Block... blocks) {
        return register(name, ()-> new BlockEntityType<>(supplier,  blocks));
    }

    @Override
    public <S extends BlockEntityType<?>> DeferredHolder<BlockEntityType<?>, S> register(String name, Supplier<? extends S> sup) {
        return this.register(name, key -> sup.get());
    }

    @Override
    protected <S extends BlockEntityType<?>> DeferredHolder<BlockEntityType<?>, S> createHolder(ResourceKey<? extends Registry<BlockEntityType<?>>> registryKey, ResourceLocation key) {
        return DeferredHolder.create(ResourceKey.create(registryKey, key));
    }
}
