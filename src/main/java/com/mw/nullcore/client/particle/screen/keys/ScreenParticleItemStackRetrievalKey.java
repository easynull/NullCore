package com.mw.nullcore.client.particle.screen.keys;

public record ScreenParticleItemStackRetrievalKey(boolean isHotbarItem, boolean isRenderedAfterItem, int x, int y) {
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ScreenParticleItemStackRetrievalKey(boolean hotbarItem, boolean renderedAfterItem, int x1, int y1))) {
            return false;
        }
        return hotbarItem == isHotbarItem && renderedAfterItem == isRenderedAfterItem && x1 == x && y1 == y;
    }
}