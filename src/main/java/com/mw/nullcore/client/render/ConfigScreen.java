package com.mw.nullcore.client.render;

import com.mw.nullcore.client.NullConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, click -> this.minecraft.setScreen(this.parent))
                        .bounds(this.width / 2 - 26, this.height - 28, 72, 20).build());
        this.addRenderableWidget(SpriteIconButton.builder(Component.empty(), click -> NullConfig.particleInGui = !NullConfig.particleInGui, true)
                .sprite(ResourceLocation.withDefaultNamespace(NullConfig.particleInGui ? "icon/accessibility" : "icon/language"), 15, 15).width(20).build())
                .setPosition(this.width / 2 - 55, this.height - 450);
    }

    @Override
    public void render(GuiGraphics gg, int p_281550_, int p_282878_, float p_282465_) {
        super.render(gg, p_281550_, p_282878_, p_282465_);
        gg.drawCenteredString(this.font, Component.translatable("nullcore.config"), this.width / 2, 20, 0xFFFFFF);
    }
}
