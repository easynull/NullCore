package com.mw.nullcore.core.mixins;

import com.mw.nullcore.core.level.BiomeManager;
import com.mw.nullcore.utils.WorldUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public final class LevelMixin {
    @Inject(method = "getDayTime", at = @At("HEAD"), cancellable = true)
    private void nc$setTimeBiome(CallbackInfoReturnable<Long> cir) {
        if (((Object)this) instanceof ServerLevel level) {
            level.players().forEach(pl ->{
                Biome biome = level.getBiome(pl.blockPosition()).value();
                ResourceLocation id = WorldUtils.getBiome(level, biome);
                BiomeManager.getBiomeTime(id).stream().boxed().findFirst().ifPresent(cir::setReturnValue);
            });
        }
    }
}
