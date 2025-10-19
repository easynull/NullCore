package com.mw.nullcore.client.particle.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mw.nullcore.core.NcUtils;
import com.mw.nullcore.core.NcConfig;
import com.mw.nullcore.core.items.Renderable;
import com.mw.nullcore.client.particle.screen.keys.ScreenParticleItemStackKey;
import com.mw.nullcore.client.particle.screen.keys.ScreenParticleItemStackRetrievalKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.util.*;

public final class ScreenParticleHandler {
    public static final Map<ScreenParticleItemStackKey, ScreenParticleHolder> ITEM_PARTICLES = new HashMap<>();
    public static final Map<ScreenParticleItemStackRetrievalKey, ItemStack> ITEM_STACK_CACHE = new HashMap<>();
    public static final Collection<ScreenParticleItemStackRetrievalKey> ACTIVELY_ACCESSED_KEYS = new ArrayList<>();

    public static ScreenParticleHolder cachedItemParticles = null;
    public static int currentItemX, currentItemY;

    public static final Tesselator tess = new Tesselator();
    public static boolean canSpawnParticles;
    public static boolean renderingHotbar;

    public static void tick() {
        if (!NcConfig.enableGuiVFX.get()) return;
        ITEM_PARTICLES.values().forEach(ScreenParticleHolder::tick);
        ITEM_PARTICLES.values().removeIf(ScreenParticleHolder::isEmpty);

        ITEM_STACK_CACHE.keySet().removeIf(k -> !ACTIVELY_ACCESSED_KEYS.contains(k));
        ACTIVELY_ACCESSED_KEYS.clear();
        canSpawnParticles = true;
    }

    public static void render() {
    }

    public static void renderItemStackEarly(PoseStack poseStack, ItemStack stack, int x, int y) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null && minecraft.player != null) {
            if (minecraft.isPaused()) {
                return;
            }

            if (!stack.isEmpty()) {
                List<Renderable> emitters = ParticleEmitterHandler.EMITTERS.get(stack.getItem());
                if (emitters != null) {
                    final Matrix4f pose = poseStack.last().pose();
                    int xOffset = (int) (8 + pose.m30());
                    int yOffset = (int) (8 + pose.m31());
                    currentItemX = x + xOffset;
                    currentItemY = y + yOffset;
                    for (Renderable emitter : emitters) {
                        ScreenParticleHandler.renderParticles(ScreenParticleHandler.spawnAndPullParticles(minecraft.level, emitter, stack, false));
                        cachedItemParticles = ScreenParticleHandler.spawnAndPullParticles(minecraft.level, emitter, stack, true);
                    }
                }
            }
        }
    }

    public static ScreenParticleHolder spawnAndPullParticles(ClientLevel level, Renderable emitter, ItemStack stack, boolean isRenderedAfterItem) {
        ScreenParticleItemStackRetrievalKey cacheKey = new ScreenParticleItemStackRetrievalKey(renderingHotbar, isRenderedAfterItem, currentItemX, currentItemY);
        ScreenParticleHolder target = ITEM_PARTICLES.computeIfAbsent(new ScreenParticleItemStackKey(renderingHotbar, isRenderedAfterItem, stack), s -> new ScreenParticleHolder());
        pullFromParticleVault(cacheKey, stack, target, isRenderedAfterItem);
        if (canSpawnParticles) {
            if (isRenderedAfterItem) {
                emitter.renderLate(target, null, level, NcUtils.partialTick, stack, currentItemX, currentItemY);
            } else {
                emitter.renderEarly(target, null, level, NcUtils.partialTick, stack, currentItemX, currentItemY);
            }
        }

        ACTIVELY_ACCESSED_KEYS.add(cacheKey);
        return target;
    }

    public static void pullFromParticleVault(ScreenParticleItemStackRetrievalKey cacheKey, ItemStack currentStack, ScreenParticleHolder target, boolean isRenderedAfterItem) {
        if (ITEM_STACK_CACHE.containsKey(cacheKey)) {
            ItemStack oldStack = ITEM_STACK_CACHE.get(cacheKey);
            if (oldStack != currentStack && oldStack.getItem().equals(currentStack.getItem())) {
                ScreenParticleItemStackKey oldKey = new ScreenParticleItemStackKey(renderingHotbar, isRenderedAfterItem, oldStack);
                ScreenParticleHolder oldParticles = ITEM_PARTICLES.get(oldKey);
                if (oldParticles != null) {
                    target.addFrom(oldParticles);
                }

                ITEM_STACK_CACHE.remove(cacheKey);
                ITEM_PARTICLES.remove(oldKey);
            }
        }

        ITEM_STACK_CACHE.put(cacheKey, currentStack);
    }

    public static void renderItemStackLate() {
        if (cachedItemParticles != null) {
            renderParticles(cachedItemParticles);
            cachedItemParticles = null;
        }
    }

    public static void renderParticles(ScreenParticleHolder screenParticleTarget) {
        screenParticleTarget.particles.forEach((renderType, particles) -> {
            var buffer = renderType.begin(tess, Minecraft.getInstance().getTextureManager());
            for (ScreenParticle next : particles) {
                next.render(buffer);
            }
            renderType.end(buffer);
        });
    }

    public static void clearParticles() {
        ITEM_PARTICLES.values().forEach(ScreenParticleHandler::clearParticles);
    }

    public static void clearParticles(ScreenParticleHolder screenParticleTarget) {
        screenParticleTarget.particles.values().forEach(ArrayList::clear);
    }

    public static <T extends ScreenParticleOptions> void addParticle(ScreenParticleHolder target, T options, double x, double y, double xMotion, double yMotion) {
        Minecraft minecraft = Minecraft.getInstance();
        ScreenParticleType<T> type = (ScreenParticleType<T>) options.type;
        ScreenParticle particle = type.provider.createParticle(minecraft.level, options, x, y, xMotion, yMotion);
        ArrayList<ScreenParticle> list = target.particles.computeIfAbsent(options.renderType, (a) -> new ArrayList<>());
        list.add(particle);
    }
}
