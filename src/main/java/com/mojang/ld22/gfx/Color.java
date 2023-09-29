package com.mojang.ld22.gfx;

public class Color {
    public static int rgb(int red, int green, int blue) {
        return 0xFF << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
    }

    public static int rgba(int red, int green, int blue, int alpha) {
        return (alpha & 0xFF) << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
    }

    public static int get(int a, int b, int c, int d) {
        return (Color.get(d) << 24) + (Color.get(c) << 16) + (Color.get(b) << 8) + (Color.get(a));
    }

    public static int get(int d) {
        if (d < 0) {
            return 255;
        }
        int r = d / 100 % 10;
        int g = d / 10 % 10;
        int b = d % 10;
        return r * 36 + g * 6 + b;
    }
}
