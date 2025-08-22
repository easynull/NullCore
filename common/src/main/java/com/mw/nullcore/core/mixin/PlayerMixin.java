package com.mw.nullcore.core.mixin;

import com.mw.nullcore.core.builders.ArmorMaterialBuilder;
import com.mw.nullcore.core.items.armor.SuitItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public final class PlayerMixin {
    @Unique
    private int nc$tick;

    @Inject(method = "tick", at = @At("HEAD"))
    private void nc$onTick(CallbackInfo ci) {
        if (nc$tick % 40 == 0) {
            Player player = (Player) (Object) this;
            if(player.level().isClientSide()) return;
            for (var effect : ArmorMaterialBuilder.effects.entrySet()) {
                if (SuitItem.hasArmorSet(player, effect.getKey())) {
                    for (var shyEffect : effect.getValue()) {
                        if (shyEffect.condition().test(player)) player.addEffect(new MobEffectInstance(shyEffect.effect(), shyEffect.time(), shyEffect.level(), shyEffect.visible(), shyEffect.visible()));
                    }
                }
            }
        }
        nc$tick++;
    }
}
