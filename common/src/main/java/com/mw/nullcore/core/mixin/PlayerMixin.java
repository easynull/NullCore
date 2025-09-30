package com.mw.nullcore.core.mixin;

import com.mw.nullcore.NullCore;
import com.mw.nullcore.Utils;
import com.mw.nullcore.core.builders.ArmorMaterialBuilder;
import com.mw.nullcore.core.items.armor.SuitItem;
import com.mw.nullcore.core.managers.LockableManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public final class PlayerMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void nc$onTick(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (self.level().getGameTime() % 40 == 0) {
            if (self.isSilent()) return;
            for (var effect : ArmorMaterialBuilder.effects.entrySet()) {
                if (SuitItem.hasArmorSet(self, effect.getKey())) {
                    for (var shyEffect : effect.getValue()) {
                        if (shyEffect.condition().test(self)) self.addEffect(new MobEffectInstance(shyEffect.effect(), shyEffect.time(), shyEffect.level(), shyEffect.visible(), shyEffect.visible()));
                    }
                }
            }
            for (var id : LockableManager.selfLockable.keySet()) {
                LockableManager.LockableEntry entry = LockableManager.selfLockable.get(id);
                if (entry.effects() == null) return;
                if (Utils.Level.isStructure(self.level(), self.blockPosition(), id) || self.level().getBiome(self.blockPosition()).is(id)) {
                    if (entry.lock() || (entry.required() != null && !Utils.Client.hasAdvancement((ServerPlayer) self, entry.required().advancement()))) {
                        for (var effect : entry.effects()) {
                            self.addEffect(new MobEffectInstance(effect, 45));
                        }
                    }
                }
            }
        }
    }
}
