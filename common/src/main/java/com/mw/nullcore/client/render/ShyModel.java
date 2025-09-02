package com.mw.nullcore.client.render;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import java.util.Arrays;

public class ShyModel {
    private final boolean[] renderSides = new boolean[]{true, true, true, true, true, true, false};
    public final TextureAtlasSprite[] textures = new TextureAtlasSprite[6];
    float minX, minY, minZ;
    float maxX, maxY, maxZ;

    public float sizeX() {
        return maxX - minX;
    }

    public float sizeY() {
        return maxY - minY;
    }

    public float sizeZ() {
        return maxZ - minZ;
    }

    public float getMinX() {
        return minX;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMaxY() {
        return maxY;
    }

    public float getMinZ() {
        return minZ;
    }

    public float getMaxZ() {
        return maxZ;
    }

    public ShyModel setMinX(float x) {
        this.minX = x;
        return this;
    }

    public ShyModel setMaxX(float x) {
        this.maxX = x;
        return this;
    }

    public ShyModel setMinY(float y) {
        this.minY = y;
        return this;
    }

    public ShyModel setMaxY(float y) {
        this.maxY = y;
        return this;
    }

    public ShyModel setMinZ(float z) {
        this.minZ = z;
        return this;
    }

    public ShyModel setMaxZ(float z) {
        this.maxZ = z;
        return this;
    }

    public ShyModel setSideRender(Direction side, boolean value) {
        renderSides[side.ordinal()] = value;
        return this;
    }

    public boolean shouldSideRender(Direction side) {
        return renderSides[side.ordinal()];
    }

    public ShyModel setTexture(TextureAtlasSprite tex) {
        Arrays.fill(textures, tex);
        return this;
    }

    public ShyModel setTexture(TextureAtlasSprite down, TextureAtlasSprite up, TextureAtlasSprite north, TextureAtlasSprite south, TextureAtlasSprite west, TextureAtlasSprite east) {
        textures[0] = down;
        textures[1] = up;
        textures[2] = north;
        textures[3] = south;
        textures[4] = west;
        textures[5] = east;
        return this;
    }
}
