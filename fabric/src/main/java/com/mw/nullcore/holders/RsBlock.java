package com.mw.nullcore.holders;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class RsBlock {
    private final String modid;
    private final Map<ResourceLocation, Block> blocks = new HashMap<>();
    private final RsItem items;

    private RsBlock(String modid, RsItem items) {
        this.modid = modid;
        this.items = items;
    }

    public static RsBlock create(String modid, RsItem items) {
        return new RsBlock(modid, items);
    }

    public static RsBlock create(String modid) {
        return create(modid, null);
    }

    public <B extends Block> B registerBlock(String id, Function<BlockBehaviour.Properties, ? extends B> block, Block copy) {
        B reg = block.apply((copy != null ? BlockBehaviour.Properties.ofFullCopy(copy) : BlockBehaviour.Properties.of()).setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(modid, id))));
        blocks.put(ResourceLocation.fromNamespaceAndPath(modid, id), reg);
        if (items != null) items.registerItem(id, prop -> new BlockItem(reg, prop));
        return reg;
    }

    public void registerAll() {
        blocks.forEach((id, supplier) -> Registry.register(BuiltInRegistries.BLOCK, id, supplier));
    }
}
