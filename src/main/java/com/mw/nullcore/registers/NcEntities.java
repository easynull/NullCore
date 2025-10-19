package com.mw.nullcore.registers;

import com.mw.nullcore.core.entities.ShyBlockEntity;
import com.mw.nullcore.core.entities.ShyItemEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.mw.nullcore.NullCore.ID;

public final class NcEntities {
    public static final DeferredRegister.Entities entities = DeferredRegister.createEntities(ID);

    public static final Supplier<EntityType<ShyItemEntity>> SHYITEM = entities.registerEntityType("shy_item", ShyItemEntity::new, MobCategory.MISC, builder -> builder.noLootTable().sized(0.25F, 0.25F).eyeHeight(0.2125F).clientTrackingRange(6).updateInterval(20));
    public static final Supplier<EntityType<ShyBlockEntity>> SHYBLOCK = entities.registerEntityType("shy_block", ShyBlockEntity::new, MobCategory.MISC, builder -> builder.noLootTable().sized(0.98F, 0.98F).eyeHeight(1F).clientTrackingRange(10).updateInterval(20));
}
