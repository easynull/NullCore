package com.mw.nullcore.core.holders;

import com.mw.nullcore.NullCore;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public final class OuterBlock extends DeferredRegister<Block> {
    final String id;
    final OuterItem register;
    private OuterBlock(String namespace, OuterItem register) {
        super(Registries.BLOCK, namespace);
        this.id = namespace;
        this.register = register;
    }

    public static OuterBlock create(String modId, OuterItem withItems){
        return new OuterBlock(modId, withItems);
    }

    public static OuterBlock create(String modId){
        return new OuterBlock(modId, null);
    }

    public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> prop, Block copy, Item.Properties itemProp) {
        DeferredBlock<B> reg = register(name, () -> prop.apply((copy != null ? BlockBehaviour.Properties.ofFullCopy(copy) : BlockBehaviour.Properties.of()).setId(NullCore.key(Registries.BLOCK, id, name))));
        if (register != null) register.register(name, ()-> new BlockItem(reg.get(), itemProp.setId(NullCore.key(Registries.ITEM, id, name))));
        return reg;
    }

    public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> block, Item.Properties itemProp) {
        return registerBlock(name, block, null, itemProp);
    }

    public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> block, Block copy) {
        return registerBlock(name, block, copy, new Item.Properties());
    }

    public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> block) {
        return registerBlock(name, block, null, new Item.Properties());
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