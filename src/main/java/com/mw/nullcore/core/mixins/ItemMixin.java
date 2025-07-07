package com.mw.nullcore.core.mixins;

import com.mw.nullcore.core.holders.KeyRegisters;
import com.mw.nullcore.core.multiblocks.Blueprint;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public final class ItemMixin {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void nc$blueprinter(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Item item = (Item) ((Object) this);
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        if (!level.isClientSide && player != null) {
            KeyRegisters.MULTIBLOCK_TYPE.stream()
                    .filter(print -> print.getActivator() == item)
                    .filter(print -> print.canActivate(player, level, item.getDefaultInstance()))
                    .findFirst().ifPresent(print -> {
                        Blueprint.Structure structure = print.getStructure(level, pos);
                        if (structure != null) {
                            print.onBuilt(level, pos, structure);
                            cir.setReturnValue(InteractionResult.SUCCESS);
                        }
                    });
        }
    }
}
