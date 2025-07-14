package com.mw.nullcore;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mw.nullcore.client.audio.TrackerController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import static com.mw.nullcore.NullCore.LOG;

public final class Utils {
    public static final @NotNull Minecraft mc = Minecraft.getInstance();
    public static final RandomSource rand = RandomSource.createNewThreadLocalInstance();
    public static final @NotNull Font font = mc.font;
    public static final float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
    public static int clientTick;

    public static final class Block {
        public static void forEachCube(BlockPos center, int radius, Consumer<BlockPos> action) {
            forEachInVolume(center, radius, radius, radius, pos -> true, action);
        }

        public static void forEachSphere(BlockPos center, int radius, Consumer<BlockPos> action) {
            int radiusSq = radius * radius;
            forEachInVolume(center, radius, radius, radius,
                    pos -> {
                        int dx = pos.getX() - center.getX();
                        int dy = pos.getY() - center.getY();
                        int dz = pos.getZ() - center.getZ();
                        return dx * dx + dy * dy + dz * dz <= radiusSq;
                    },
                    action);
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
    }

    public static final class Client {
        public static void setSafeScreen(Screen screen) {
            try {
                if (!mc.isSameThread()) {
                    mc.execute(() -> setSafeScreen(screen));
                    return;
                }
                if (mc.level == null || mc.player == null) return;
                if (!mc.level.isClientSide()) return;
                mc.setScreen(screen);
            } catch (Exception e) {
                LOG.error("Error launching {}, {}", screen.getClass().getName(), e.getMessage());
            }
        }

        public static void sendPacketDispatch(BlockEntity be) {
            if (be.getLevel() instanceof ServerLevel) {
                Packet<?> packet = be.getUpdatePacket();
                if (packet != null) {
                    BlockPos pos = be.getBlockPos();
                    ((ServerChunkCache) be.getLevel().getChunkSource()).chunkMap.getPlayers(new ChunkPos(pos), false).forEach(e -> e.connection.send(packet));
                }
            }
        }

        public static void tickClient() {
            if (!mc.isPaused()){
                clientTick++;
            }
        }

        public static void tickServer(MinecraftServer server){
            for(ServerPlayer player : server.getPlayerList().getPlayers()) TrackerController.tick(player);
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
                    }:
                    new float[]{
                            ((color >> 24) & 0xFF) / 255f,
                            ((color >> 16) & 0xFF) / 255f,
                            ((color >> 8) & 0xFF) / 255f,
                            (color & 0xFF) / 255f
                    };
        }

        public static int pack(float r, float g, float b, float a, boolean asARGB) {
            return asARGB ? ((int)(a * 255) << 24) | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255) : ((int)(r * 255) << 24) | ((int)(g * 255) << 16) | ((int)(b * 255) << 8) | (int)(a * 255);
        }

        public static int lerpColors(float progress, int... colors) {
            float segment = progress * (colors.length - 1);
            int index = (int)segment;
            float factor = segment - index;

            if (index >= colors.length - 1) {
                index = colors.length - 2;
                factor = 1f;
            }

            float[] c1 = unpack(colors[index], false);
            float[] c2 = unpack(colors[index + 1], false);

            return pack(
                    net.minecraft.util.Mth.lerp(factor, c1[0], c2[0]),
                    net.minecraft.util.Mth.lerp(factor, c1[1], c2[1]),
                    net.minecraft.util.Mth.lerp(factor, c1[2], c2[2]),
                    net.minecraft.util.Mth.lerp(factor, c1[3], c2[3]),
                    false
            );
        }

        public static int getCyclingColor(int[] palette, float speed) {
            float time = (clientTick + partialTick) * speed;
            return lerpColors((net.minecraft.util.Mth.sin(time) + 1) / 2, palette);
        }

