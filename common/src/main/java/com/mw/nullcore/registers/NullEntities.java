package com.mw.nullcore.registers;

import com.mw.nullcore.NullCore;
import com.mw.nullcore.core.entities.ShyItemEntity;
import com.mw.nullcore.platform.Platform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public final class NullEntities {
    public static final Supplier<EntityType<ShyItemEntity>> shyItem = register("shy_item", ShyItemEntity::new, MobCategory.MISC, 0.25F, 0.25F,0.2125F, 6, 20);

    private static <E extends Entity> Supplier<EntityType<E>> register(String id, EntityType.EntityFactory<E> factory, MobCategory category, float width, float height, float eyeHeight, int trackingRange, int updateInterval) {
        return Platform.PLATFORM.register(BuiltInRegistries.ENTITY_TYPE, id, ()-> EntityType.Builder.of(factory, category).sized(width, height).eyeHeight(eyeHeight).clientTrackingRange(trackingRange).updateInterval(updateInterval).build(NullCore.key(Registries.ENTITY_TYPE, id)));
    }

    public static void initialization(){
        NullCore.LOG.info("Success initialization entities!");
    }
}
