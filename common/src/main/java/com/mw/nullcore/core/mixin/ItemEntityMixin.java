package com.mw.nullcore.core.mixin;

import com.mw.nullcore.Utils;
import com.mw.nullcore.core.items.AmbientItem;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public final class ItemEntityMixin {
    @Inject(method = "tick", at = @At("RETURN"))
    private void nc$onTick(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) ((Object) this);
        Utils.Item.instanceOf(self.getItem().getItem(), AmbientItem.class, item -> item.entityTick(self.level(), self.position(), self));
    }
}
