package com.mw.nullcore.core.builders;

import com.mw.nullcore.client.render.NcRenderType;
import com.mw.nullcore.client.particle.data.ColorParticleData;
import com.mw.nullcore.client.particle.data.GenericParticleData;
import com.mw.nullcore.client.particle.data.SpinParticleData;
import com.mw.nullcore.client.particle.data.SpriteParticleData;
import com.mw.nullcore.client.particle.screen.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public final class ScreenParticleBuilder {
    RandomSource random = RandomSource.create();
    double vx = 0;
    double vy = 0;
    double maxXSpeed = 0;
    double maxYSpeed = 0;
    double maxXDist = 0;
    double maxYDist = 0;
    final ScreenParticleType<?> type;
    final ScreenParticleOptions options;
    final ScreenParticleHolder target;

    public static ScreenParticleBuilder create(ScreenParticleType<?> type, ScreenParticleHolder target) {
        return new ScreenParticleBuilder(type, target);
    }

    private ScreenParticleBuilder(ScreenParticleType<?> type, ScreenParticleHolder target) {
        this.type = type;
        this.options = new ScreenParticleOptions(type);
        this.target = target;
    }

    public ScreenParticleOptions getParticleOptions() {
        return options;
    }

    public ScreenParticleBuilder modifyData(Supplier<GenericParticleData> dataType, Consumer<GenericParticleData> dataConsumer) {
        dataConsumer.accept(dataType.get());
        return this;
    }

    public ScreenParticleBuilder modifyData(Optional<GenericParticleData> dataType, Consumer<GenericParticleData> dataConsumer) {
        dataType.ifPresent(dataConsumer);
        return this;
    }

    public ScreenParticleBuilder modifyData(Function<ScreenParticleBuilder, GenericParticleData> dataType, Consumer<GenericParticleData> dataConsumer) {
        dataConsumer.accept(dataType.apply(this));
        return this;
    }

    public ScreenParticleBuilder modifyDataOptional(Function<ScreenParticleBuilder, Optional<GenericParticleData>> dataType, Consumer<GenericParticleData> dataConsumer) {
        return modifyData(dataType.apply(this), dataConsumer);
    }

    public ScreenParticleBuilder modifyData(Collection<Supplier<GenericParticleData>> dataTypes, Consumer<GenericParticleData> dataConsumer) {
        for (Supplier<GenericParticleData> dataFunction : dataTypes) {
            dataConsumer.accept(dataFunction.get());
        }
        return this;
    }

    public ScreenParticleBuilder setRenderType(NcRenderType renderType) {
        options.renderType = renderType;
        return this;
    }

    public ScreenParticleBuilder randomVelocity(double maxSpeed) {
        return randomVelocity(maxSpeed, maxSpeed);
    }

    public ScreenParticleBuilder randomVelocity(double maxXSpeed, double maxYSpeed) {
        this.maxXSpeed = maxXSpeed;
        this.maxYSpeed = maxYSpeed;
        return this;
    }

    public ScreenParticleBuilder addVelocity(double vx, double vy, double vz) {
        this.vx += vx;
        this.vy += vy;
        return this;
    }

    public ScreenParticleBuilder setVelocity(double vx, double vy, double vz) {
        this.vx = vx;
        this.vy = vy;
        return this;
    }

    public ScreenParticleBuilder randomOffset(double maxDistance) {
        return randomOffset(maxDistance, maxDistance);
    }

    public ScreenParticleBuilder randomOffset(double maxXDist, double maxYDist) {
        this.maxXDist = maxXDist;
        this.maxYDist = maxYDist;
        return this;
    }

    public ScreenParticleBuilder act(Consumer<ScreenParticleBuilder> particleBuilderConsumer) {
        particleBuilderConsumer.accept(this);
        return this;
    }

    public ScreenParticleBuilder addActor(Consumer<GenericScreenParticle> particleActor) {
        options.actor = particleActor;
        return this;
    }

    public ScreenParticleBuilder setSprite(ParticleOptions particle) {
        options.spriteData = new SpriteParticleData();
        return this;
    }

    public ScreenParticleBuilder spawn(double x, double y) {
        double yaw = random.nextFloat() * Math.PI * 2, pitch = random.nextFloat() * Math.PI - Math.PI / 2, xSpeed = random.nextFloat() * maxXSpeed, ySpeed = random.nextFloat() * maxYSpeed;
        this.vx += Math.sin(yaw) * Math.cos(pitch) * xSpeed;
        this.vy += Math.sin(pitch) * ySpeed;
        double yaw2 = random.nextFloat() * Math.PI * 2, pitch2 = random.nextFloat() * Math.PI - Math.PI / 2, xDist = random.nextFloat() * maxXDist, yDist = random.nextFloat() * maxYDist;
        double xPos = Math.sin(yaw2) * Math.cos(pitch2) * xDist;
        double yPos = Math.sin(pitch2) * yDist;
        ScreenParticleHandler.addParticle(target, options, x + xPos, y + yPos, vx, vy);
        return this;
    }

    public ScreenParticleBuilder repeat(double x, double y, int n) {
        for (int i = 0; i < n; i++) spawn(x, y);
        return this;
    }

    public ScreenParticleBuilder spawnOnStack(double xOffset, double yOffset) {
        options.tracksStack = true;
        options.stackTrackXOffset = xOffset;
        options.stackTrackYOffset = yOffset;
        spawn(ScreenParticleHandler.currentItemX + xOffset, ScreenParticleHandler.currentItemY + yOffset);
        return this;
    }

    public ScreenParticleBuilder repeatOnStack(double xOffset, double yOffset, int n) {
        for (int i = 0; i < n; i++) spawnOnStack(xOffset, yOffset);
        return this;
    }

    public ScreenParticleBuilder setColorData(ColorParticleData colorData){
        options.colorData = colorData;
        return this;
    }

    public ScreenParticleBuilder setScaleData(GenericParticleData scaleData){
        options.scaleData = scaleData;
        return this;
    }

    public ScreenParticleBuilder setTransparencyData(GenericParticleData transparencyData){
        options.transparencyData = transparencyData;
        return this;
    }

    public ScreenParticleBuilder setSpinData(SpinParticleData spinData){
        options.spinData = spinData;
        return this;
    }

    public ScreenParticleBuilder setGravity(float gravity){
        options.gravity = gravity;
        return this;
    }

    public ScreenParticleBuilder setGravity(float gravity, float additionalGravity){
        options.gravity = gravity;
        options.additionalGravity = additionalGravity;
        return this;
    }

    public ScreenParticleBuilder setLifetime(int lifetime){
        options.lifetime = lifetime;
        return this;
    }

    public ScreenParticleBuilder setLifetime(int lifetime, int additionalLifetime){
        options.lifetime = lifetime;
        options.additionalLifetime = additionalLifetime;
        return this;
    }
}
