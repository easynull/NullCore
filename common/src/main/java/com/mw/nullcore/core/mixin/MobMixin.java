package com.mw.nullcore.core.mixin;

import com.mw.nullcore.core.managers.BiomeRulesManager;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Mob.class)
public final class MobMixin {
    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    private void nc$isSunBurnTick(CallbackInfoReturnable<Boolean> cir) {
        Mob mob = (Mob) (Object) this;
        Level level = mob.level();
        Holder<Biome> biome = level.getBiome(mob.blockPosition());
        ResourceLocation id = biome.unwrapKey().map(ResourceKey::location).orElse(null);
        Optional<Long> time = BiomeRulesManager.getRules(id)
                .flatMap(r -> r.getRule("fixed_time", Long.class));
        Optional<Float> weather = BiomeRulesManager.getRules(id)
                .flatMap(r -> r.getRule("rain_force", Float.class));

        if (time.isPresent() || weather.isPresent()) {
            boolean isDay = time.map(t -> t % 24000 < 12000).orElseGet(() -> level.getDayTime() % 24000 < 12000);
            boolean isRaining = weather.map(intensity -> intensity > 0.5f).orElseGet(() -> level.isRainingAt(mob.blockPosition()));
            cir.setReturnValue(isDay && !isRaining);
        }
    }
}
