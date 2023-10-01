package com.mojang.ld22.gfx;

import java.util.Arrays;

public class Screen {
    public int xOffset;
    public int yOffset;

    public static final int BIT_MIRROR_X = 0x01;
    public static final int BIT_MIRROR_Y = 0x02;

    public final int w, h;
    public int[] pixels;

    private final SpriteSheet[] sheets;

    public Screen(int w, int h, SpriteSheet[] sheets) {
        this.sheets = sheets;
        this.w = w;
        this.h = h;
        this.pixels = new int[w * h];
    }

    public void clear(int color) {
        Arrays.fill(this.pixels, color);
    }

    @Deprecated(forRemoval = true)
    public void render(int xp, int yp, int tile, int colors, int bits) {
        this.render(xp, yp, tile, 0, colors, bits);
    }

    @Deprecated(forRemoval = true)
    public void render(int xp, int yp, int tile, int sheet, int colors, int bits) {
        this.render(xp, yp, tile, this.sheets[sheet], colors, bits);
    }

    public void renderColored(int xp, int yp, int width, int height, int tint) {
        xp -= this.xOffset;
        yp -= this.yOffset;

        for (int y = yp; y < yp + height; ++y) {
            if (y < 0 || y >= this.h) continue;

            for (int x = xp; x < xp + width; ++x) {
                if (x < 0 || x >= this.w) continue;

                this.pixels[x + y * this.w] = tint | 0xFF << 24;
            }
        }
    }

    public void renderTextured(int xp, int yp, int width, int height, int tile, int sheetIndex, int whiteTint, int bits) {
        this.renderTextured(xp, yp, width, height, tile, sheetIndex, whiteTint, false, bits);
    }

    public void renderTextured(int xp, int yp, int width, int height, int tile, int sheetIndex, int whiteTint, boolean fullbright, int bits) {
        this.renderTextured(xp, yp, width, height, (tile % 32 * 8), (tile / 32 * 8), sheetIndex, whiteTint, fullbright, bits);
    }

    public void renderTextured(int xp, int yp, int width, int height, int u, int v, int sheetIndex, int whiteTint, int bits) {
        this.renderTextured(xp, yp, width, height, u, v, sheetIndex, whiteTint, false, bits);
    }

    public void renderTextured(int xp, int yp, int width, int height, int u, int v, int sheetIndex, int whiteTint, boolean fullbright, int bits) {
        xp -= this.xOffset;
        yp -= this.yOffset;
        boolean mirrorX = (bits & Screen.BIT_MIRROR_X) > 0;
        boolean mirrorY = (bits & Screen.BIT_MIRROR_Y) > 0;

        u = u % 256;
        v = v % 256;

        SpriteSheet sheet = this.sheets[sheetIndex];
        int toffs = u + v * sheet.width;

        for (int y = 0; y < height; y++) {
            int ys = y;
            if (mirrorY) {
                ys = height - 1 - y;
            }
            if (y + yp < 0 || y + yp >= this.h) {
                continue;
            }
            for (int x = 0; x < width; x++) {
                if (x + xp < 0 || x + xp >= this.w) {
                    continue;
                }

                int xs = x;
                if (mirrorX) {
                    xs = width - 1 - x;
                }
                int col = sheet.pixels[xs + ys * sheet.width + toffs];
                int a = (col >> 24) & 0xFF;
                if (col == 0xFF_FFFFFF) {
                    this.pixels[(x + xp) + (y + yp) * this.w] = whiteTint | 255 << 24;
                } else if (a > 0) {
                    if (fullbright) {
                        this.pixels[(x + xp) + (y + yp) * this.w] = 0xFF_FFFFFF;
                    } else {
                        this.pixels[(x + xp) + (y + yp) * this.w] = col;
                    }
                }
            }
        }
    }

    public void renderSprite(int xp, int yp, int tile, int sheet, int tint, int bits) {
        this.renderSprite(xp, yp, tile, this.sheets[sheet], tint, bits);
    }

    public void renderSprite(int xp, int yp, int tile, int sheet, int bits) {
        this.renderSprite(xp, yp, tile, this.sheets[sheet], -1, bits);
    }

