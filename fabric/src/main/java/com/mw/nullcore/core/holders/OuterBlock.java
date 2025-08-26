package com.mw.nullcore.core.holders;

import com.mw.nullcore.NullCore;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class OuterBlock {
    private final String modid;
    private final Map<ResourceLocation, Block> blocks = new HashMap<>();
    private final OuterItem items;

    private OuterBlock(String modid, OuterItem items) {
        this.modid = modid;
        this.items = items;
    }

    public static OuterBlock create(String modid, OuterItem items) {
        return new OuterBlock(modid, items);
    }

    public static OuterBlock create(String modid) {
        return create(modid, null);
    }

    public <B extends Block> B registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> block, Block copy, Item.Properties itemProp) {
        B reg = block.apply((copy != null ? BlockBehaviour.Properties.ofFullCopy(copy) : BlockBehaviour.Properties.of()).setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(modid, name))));
        blocks.put(ResourceLocation.fromNamespaceAndPath(modid, name), reg);
        if (items != null) items.register(name, ()-> new BlockItem(reg, itemProp.setId(NullCore.key(Registries.ITEM, modid, name))));
        return reg;
    }

    public <B extends Block> B registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> block, Block copy) {
        return registerBlock(name, block, copy, new Item.Properties());
    }

    public <B extends Block> B registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> block, Item.Properties itemProp) {
        return registerBlock(name, block, null, itemProp);
    }

    public <B extends Block> B registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> block) {
        return registerBlock(name, block, null, new Item.Properties());
    }

    public void registerAll() {
        blocks.forEach((id, supplier) -> Registry.register(BuiltInRegistries.BLOCK, id, supplier));
    }
}
