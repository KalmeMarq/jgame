package com.mojang.ld22.gfx;

import java.awt.image.BufferedImage;

public class SpriteSheet {
    public int width, height;
    public int[] pixels;

    public SpriteSheet(BufferedImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.pixels = new int[this.width * this.height];
        image.getRGB(0, 0, this.width, this.height, this.pixels, 0, this.width);
    }

    public static int uvTile(int u, int v) {
        return xyTile(u / 8, v / 8);
    }

    public static int xyTile(int x, int y) {
        return y * 32 + x;
    }
}