    public void renderSprite(int xp, int yp, int tile, SpriteSheet sheet, int tint, int bits) {
        xp -= this.xOffset;
        yp -= this.yOffset;
        boolean mirrorX = (bits & Screen.BIT_MIRROR_X) > 0;
        boolean mirrorY = (bits & Screen.BIT_MIRROR_Y) > 0;

        int xTile = tile % 32;
        int yTile = tile / 32;
        int toffs = xTile * 8 + yTile * 8 * sheet.width;

        for (int y = 0; y < 8; y++) {
            int ys = y;
            if (mirrorY) {
                ys = 7 - y;
            }
            if (y + yp < 0 || y + yp >= this.h) {
                continue;
            }
            for (int x = 0; x < 8; x++) {
                if (x + xp < 0 || x + xp >= this.w) {
                    continue;
                }

                int xs = x;
                if (mirrorX) {
                    xs = 7 - x;
                }
                int col = sheet.pixels[xs + ys * sheet.width + toffs];
                int a = (col >> 24) & 0xFF;
                if (col == 0xFF_FFFFFF) {
                    this.pixels[(x + xp) + (y + yp) * this.w] = tint | 255 << 24;
                } else if (a > 0) {
                    this.pixels[(x + xp) + (y + yp) * this.w] = col;
                }
            }
        }
    }

    @Deprecated(forRemoval = true)
    public void render(int xp, int yp, int tile, SpriteSheet sheet, int colors, int bits) {
        xp -= this.xOffset;
        yp -= this.yOffset;
        boolean mirrorX = (bits & Screen.BIT_MIRROR_X) > 0;
        boolean mirrorY = (bits & Screen.BIT_MIRROR_Y) > 0;

        int xTile = tile % 32;
        int yTile = tile / 32;
        int toffs = xTile * 8 + yTile * 8 * sheet.width;

        for (int y = 0; y < 8; y++) {
            int ys = y;
            if (mirrorY) {
                ys = 7 - y;
            }
            if (y + yp < 0 || y + yp >= this.h) {
                continue;
            }
            for (int x = 0; x < 8; x++) {
                if (x + xp < 0 || x + xp >= this.w) {
                    continue;
                }

                int xs = x;
                if (mirrorX) {
                    xs = 7 - x;
                }
//                int col = (colors >> (sheet.pixels[xs + ys * sheet.width + toffs] * 8)) & 255;
//                if (col < 255) {
//                    this.pixels[(x + xp) + (y + yp) * this.w] = col;
//                }
                int col = sheet.pixels[xs + ys * sheet.width + toffs];
                this.pixels[(x + xp) + (y + yp) * this.w] = col;
            }
        }
    }

    public void setOffset(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    private final int[] dither = new int[]{0, 8, 2, 10, 12, 4, 14, 6, 3, 11, 1, 9, 15, 7, 13, 5,};

    public void overlay(Screen screen2, int xa, int ya) {
        int[] oPixels = screen2.pixels;
        int i = 0;
        for (int y = 0; y < this.h; y++) {
            for (int x = 0; x < this.w; x++) {
                if (oPixels[i] / 10 <= this.dither[((x + xa) & 3) + ((y + ya) & 3) * 4]) {
                    this.pixels[i] = 0;
                }
                i++;
            }
        }
    }

    public void renderLight(int x, int y, int r) {
        x -= this.xOffset;
        y -= this.yOffset;
        int x0 = x - r;
        int x1 = x + r;
        int y0 = y - r;
        int y1 = y + r;

        if (x0 < 0) {
            x0 = 0;
        }
        if (y0 < 0) {
            y0 = 0;
        }
        if (x1 > this.w) {
            x1 = this.w;
        }
        if (y1 > this.h) {
            y1 = this.h;
        }
        for (int yy = y0; yy < y1; yy++) {
            int yd = yy - y;
            yd = yd * yd;
            for (int xx = x0; xx < x1; xx++) {
                int xd = xx - x;
                int dist = xd * xd + yd;
                if (dist <= r * r) {
                    int br = 255 - dist * 255 / (r * r);
                    if (this.pixels[xx + yy * this.w] < br) {
                        this.pixels[xx + yy * this.w] = br;
                    }
                }
            }
        }
    }
}
