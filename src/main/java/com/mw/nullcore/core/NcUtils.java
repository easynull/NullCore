package com.mw.nullcore.core;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.JsonOps;
import com.mw.nullcore.NullCore;
import com.mw.nullcore.client.render.ShyModel;
import com.mw.nullcore.core.blocks.type.ContainerBlockEntity;
import com.mw.nullcore.core.builders.ArmorMaterialBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public final class NcUtils {
    @OnlyIn(Dist.CLIENT)
    public static final @NotNull Minecraft mc = Minecraft.getInstance();
    @OnlyIn(Dist.CLIENT)
    public static final @NotNull Font font = mc.font;
    @OnlyIn(Dist.CLIENT)
    public static final float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
    @OnlyIn(Dist.CLIENT)
    public static int clientTick;
    public static final RandomSource rand = RandomSource.create(1L);

    public static final class Block {
        public static void forEachCube(BlockPos center, int radius, Consumer<BlockPos> action) {
            forEachInVolume(center, radius, radius, radius, pos -> true, action);
        }

        public static void forEachSphere(BlockPos center, int radius, Consumer<BlockPos> action) {
            int radiusSq = radius * radius;
            forEachInVolume(center, radius, radius, radius, pos -> {
                int dx = pos.getX() - center.getX();
                int dy = pos.getY() - center.getY();
                int dz = pos.getZ() - center.getZ();
                return dx * dx + dy * dy + dz * dz <= radiusSq;
            }, action);
        }

        public static void forEachCircle(BlockPos center, int radius, int height, Consumer<BlockPos> action) {
            int radiusSq = radius * radius;
            forEachInVolume(center, radius, height, radius, pos -> {
                int dx = pos.getX() - center.getX();
                int dz = pos.getZ() - center.getZ();
                return dx * dx + dz * dz <= radiusSq;
            }, action);
        }

        public static void forEachDiamond(BlockPos center, int radius, int height, Consumer<BlockPos> action) {
            if (radius <= 0 || height <= 0) return;
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            for (int y = 0; y <= height; y++) {
                int currentRadius = radius * (height - y) / height;
                for (int x = -currentRadius; x <= currentRadius; x++) {
                    for (int z = -currentRadius; z <= currentRadius; z++) {
                        if (Math.abs(x) + Math.abs(z) <= currentRadius) {
                            mutablePos.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                            action.accept(mutablePos);
                            if (y != 0) {
                                mutablePos.setY(center.getY() - y);
                                action.accept(mutablePos);
                            }
                        }
                    }
                }
            }
        }

        public static void forEachNeighbor(BlockPos pos, Consumer<BlockPos> action) {
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            for (Direction dir : Direction.values()) {
                action.accept(mutablePos.setWithOffset(pos, dir));
            }
        }

        public static ToIntFunction<BlockState> lightLit(int value, int baseValue) {
            return state -> state.getValue(BlockStateProperties.LIT) ? value : baseValue;
        }

        public static boolean isFluid(BlockState state) {
            return state.getFluidState().isSource();
        }

        private static void forEachInVolume(BlockPos center, int xRad, int yRad, int zRad, Predicate<BlockPos> filter, Consumer<BlockPos> action) {
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            for (int x = -xRad; x <= xRad; x++) {
                for (int y = -yRad; y <= yRad; y++) {
                    for (int z = -zRad; z <= zRad; z++) {
                        mutablePos.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                        if (filter.test(mutablePos)) {
                            action.accept(mutablePos);
                        }
                    }
                }
            }
        }

        public static boolean isFullVoxel(BlockState state, boolean inHeight) {
            VoxelShape shape = state.getCollisionShape(null, null);
            return inHeight ? shape.bounds().minY == 0.0 && shape.bounds().maxY == 1.0 : (shape.bounds().minX == 0.0 && shape.bounds().maxX == 1.0 && shape.bounds().minY == 0.0 && shape.bounds().maxY == 1.0 && shape.bounds().minZ == 0.0 && shape.bounds().maxZ == 1.0);
        }

        public static void updateBlockEntity(BlockEntity be) {
            if (be.getLevel() == null) return;
            be.setChanged();
            be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
        }

        public static boolean createLootableChest(LevelAccessor level, BlockPos pos, ResourceLocation loot) {
            return createLootableChest(Blocks.CHEST, level, pos, loot);
        }

        public static boolean createLootableChest(net.minecraft.world.level.block.Block chest, LevelAccessor level, BlockPos pos, ResourceLocation loot) {
            level.setBlock(pos, chest.defaultBlockState(), 3);
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RandomizableContainerBlockEntity r) {
                r.setLootTable(ResourceKey.create(Registries.LOOT_TABLE, loot));
                return true;
            }
            return false;
        }
    }

    public static final class Item {
        public static NonNullList<ItemStack> inventoryToList(Container inv) {
            NonNullList<ItemStack> list = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
            for (int i = 0; i < inv.getContainerSize(); i++) {
                list.set(i, inv.getItem(i).copy());
            }
            return list;
        }

        public static boolean insertItem(ContainerBlockEntity be, Player player, int slot, int maxTransfer) {
            SimpleContainer inv = be.getInventory();
            ItemStack slotStack = inv.getItem(slot);
            ItemStack heldStack = player.getMainHandItem();
            int actualMax = Math.min(maxTransfer, be.maxInSlot);

            if (heldStack.isEmpty()) {
                if (slotStack.isEmpty()) return false;
                int transferAmount = Math.min(slotStack.getCount(), actualMax);

                player.setItemInHand(InteractionHand.MAIN_HAND, slotStack.copy());
                slotStack.shrink(transferAmount);
                inv.setItem(slot, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
                return true;
            } else if (slotStack.isEmpty()) {
                int transferAmount = Math.min(heldStack.getCount(), actualMax);
                ItemStack insert = heldStack.copyWithCount(transferAmount);

                inv.setItem(slot, insert);
                heldStack.shrink(transferAmount);
                return true;
            } else if (ItemStack.isSameItem(slotStack, heldStack)) {
                int spaceAvailable = Math.min(be.maxInSlot, slotStack.getMaxStackSize()) - slotStack.getCount();
                int transferAmount = Math.min(Math.min(heldStack.getCount(), spaceAvailable), actualMax);

                if (transferAmount <= 0) return false;

                slotStack.grow(transferAmount);
                heldStack.shrink(transferAmount);
                inv.setItem(slot, slotStack);
                return true;
            } else {
                inv.setItem(slot, heldStack.copy());
                player.setItemInHand(InteractionHand.MAIN_HAND, slotStack.copy());
                return true;
            }
        }

        public static LootTable getLootTable(ServerLevel level, ResourceKey<LootTable> id) {
            return level.getServer().reloadableRegistries().getLootTable(id);
        }

        public static LootParams getGiftParams(ServerLevel level, Vec3 pos, Entity entity, float luck) {
            return new LootParams.Builder(level).withParameter(LootContextParams.THIS_ENTITY, entity).withParameter(LootContextParams.ORIGIN, pos).withLuck(luck).create(LootContextParamSets.GIFT);
        }

        public static LootParams getParamsWithPlayer(ServerLevel level, Vec3 pos, Entity entity, float luck, ContextKeySet param) {
            ServerPlayer fakePlayer = FakePlayerFactory.getMinecraft(level);
            return new LootParams.Builder(level).withParameter(LootContextParams.THIS_ENTITY, entity).withParameter(LootContextParams.ORIGIN, pos).withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().playerAttack(fakePlayer)).withParameter(LootContextParams.LAST_DAMAGE_PLAYER, fakePlayer).withParameter(LootContextParams.ATTACKING_ENTITY, fakePlayer).withLuck(luck).create(param);
        }

        public static List<ItemStack> createLoot(ResourceKey<LootTable> id, LootParams params) {
            LootTable loot = getLootTable(params.getLevel(), id);
            if (loot == LootTable.EMPTY) return Lists.newArrayList();
            return loot.getRandomItems(params);
        }

        public static void giveLoot(Player player, List<ItemStack> items) {
            for (ItemStack stack : items) {
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
            }
        }

        public static void spawnLoot(net.minecraft.world.level.Level level, BlockPos pPos, Collection<ItemStack> items) {
            if (!level.isClientSide()) {
                for (ItemStack stack : items) {
                    level.addFreshEntity(new ItemEntity(level, pPos.getX() + 0.5F, pPos.getY() + 0.5F, pPos.getZ() + 0.5F, stack));
                }
            }
        }

        public static <T> Optional<DataComponentType<T>> hasComponent(ItemStack stack, Supplier<DataComponentType<T>> comp, Consumer<T> cons) {
            if (stack.has(comp)) {
                cons.accept(stack.get(comp.get()));
                return Optional.of(comp.get());
            }
            return Optional.empty();
        }

        public static <T> void instanceOf(net.minecraft.world.item.Item item, Class<T> targetClass, Consumer<T> action) {
            Object target = item instanceof BlockItem bi ? bi.getBlock() : item;
            if (targetClass.isInstance(target)) {
                action.accept(targetClass.cast(target));
            }
        }
    }

    public static final class Client {
        @OnlyIn(Dist.CLIENT)
        public static void setSafeScreen(Screen screen) {
            try {
                if (!mc.isSameThread()) {
                    mc.execute(() -> setSafeScreen(screen));
                    return;
                }
                if (mc.level == null || mc.player == null) return;
                if (!mc.level.isClientSide()) return;
                mc.setScreen(screen);
            } catch (Exception ignored) {}
        }

        public static boolean hasAdvancement(ServerPlayer player, ResourceLocation id) {
            AdvancementHolder advancement = player.server.getAdvancements().get(id);
            return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
        }

        @OnlyIn(Dist.CLIENT)
        public static void sendMessage(Player player, Component key) {
            if (key != null) {
                player.displayClientMessage(key, true);
            }
        }
    }

    public static final class Color {
        public static float[] unpack(int color, boolean asARGB) {
            return asARGB ?
                    new float[]{
                            ((color >> 16) & 0xFF) / 255f,
                            ((color >> 8) & 0xFF) / 255f,
                            (color & 0xFF) / 255f,
                            ((color >> 24) & 0xFF) / 255f
                    } :
                    new float[]{
                            ((color >> 24) & 0xFF) / 255f,
                            ((color >> 16) & 0xFF) / 255f,
                            ((color >> 8) & 0xFF) / 255f,
                            (color & 0xFF) / 255f
                    };
        }

        public static int pack(float r, float g, float b, float a, boolean asARGB) {
            return asARGB ? ((int) (a * 255) << 24) | ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255) : ((int) (r * 255) << 24) | ((int) (g * 255) << 16) | ((int) (b * 255) << 8) | (int) (a * 255);
        }

        public static int hexPack(String hexColor) {
            if (hexColor.startsWith("#")) {
                hexColor = hexColor.substring(1);
            }
            if (hexColor.length() == 6) {
                hexColor = "FF" + hexColor;
            }
            return (int) Long.parseLong(hexColor, 16);
        }

        public static int lerpColors(float progress, int... colors) {
            float segment = progress * (colors.length - 1);
            int index = (int) segment;
            float factor = segment - index;

            if (index >= colors.length - 1) {
                index = colors.length - 2;
                factor = 1f;
            }

            float[] c1 = unpack(colors[index], false);
            float[] c2 = unpack(colors[index + 1], false);

            return pack(net.minecraft.util.Mth.lerp(factor, c1[0], c2[0]), net.minecraft.util.Mth.lerp(factor, c1[1], c2[1]), net.minecraft.util.Mth.lerp(factor, c1[2], c2[2]), net.minecraft.util.Mth.lerp(factor, c1[3], c2[3]), false);
        }

        public static int getCyclingColor(float speed, int... colors) {
            float time = Render.getAnimationTick() * speed;
            return lerpColors((net.minecraft.util.Mth.sin(time) + 1f) / 2f, colors);
        }

        public static int getRainbow(float speed) {
            int[] rainbow = new int[36];
            for (int i = 0; i < rainbow.length; i++) {
                rainbow[i] = java.awt.Color.HSBtoRGB(i / 36f, 1f, 1f) & 0xFFFFFF;
            }
            return getCyclingColor(speed, rainbow);
        }

        public static float getRed(int color) {
            return (color >> 16 & 0xFF) / 255.0F;
        }

        public static float getGreen(int color) {
            return (color >> 8 & 0xFF) / 255.0F;
        }

        public static float getBlue(int color) {
            return (color & 0xFF) / 255.0F;
        }

        public static float getAlpha(int color) {
            return (color >> 24 & 0xFF) / 255.0F;
        }
    }

    public static final class Mth {
        public static final Vector3f VZERO = new Vector3f(0, 0, 0);

        public static boolean chance(float chance) {
            return rand.nextFloat() < net.minecraft.util.Mth.clamp(chance, 0, (byte) 1);
        }

        public static String formatRealTime(long gameTime) {
            long totalSeconds = gameTime / 20;
            long hours = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            long seconds = totalSeconds % 60;
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        public static float perlinNoise(float x, float y, float z, float frequency, float amplitude) {
            float noise = 0;
            for (int i = 0; i < 3; i++) {
                noise += (float) (Math.sin(x * frequency) * Math.cos(y * frequency) * (z != 0 ? Math.sin(z * frequency) : 1) * amplitude);
                frequency *= 2.0f;
                amplitude *= 0.4f;
            }

            return noise;
        }

        public static int secondTick(int seconds) {
            return seconds * 20;
        }

        public static int minuteTick(int minutes) {
            return secondTick(minutes * 60);
        }

        public static int hourTick(int hours) {
            return minuteTick(hours * 60);
        }

        public static int dayTick(int days) {
            return hourTick(days * 24);
        }

        public static float calculateProgress(float current, float max, float minOutput, float maxOutput) {
            if (max <= 0) return minOutput;
            float progress = current / max;
            progress = net.minecraft.util.Mth.clamp(progress, 0.0f, 1.0f);
            return net.minecraft.util.Mth.lerp(progress, minOutput, maxOutput);
        }
    }

    public static final class Particle {
        public static void forParticleSpawn(net.minecraft.world.level.Level level, ParticleOptions particle, float pX, float pY, float pZ, float xSpeed, float ySpeed, float zSpeed, int count) {
            RandomSource rand = level.random;
            for (int i = 0; i < count; i++) {
                double oX = (rand.nextDouble() - 0.5) * 0.5;
                double oY = (rand.nextDouble() - 0.5) * 0.5;
                double oZ = (rand.nextDouble() - 0.5) * 0.5;
                level.addParticle(particle, pX + oX, pY + oY, pZ + oZ, xSpeed, ySpeed, zSpeed);
            }
        }

        public static void forParticleSpawn(net.minecraft.world.level.Level level, ParticleOptions particle, float pX, float pY, float pZ, int count) {
            forParticleSpawn(level, particle, pX, pY, pZ, 0, 0, 0, count);
        }

        public static <T extends ParticleOptions> boolean sendPlayerParticles(ServerPlayer player, T particle, double posX, double posY, double posZ, int count, double xDist, double yDist, double zDist, double maxSpeed) {
            ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(particle, false, false, posX, posY, posZ, (float)xDist, (float)yDist, (float)zDist, (float)maxSpeed, count);
            BlockPos playerPos = player.blockPosition();
            if (playerPos.closerToCenterThan(new Vec3(posX, posY, posZ), 32.0f)) {
                player.connection.send(packet);
                return true;
            }
            return false;
        }

        public static void writeParticle(CompoundTag tag, ParticleOptions particle) {
            CompoundTag pTag = new CompoundTag();
            ResourceLocation loc = BuiltInRegistries.PARTICLE_TYPE.getKey(particle.getType());
            pTag.putString("namespace", loc.getNamespace());
            pTag.putString("path", loc.getPath());
            tag.put("particle", pTag);
        }

        public static ParticleOptions readParticle(CompoundTag tag) {
            CompoundTag pTag = (CompoundTag) tag.get("particle");
            return (ParticleOptions) BuiltInRegistries.PARTICLE_TYPE.getOptional(ResourceLocation.fromNamespaceAndPath(pTag.getString("namespace"), pTag.getString("path"))).get();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static final class Render {
        public static final MultiBufferSource mBuffer = mc.renderBuffers().bufferSource();

        public static void drawTexture(GuiGraphics gg, ResourceLocation texture, int x, int y, int u, int v, int pixelWidth, int pixelHeight, int width, int height, int color) {
            gg.blit(RenderType::guiTextured, texture, x, y, u, v, pixelWidth, pixelHeight, width, height, color);
        }

        public static void drawTexture(GuiGraphics gg, ResourceLocation texture, int x, int y, int u, int v, int pixelWidth, int pixelHeight, int width, int height) {
            drawTexture(gg, texture, x, y, u, v, pixelWidth, pixelHeight, width, height, 0xFFFFFFFF);
        }

        public static void drawFullTexture(GuiGraphics gui, ResourceLocation texture, float x, float y, float size, int color) {
            gui.blit(RenderType::guiTextured, texture, (int)x, (int)y, 0, 0, (int)size, (int)size, (int)size, (int)size, color);
        }

        public static void drawFullTexture(GuiGraphics gui, ResourceLocation texture, float x, float y, float size) {
            drawFullTexture(gui, texture, x, y, size, 0xFFFFFFFF);
        }

        public static void drawLine(PoseStack ps, VertexConsumer buffer, float x1, float y1, float z1, float x2, float y2, float z2, int color, float width) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.lineWidth(width);

            PoseStack.Pose pose = ps.last();
            buffer.addVertex(pose.pose(), x1, y1, z1).setColor(color);
            buffer.addVertex(pose.pose(), x2, y2, z2).setColor(color);

            RenderSystem.disableBlend();
        }

        public static void drawText(GuiGraphics gg, Object text, int x, int y, int color, boolean shadow) {
            String str = text instanceof Component c ? c.getString() : text.toString();
            gg.drawString(font, str, x, y, color, shadow);
        }

        public static void drawText(GuiGraphics gg, Object text, int x, int y, int color) {
            drawText(gg, text, x, y, color, false);
        }

        public static void renderRays(PoseStack ps, VertexConsumer buffer, int color, float time, float pTick) {
            ps.pushPose();
            float rotationTime = ((clientTick + pTick) * rand.nextFloat() + 0.5f) * time;
            float[] rgb = Color.unpack(color, false);
            int count = rand.nextInt(10, 25);
            final Vector3f center = new Vector3f(0, 0, 0);
            for (int l = 0; l < count; l++) {
                float length = rand.nextFloat();
                float width = rand.nextFloat() * 0.3f;
                Vector3f left = new Vector3f(-0.866f * width, length, -0.5F * width);
                Vector3f right = new Vector3f(0.866f * width, length, -0.5F * width);
                Vector3f front = new Vector3f(0.0F, length, width);

                Quaternionf rotate = new Quaternionf().rotationXYZ(rand.nextFloat() * net.minecraft.util.Mth.TWO_PI + rotationTime * 0.05f, net.minecraft.util.Mth.TWO_PI + rotationTime * 0.08f, rand.nextFloat() * net.minecraft.util.Mth.TWO_PI).rotateXYZ(rand.nextFloat() * net.minecraft.util.Mth.TWO_PI, rand.nextFloat() * net.minecraft.util.Mth.TWO_PI, rand.nextFloat() * net.minecraft.util.Mth.TWO_PI + rotationTime * 0.06f);
                ps.mulPose(rotate);
                PoseStack.Pose pose = ps.last();
                buffer.addVertex(pose, center).setColor(1.0f, rgb[1] - 0.2f, rgb[2] - 0.2f, 1.0f);
                buffer.addVertex(pose, left).setColor(rgb[0], rgb[1], rgb[2], 0.0f);
                buffer.addVertex(pose, right).setColor(rgb[0], rgb[1], rgb[2], 0.0f);

                buffer.addVertex(pose, center).setColor(1.0f, rgb[1] - 0.2f, rgb[2] - 0.2f, 1.0f);
                buffer.addVertex(pose, right).setColor(rgb[0], rgb[1], rgb[2], 0.0f);
                buffer.addVertex(pose, front).setColor(rgb[0], rgb[1], rgb[2], 0.0f);

                buffer.addVertex(pose, center).setColor(1.0f, rgb[1] - 0.2f, rgb[2] - 0.2f, 1.0f);
                buffer.addVertex(pose, front).setColor(rgb[0], rgb[1], rgb[2], 0.0f);
                buffer.addVertex(pose, left).setColor(rgb[0], rgb[1], rgb[2], 0.0f);
                ps.mulPose(rotate.invert());
            }
            ps.popPose();
        }

        private static Vector3f withValue(Vector3f vector, Direction.Axis axis, float value) {
            if (axis == Direction.Axis.X) {
                return new Vector3f(value, vector.y(), vector.z());
            } else if (axis == Direction.Axis.Y) {
                return new Vector3f(vector.x(), value, vector.z());
            } else if (axis == Direction.Axis.Z) {
                return new Vector3f(vector.x(), vector.y(), value);
            }
            throw new RuntimeException("Was given a null axis! That was probably not intentional, consider this a bug! (Vector = " + vector + ")");
        }

        public static double getValue(Vec3 vector, Direction.Axis axis) {
            if (axis == Direction.Axis.X) {
                return vector.x;
            } else if (axis == Direction.Axis.Y) {
                return vector.y;
            } else if (axis == Direction.Axis.Z) {
                return vector.z;
            }
            throw new RuntimeException("Was given a null axis! That was probably not intentional, consider this a bug! (Vector = " + vector + ")");
        }

        private static final int U0 = 0;
        private static final int U1 = 1;
        private static final int V0 = 2;
        private static final int V1 = 3;
        public static void renderCube(ShyModel cube, PoseStack ps, VertexConsumer buffer, int argb, int light, int overlay) {
            float red = Color.getRed(argb);
            float green = Color.getGreen(argb);
            float blue = Color.getBlue(argb);
            float alpha = Color.getAlpha(argb);
            Vec3 size = new Vec3(cube.sizeX(), cube.sizeY(), cube.sizeZ());
            ps.pushPose();
            ps.translate(cube.getMinX(), cube.getMinY(), cube.getMinZ());
            PoseStack.Pose pose = ps.last();
            Matrix4f mat = pose.pose();
            for (Direction face : Direction.values()) {
                if (cube.shouldSideRender(face)) {
                    int ordinal = face.ordinal();
                    TextureAtlasSprite sprite = cube.textures[ordinal];
                    if (sprite != null) {
                        Direction.Axis u = face.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
                        Direction.Axis v = face.getAxis() == Direction.Axis.Y ? Direction.Axis.Z : Direction.Axis.Y;
                        float other = face.getAxisDirection() == Direction.AxisDirection.POSITIVE ? (float) getValue(size, face.getAxis()) : 0;

                        face = face.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? face : face.getOpposite();
                        Direction opposite = face.getOpposite();

                        float minU = sprite.getU0();
                        float maxU = sprite.getU1();
                        float minV = sprite.getV1();
                        float maxV = sprite.getV0();
                        double sizeU = getValue(size, u);
                        double sizeV = getValue(size, v);
                        for (int uIndex = 0; uIndex < sizeU; uIndex++) {
                            float[] baseUV = new float[] { minU, maxU, minV, maxV };
                            double addU = 1;
                            if (uIndex + addU > sizeU) {
                                addU = sizeU - uIndex;
                                baseUV[U1] = baseUV[U0] + (baseUV[U1] - baseUV[U0]) * (float) addU;
                            }
                            for (int vIndex = 0; vIndex < sizeV; vIndex++) {
                                float[] uv = Arrays.copyOf(baseUV, 4);
                                double addV = 1;
                                if (vIndex + addV > sizeV) {
                                    addV = sizeV - vIndex;
                                    uv[V1] = uv[V0] + (uv[V1] - uv[V0]) * (float) addV;
                                }
                                float[] xyz = new float[] { uIndex, (float) (uIndex + addU), vIndex, (float) (vIndex + addV) };

                                renderPoint(mat, pose, buffer, face, u, v, other, uv, xyz, true, false, red, green, blue, alpha, light, overlay);
                                renderPoint(mat, pose, buffer, face, u, v, other, uv, xyz, true, true, red, green, blue, alpha, light, overlay);
                                renderPoint(mat, pose, buffer, face, u, v, other, uv, xyz, false, true, red, green, blue, alpha, light, overlay);
                                renderPoint(mat, pose, buffer, face, u, v, other, uv, xyz, false, false, red, green, blue, alpha, light, overlay);

                                renderPoint(mat, pose, buffer, opposite, u, v, other, uv, xyz, false, false, red, green, blue, alpha, light, overlay);
                                renderPoint(mat, pose, buffer, opposite, u, v, other, uv, xyz, false, true, red, green, blue, alpha, light, overlay);
                                renderPoint(mat, pose, buffer, opposite, u, v, other, uv, xyz, true, true, red, green, blue, alpha, light, overlay);
                                renderPoint(mat, pose, buffer, opposite, u, v, other, uv, xyz, true, false, red, green, blue, alpha, light, overlay);
                            }
                        }
                    }
                }
            }
            ps.popPose();
        }

        public static void renderPoint(Matrix4f matrix4f, PoseStack.Pose normal, VertexConsumer buffer, Direction face, Direction.Axis u, Direction.Axis v, float other, float[] uv, float[] xyz, boolean minU, boolean minV, float red, float green, float blue, float alpha, int light, int overlay) {
            int U_ARRAY = minU ? U0 : U1;
            int V_ARRAY = minV ? V0 : V1;
            Vector3f vertex = withValue(Mth.VZERO, u, xyz[U_ARRAY]);
            vertex = withValue(vertex, v, xyz[V_ARRAY]);
            vertex = withValue(vertex, face.getAxis(), other);
            Vec3i normalForFace = face.getUnitVec3i();
            float adjustment = 2.5F;
            Vector3f norm = new Vector3f(normalForFace.getX() + adjustment, normalForFace.getY() + adjustment, normalForFace.getZ() + adjustment);
            norm.normalize();
            buffer.addVertex(matrix4f, vertex.x(), vertex.y(), vertex.z()).setColor(red, green, blue, alpha).setUv(uv[U_ARRAY], uv[V_ARRAY]).setOverlay(overlay).setUv2(light, light).setNormal(normal, norm.x(), norm.y(), norm.z());
        }

        public static float getAnimationTick(float pTick) {
            return pTick + clientTick;
        }

        public static float getAnimationTick() {
            return getAnimationTick(partialTick);
        }

        public static TextureAtlasSprite getSprite(ResourceLocation texture) {
            TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS);
            return atlas.getSprite(texture);
        }

        public static TextureAtlasSprite getSpriteOf(ResourceLocation texture) {
            return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
        }
    }

    public static final class Text {
        public static void addTooltipWithKey(int button, int colorButton, List<Component> adder, Component... components) {
            if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), button)) {
                for (Component cm : components) {
                    if (cm != null) adder.add(cm);
                }
            } else {
                adder.add(Component.translatable("tooltip.nullcore.info", Component.translatable(InputConstants.getKey(button, 0).toString()).withColor(colorButton)));
            }
        }

        public static String formatNum(float amount) {
            String[] sufx = {"", "K", "M", "B", "T", "Qa", "Qt", "Sx", "Sp", "Oc", "N", "D"};
            int suffixIndex = 0;
            float scaledNumber = amount;
            while (scaledNumber >= 1000 && suffixIndex < sufx.length - 1) {
                scaledNumber /= 1000;
                suffixIndex++;
            }
            DecimalFormat df = new DecimalFormat("#.#");
            return df.format(scaledNumber) + sufx[suffixIndex];
        }

        public static Component addLinerTextGradient(Object text, float speed, boolean toRight, int... colors) {
            String str = text instanceof Component c ? c.getString() : text.toString();
            RandomSource rand = RandomSource.create(1L);
            float offset = (toRight ? -(Render.getAnimationTick() * rand.nextFloat() + 0.5f) : (Render.getAnimationTick() * rand.nextFloat() + 0.5f)) * speed;
            MutableComponent component = Component.empty();
            int length = str.length();
            if (length == 0 || colors.length == 0) return component;
            offset %= 1.0f;
            if (offset < 0) offset += 1.0f;

            for (int i = 0; i < length; i++) {
                float pos = (float) i / length + offset;
                pos %= 1.0f;

                float colorPos = pos * (colors.length - 1);
                int colorIndex = (int) colorPos;
                float lerp = colorPos - colorIndex;

                int color1 = colors[colorIndex % colors.length];
                int color2 = colors[(colorIndex + 1) % colors.length];
                int color = Color.lerpColors(lerp, color1, color2);

                component.append(Component.literal(String.valueOf(str.charAt(i))).withStyle(style -> style.withColor(color)));
            }
            return component;
        }

        public static Component addTextGradient(Object text, float speed, int... colors) {
            String str = text instanceof Component c ? c.getString() : text.toString();
            int color = Color.getCyclingColor(speed, colors);
            return Component.literal(str).withColor(TextColor.fromRgb(color & 0xFFFFFF).getValue());
        }

        public static void addPositionTooltip(List<Component> tooltip, BlockPos pos, Component key, int colorPos, int colorData) {
            if (pos != null) {
                tooltip.add(key.copy().withColor(colorPos));
                tooltip.add(Component.literal(String.format("X: %d, Y: %d, Z: %d", pos.getX(), pos.getY(), pos.getZ())).withColor(colorData));
            }
        }

        public static void addTooltipEffects(List<Component> tooltip, MutableComponent component, List<ArmorMaterialBuilder.ShyEffect> list) {
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    MobEffect effect = list.get(i).effect().value();
                    var effectName = effect.getDisplayName().getString();
                    component.append(Component.literal(effectName).withColor(effect.getColor()));
                    if (i < list.size() - 1) {
                        component.append(Component.literal(", ").withStyle(component.getStyle()));
                    }
                }
                tooltip.add(component);
            }
        }
    }

    public static final class Level {
        public static void setBiome(ServerLevel level, ResourceKey<Biome> biome, BlockPos from, BlockPos to) {
            FillBiomeCommand.fill(level, from, to, getBiome(level, biome));
        }

        public static void setBiome(ServerLevel level, ResourceKey<Biome> biome, BlockPos pos) {
            setBiome(level, biome, pos, pos);
        }

        public static Holder<Biome> getBiome(net.minecraft.world.level.Level level, ResourceKey<Biome> biome) {
            return level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(biome);
        }

        public static ResourceLocation getBiome(net.minecraft.world.level.Level level, Biome biome) {
            return level.registryAccess().lookupOrThrow(Registries.BIOME).getKey(biome);
        }

        public static List<ResourceLocation> getStructuresAt(ServerLevel level, BlockPos pos) {
            Registry<Structure> structureRegistry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
            return level.structureManager().getAllStructuresAt(pos).keySet().stream().map(structureRegistry::getKey).filter(Objects::nonNull).toList();
        }

        public static boolean isStructure(net.minecraft.world.level.Level level, BlockPos pos, ResourceLocation structureId) {
            if (!(level instanceof ServerLevel sl)) return false;
            Registry<Structure> registry = sl.registryAccess().lookupOrThrow(Registries.STRUCTURE);
            Structure structure = registry.getValue(structureId);
            if (structure == null) return false;
            return sl.structureManager().getStructureAt(pos, structure).isValid();
        }

        public static List<Entity> getEntities(net.minecraft.world.level.Level level, BlockPos startPos, double radius) {
            return level.getEntities(null, new AABB(startPos.getX() - radius, startPos.getY() - radius, startPos.getZ() - radius, startPos.getX() + radius, startPos.getY() + radius, startPos.getZ() + radius));
        }
    }

    public static final class Reader {
        public static Object parseJsonValue(JsonElement element, Object defaultValue) {
            if (defaultValue instanceof Boolean) return element.getAsBoolean();
            if (defaultValue instanceof Integer) return element.getAsInt();
            if (defaultValue instanceof Byte) return element.getAsByte();
            if (defaultValue instanceof Short) return element.getAsShort();
            if (defaultValue instanceof Long) return element.getAsLong();
            if (defaultValue instanceof Float) return element.getAsFloat();
            if (defaultValue instanceof String) return element.getAsString();
            if (defaultValue instanceof Character) return element.getAsCharacter();
            if (defaultValue instanceof List) return parseList(element.getAsJsonArray(), (List<?>) defaultValue);
            return null;
        }

        public static void addValueToJson(JsonObject json, String key, Object value) {
            if (value instanceof Boolean v) {
                json.addProperty(key, v);
            } else if (value instanceof Number v) {
                json.addProperty(key, v);
            } else if (value instanceof String v) {
                json.addProperty(key, v);
            } else if (value instanceof Character v) {
                json.addProperty(key, v);
            } else if (value instanceof List<?> list) {
                JsonArray array = new JsonArray();
                for (Object item : list) {
                    addItemToJsonArray(array, item);
                }
                json.add(key, array);
            }
        }

        public static List<Object> parseList(JsonArray array, List<?> list) {
            List<Object> result = new ArrayList<>();
            if (array == null || array.isEmpty()) {
                return result;
            }
            if (!list.isEmpty()) {
                Object firstElement = list.getFirst();
                for (JsonElement element : array) {
                    result.add(parseJsonValue(element, firstElement));
                }
            } else {
                for (JsonElement element : array) {
                    if (element.isJsonPrimitive()) {
                        JsonPrimitive primitive = element.getAsJsonPrimitive();
                        if (primitive.isBoolean()) {
                            result.add(primitive.getAsBoolean());
                        } else if (primitive.isNumber()) {
                            result.add(primitive.getAsNumber());
                        } else if (primitive.isString()) {
                            result.add(primitive.getAsString());
                        }
                    } else {
                        result.add(element.toString());
                    }
                }
            }
            return result;
        }

        public static void addItemToJsonArray(JsonArray array, Object item) {
            if (item instanceof Boolean v) {
                array.add(v);
            } else if (item instanceof Number v) {
                array.add(v);
            } else if (item instanceof String v) {
                array.add(v);
            } else if (item instanceof Character v) {
                array.add(v);
            } else if (item instanceof List<?> v) {
                JsonArray nestedArray = new JsonArray();
                for (Object nestedItem : v) {
                    addItemToJsonArray(nestedArray, nestedItem);
                }
                array.add(nestedArray);
            }
        }

        public static  <T> void applyComponents(ItemStack stack, JsonObject json) {
            DataComponentPatch.Builder patch = DataComponentPatch.builder();

            json.entrySet().forEach(entry -> {
                String key = entry.getKey();
                JsonElement value = entry.getValue();

                ResourceLocation componentId = ResourceLocation.tryParse(key);
                if (componentId == null) return;

                DataComponentType<T> type = (DataComponentType<T>) BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(componentId);
                if (type == null) {
                    NullCore.LOGGER.warn("Unknown DataComponent: {}", key);
                    return;
                }

                type.codec().parse(JsonOps.INSTANCE, value).result().ifPresent(parsedValue -> patch.set(type, parsedValue));
            });

            stack.applyComponents(patch.build());
        }
    }
}