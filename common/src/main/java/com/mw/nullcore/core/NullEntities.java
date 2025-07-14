package com.mw.nullcore.core;

import com.mw.nullcore.NullCore;
import com.mw.nullcore.core.entities.AdvancedItemEntity;
import com.mw.nullcore.platform.IPlatform;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class NullEntities {
    public static final Supplier<EntityType<AdvancedItemEntity>> advancedItem = register("advanced_item", AdvancedItemEntity::new, MobCategory.MISC, 0.25F, 0.25F,0.2125F, 6, 20);

    private static <E extends Entity> Supplier<EntityType<E>> register(String id, EntityType.EntityFactory<E> factory, MobCategory category, float width, float height, float eyeHeight, int trackingRange, int updateInterval) {
        return IPlatform.PLATFORM.register(BuiltInRegistries.ENTITY_TYPE, id, ()-> EntityType.Builder.of(factory, category).sized(width, height).eyeHeight(eyeHeight).clientTrackingRange(trackingRange).updateInterval(updateInterval).build(NullCore.defaultKey(Registries.ENTITY_TYPE, id)));
    }

    public static void initialization(){
        NullCore.LOG.info("Success initialization entities!");
    }
}
