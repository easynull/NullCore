package com.mw.nullcore.core.mixins;

import com.mw.nullcore.data.BiomeRulesManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public final class LevelMixin {
    @Inject(method = "getDayTime", at = @At("HEAD"), cancellable = true)
    private void nc$getTime(CallbackInfoReturnable<Long> cir) {
        Level level = (Level)(Object)this;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        Holder<Biome> biome = level.getBiome(player.blockPosition());
        ResourceLocation id = biome.unwrapKey().map(ResourceKey::location).orElse(null);
        BiomeRulesManager.getRules(id)
                .flatMap(r -> r.getRule("fixed_time", Long.class))
                .ifPresent(cir::setReturnValue);
    }
    @Inject(method = "getRainLevel", at = @At("HEAD"), cancellable = true)
    private void nc$getRain(CallbackInfoReturnable<Float> cir) {
        Level level = (Level)(Object)this;
        if (!(level.isClientSide())) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        Holder<Biome> biome = level.getBiome(player.blockPosition());
        ResourceLocation id = biome.unwrapKey().map(ResourceKey::location).orElse(null);
        BiomeRulesManager.getRules(id)
                .flatMap(r -> r.getRule("rain_force", Float.class))
                .ifPresent(intensity -> cir.setReturnValue(Mth.clamp(intensity, 0f, 1f)));
    }
}
