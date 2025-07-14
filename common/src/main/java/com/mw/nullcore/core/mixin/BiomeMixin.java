package com.mw.nullcore.core.mixin;

import com.mw.nullcore.Utils;
import com.mw.nullcore.managers.BiomeRulesManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public abstract class BiomeMixin {
    @Shadow
    public abstract boolean coldEnoughToSnow(BlockPos pos, int seaLevel);

    @Inject(method = "getPrecipitationAt", at = @At("HEAD"), cancellable = true)
    private void nc$getPrecipitation(BlockPos pos, int seaLevel, CallbackInfoReturnable<Biome.Precipitation> cir) {
        Level level = Utils.mc.level;
        if (level == null) return;
        ResourceLocation id = Utils.Level.getBiome(Utils.mc.level, (Biome) ((Object) this));
        BiomeRulesManager.getRules(id).flatMap(r -> r.getRule("has_precipitation", Boolean.class)).ifPresent(has -> cir.setReturnValue(has ? this.coldEnoughToSnow(pos, seaLevel) ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN : Biome.Precipitation.NONE));
    }
}