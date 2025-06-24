package com.mw.nullcore.core.holders;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public final class NullRegisters {
    public static class AdItems extends DeferredRegister<Item> {
        final String id;
        public AdItems(String namespace) {
            super(Registries.ITEM, namespace);
            this.id = namespace;
        }

        public <I extends Item> DeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> item) {
            return register(name, () -> item.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(id, name)))));
        }

        @Override
        public <I extends Item> DeferredItem<I> register(String name, Supplier<? extends I> sup) {
            return (DeferredItem<I>) this.register(name, key -> sup.get());
        }
        @Override
        protected <I extends Item> DeferredItem<I> createHolder(ResourceKey<? extends Registry<Item>> registryKey, ResourceLocation key) {
            return DeferredItem.createItem(ResourceKey.create(registryKey, key));
        }
    }

    public static class AdBlocks extends DeferredRegister<Block> {
        final String id;
        final AdItems register;

        public AdBlocks(String namespace, AdItems register) {
            super(Registries.BLOCK, namespace);
            this.id = namespace;
            this.register = register;
        }

        public AdBlocks(String namespace) {
            this(namespace, null);
        }

        public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> block, Block copy) {
            DeferredBlock<B> reg = register(name, () -> block.apply((copy != null ? BlockBehaviour.Properties.ofFullCopy(copy) : BlockBehaviour.Properties.of()).setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(id, name)))));
            if (register != null) register.registerItem(name, prop -> new BlockItem(reg.get(), prop));
            return reg;
        }

        public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> block) {
            return registerBlock(name, block, null);
        }

        @Override
        public <B extends Block> DeferredBlock<B> register(String name, Supplier<? extends B> sup) {
            return (DeferredBlock<B>) this.register(name, key -> sup.get());
        }
        @Override
        protected <B extends Block> DeferredBlock<B> createHolder(ResourceKey<? extends Registry<Block>> registryKey, ResourceLocation key) {
            return DeferredBlock.createBlock(ResourceKey.create(registryKey, key));
        }
    }
}
