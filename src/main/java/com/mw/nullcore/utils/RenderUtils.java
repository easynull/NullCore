package com.mw.nullcore.utils;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class RenderUtils {
    private final static Minecraft mc = Minecraft.getInstance();
    public final static VertexConsumer vertex = mc.renderBuffers().bufferSource().getBuffer(RenderType.cutout());

    public static void drawTexture(ResourceLocation tex, GuiGraphics gg, int x, int y, int uOffset, int vOffset, int pixelWidth, int pixelHeight, int texWidth, int texHeight) {
        gg.blit(RenderType::guiTextured, tex, x, y, uOffset, vOffset, pixelWidth, pixelHeight, texWidth, texHeight);
    }
}
