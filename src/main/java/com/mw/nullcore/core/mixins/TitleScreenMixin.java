package com.mw.nullcore.core.mixins;

import com.mw.nullcore.client.render.ConfigScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommonButtons;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TitleScreen.class)
public final class TitleScreenMixin extends Screen {
    TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addConfigButton(CallbackInfo ci) {
        SpriteIconButton button = this.addRenderableWidget(SpriteIconButton.builder(Component.empty(), click -> this.minecraft.setScreen(new ConfigScreen(this)), true)
                .sprite(ResourceLocation.withDefaultNamespace("icon/accessibility"), 15, 15).width(20).build());
        button.setPosition(width / 2 + 104, height / 4 + 103);
    }
}
