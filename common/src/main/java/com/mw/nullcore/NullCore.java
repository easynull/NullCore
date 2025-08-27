package com.mw.nullcore;

import com.mw.nullcore.client.render.ShyBlockRender;
import com.mw.nullcore.client.render.ShyItemRender;
import com.mw.nullcore.registers.NullComponents;
import com.mw.nullcore.registers.NullEntities;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

public final class NullCore {
	public static final String ID = "nullcore";
	public static final Logger LOG = LoggerFactory.getLogger("NullCore");

	public static ResourceLocation path(String id){
		return ResourceLocation.fromNamespaceAndPath(ID, id);
	}

	public static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> key, String id) {
		return ResourceKey.create(key, path(id));
	}

	public static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> key, String modid, String id) {
		return ResourceKey.create(key, ResourceLocation.fromNamespaceAndPath(modid, id));
	}

	public static void init() {
		NullComponents.initialization();
		NullEntities.initialization();
	}

	public static void registerEntityRenderers(BiConsumer<EntityType<? extends Entity>, EntityRendererProvider> consumer) {
		consumer.accept(NullEntities.shyItem.get(), ShyItemRender::new);
        consumer.accept(NullEntities.shyBlock.get(), ShyBlockRender::new);
	}
}