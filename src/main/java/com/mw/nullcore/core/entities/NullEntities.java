package com.mw.nullcore.core.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.mw.nullcore.NullCore.ID;

public final class NullEntities {
    public static final DeferredRegister.Entities entities = DeferredRegister.createEntities(ID);

    public static final DeferredHolder<EntityType<?>, EntityType<AdvancedItemEntity>> advancedItem = entities.registerEntityType("advanced_item", AdvancedItemEntity::new, MobCategory.MISC, builder -> builder.noLootTable().sized(0.25F, 0.25F).eyeHeight(0.2125F).clientTrackingRange(6).updateInterval(20));
}
