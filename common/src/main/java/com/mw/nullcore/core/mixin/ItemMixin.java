package com.mw.nullcore.core.mixin;

import com.mw.nullcore.client.screen.ScreenHave;
import com.mw.nullcore.core.multiblocks.Blueprint;
import com.mw.nullcore.core.multiblocks.RsMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.mw.nullcore.Utils.Client.setSafeScreen;

@Mixin(Item.class)
public final class ItemMixin {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void nc$useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Item item = (Item) ((Object) this);
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        if (!level.isClientSide && player != null) {
            RsMultiblock.multiblocks.values().stream()
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

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void nc$use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Item item = (Item) ((Object) this);
        if(item instanceof ScreenHave s && s.canOpen(level, player, player.getItemInHand(hand))){
            setSafeScreen(s.getScreen(level, player));
            cir.setReturnValue(InteractionResult.SUCCESS);
        } else if (item instanceof MenuProvider m){
            player.openMenu(m);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