        public static int getRainbow(float speed) {
            int[] rainbow = new int[36];
            for (int i = 0; i < rainbow.length; i++) {
                rainbow[i] = java.awt.Color.HSBtoRGB(i / 36f, 1f, 1f) & 0xFFFFFF;
            }
            return getCyclingColor(rainbow, speed);
        }
    }

    public static final class Mth {
        public static boolean chance(float chance) {
            return rand.nextFloat() < net.minecraft.util.Mth.clamp(chance, 0, (byte)1);
        }

        public static float normalAngle(float angle) {
            angle %= 360.0F;
            if (angle > 180.0F) angle -= 360.0F;
            if (angle < -180.0F) angle += 360.0F;
            return angle;
        }

        public static String formatRealTime(long gameTime) {
            long totalSeconds = gameTime / 20;
            long hours = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            long seconds = totalSeconds % 60;
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
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

    public static final class Render {
        private static final MultiBufferSource mBuffer = mc.renderBuffers().bufferSource();

        public static void drawTexture(GuiGraphics gg, ResourceLocation texture, int x, int y, int u, int v, int pixelWidth, int pixelHeight, int width, int height, int color) {
            gg.blit(RenderType::guiTextured, texture, x, y, u, v, pixelWidth, pixelHeight, width, height, color);
        }

        public static void drawTexture(GuiGraphics gg, ResourceLocation texture, int x, int y, int u, int v, int pixelWidth, int pixelHeight, int width, int height) {
            drawTexture(gg, texture, x, y, u, v, pixelWidth, pixelHeight, width, height, 0);
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

        public static void renderRays(PoseStack ps, VertexConsumer buffer, int color, float time, float pTick) {
            ps.pushPose();
            RandomSource rand = RandomSource.create(1L);
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

        public static Component addGradientText(String text, float time, int... colors) {
            RandomSource rand = RandomSource.create(1L);
            float offset = -((clientTick + partialTick) * rand.nextFloat() + 0.5f) * time;
            MutableComponent component = Component.empty();
            int length = text.length();
            if (length == 0 || colors.length == 0) return component;
            offset %= 1.0f;
            if (offset < 0) offset += 1.0f;

            for (int i = 0; i < length; i++) {
                float pos = (float)i / length + offset;
                pos %= 1.0f;

                float colorPos = pos * (colors.length - 1);
                int colorIndex = (int)colorPos;
                float lerp = colorPos - colorIndex;

                int color1 = colors[colorIndex % colors.length];
                int color2 = colors[(colorIndex + 1) % colors.length];
                int color = Color.lerpColors(lerp, color1, color2);

                component.append(Component.literal(String.valueOf(text.charAt(i))).withStyle(style -> style.withColor(color)));
            }
            return component;
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

        public static List<ResourceLocation> getStructuresAt(ServerLevel level, BlockPos pos){
            Registry<Structure> structureRegistry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
            return level.structureManager().getAllStructuresAt(pos).keySet().stream().map(structureRegistry::getKey).filter(Objects::nonNull).toList();
        }

        public static boolean isPosInStructure(ServerLevel level, BlockPos pos, ResourceLocation structureId) {
            Registry<Structure> registry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
            Structure structure = registry.getValue(structureId);
            if (structure == null) return false;
            return level.structureManager().getStructureAt(pos, structure).isValid();
        }

        public static List<Entity> getEntities(net.minecraft.world.level.Level level, BlockPos startPos, double radius) {
            return level.getEntities(null, new AABB(startPos.getX() - radius, startPos.getY() - radius, startPos.getZ() - radius, startPos.getX() + radius, startPos.getY() + radius, startPos.getZ() + radius));
        }
    }

    public static final class Json {
        public static Object parseJsonValue(JsonElement element, Object defaultValue) {
            if (defaultValue instanceof Boolean) return element.getAsBoolean();
            if (defaultValue instanceof Integer) return element.getAsInt();
            if (defaultValue instanceof Byte) return element.getAsByte();
            if (defaultValue instanceof Short) return element.getAsShort();
            if (defaultValue instanceof Long) return element.getAsLong();
            if (defaultValue instanceof Float) return element.getAsFloat();
            if (defaultValue instanceof String) return element.getAsString();
            if (defaultValue instanceof Character) return element.getAsCharacter();
            return null;
        }

        public static void addValueToJson(JsonObject json, String key, Object value) {
            if (value instanceof Boolean) {
                json.addProperty(key, (Boolean) value);
            } else if (value instanceof Number) {
                json.addProperty(key, (Number) value);
            } else if (value instanceof String) {
                json.addProperty(key, (String) value);
            } else if (value instanceof Character) {
                json.addProperty(key, (Character) value);
            }
        }
    }
}
