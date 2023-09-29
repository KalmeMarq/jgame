package com.mojang.ld22.gfx;

public class Font {
    private static final String chars =
        "abcdefghijklmnopqrstuvwxyz      " +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ      " + //
            "0123456789.,!?'\"-+=/\\%()<>:;     ";

    public void draw(String msg, Screen screen, int x, int y, int col) {
        for (int i = 0; i < msg.length(); i++) {
            int ix = Font.chars.indexOf(msg.charAt(i));
            if (ix >= 0) {
                screen.renderSprite(x + i * 8, y, ix + 29 * 32, 1, col, 0);
            }
        }
    }

    public void drawWithBackground(String msg, Screen screen, int x, int y, int col) {
        for (int i = 0; i < msg.length(); i++) {
            int ix = Font.chars.indexOf(msg.charAt(i));
            screen.renderSprite(x + i * 8, y, 3 + 3 * 32, 2, 1);
            if (ix >= 0) {
                screen.renderSprite(x + i * 8, y, ix + 29 * 32, 1, col, 0);
            }
        }
    }

    public void drawCentered(String msg, Screen screen, int x, int y, int col) {
        int width = msg.length() * 8;
        for (int i = 0; i < msg.length(); i++) {
            int ix = Font.chars.indexOf(msg.charAt(i));
            if (ix >= 0) {
                screen.renderSprite(x + i * 8 - width / 2, y, ix + 29 * 32, 1, col, 0);
            }
        }
    }

    public void renderFrame(Screen screen, String title, int x0, int y0, int x1, int y1) {
        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                if (x == x0 && y == y0) {
                    screen.renderSprite(x * 8, y * 8, 3 * 32, 2, 0);
                } else if (x == x1 && y == y0) {
                    screen.renderSprite(x * 8, y * 8, 3 * 32, 2, 1);
                } else if (x == x0 && y == y1) {
                    screen.renderSprite(x * 8, y * 8, 3 * 32, 2, 2);
                } else if (x == x1 && y == y1) {
                    screen.renderSprite(x * 8, y * 8, 3 * 32, 2, 3);
                } else if (y == y0) {
                    screen.renderSprite(x * 8, y * 8, 1 + 3 * 32, 2, 0);
                } else if (y == y1) {
                    screen.renderSprite(x * 8, y * 8, 1 + 3 * 32, 2, 2);
                } else if (x == x0) {
                    screen.renderSprite(x * 8, y * 8, 2 + 3 * 32, 2, 0);
                } else if (x == x1) {
                    screen.renderSprite(x * 8, y * 8, 2 + 3 * 32, 2, 1);
                } else {
                    screen.renderSprite(x * 8, y * 8, 3 + 3 * 32, 2, 1);
                }
            }
        }

        this.drawWithBackground(title, screen, x0 * 8 + 8, y0 * 8, 0xEEEE00);
    }
}
