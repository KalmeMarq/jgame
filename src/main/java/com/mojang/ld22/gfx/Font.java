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
                screen.render(x + i * 8, y, ix + 29 * 32, 1, col, 0);
            }
        }
    }

    public void drawCentered(String msg, Screen screen, int x, int y, int col) {
        int width = msg.length() * 8;
        for (int i = 0; i < msg.length(); i++) {
            int ix = Font.chars.indexOf(msg.charAt(i));
            if (ix >= 0) {
                screen.render(x + i * 8 - width / 2, y, ix + 29 * 32, 1, col, 0);
            }
        }
    }

    public void renderFrame(Screen screen, String title, int x0, int y0, int x1, int y1) {
        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                if (x == x0 && y == y0) {
                    screen.render(x * 8, y * 8, 13 * 32, Color.get(-1, 1, 5, 445), 0);
                } else if (x == x1 && y == y0) {
                    screen.render(x * 8, y * 8, 13 * 32, Color.get(-1, 1, 5, 445), 1);
                } else if (x == x0 && y == y1) {
                    screen.render(x * 8, y * 8, 13 * 32, Color.get(-1, 1, 5, 445), 2);
                } else if (x == x1 && y == y1) {
                    screen.render(x * 8, y * 8, 13 * 32, Color.get(-1, 1, 5, 445), 3);
                } else if (y == y0) {
                    screen.render(x * 8, y * 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 0);
                } else if (y == y1) {
                    screen.render(x * 8, y * 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 2);
                } else if (x == x0) {
                    screen.render(x * 8, y * 8, 2 + 13 * 32, Color.get(-1, 1, 5, 445), 0);
                } else if (x == x1) {
                    screen.render(x * 8, y * 8, 2 + 13 * 32, Color.get(-1, 1, 5, 445), 1);
                } else {
                    screen.render(x * 8, y * 8, 2 + 13 * 32, Color.get(5, 5, 5, 5), 1);
                }
            }
        }

        this.draw(title, screen, x0 * 8 + 8, y0 * 8, Color.get(5, 5, 5, 550));
    }
}
