package com.mojang.ld22;

import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.gfx.SpriteSheet;
import me.kalmemarq.jgame.logging.LogManager;
import me.kalmemarq.jgame.logging.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.Objects;

public class GameRenderer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Game game;
    protected final Canvas canvas = new Canvas();
    private final BufferedImage image = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_ARGB);
    private final int[] pixels = ((DataBufferInt) this.image.getRaster().getDataBuffer()).getData();
    private Screen screen;
    private Screen lightScreen;

    public GameRenderer(Game game) {
        this.game = game;
    }

    public void init() {
        try {
            SpriteSheet iconsSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/icons.png"))));
            SpriteSheet newIconsSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/new_icons.png"))));
            SpriteSheet icons2Sheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/icons2.png"))));
            this.screen = new Screen(Game.WIDTH, Game.HEIGHT, new SpriteSheet[]{ iconsSheet, icons2Sheet, newIconsSheet });
            this.lightScreen = new Screen(Game.WIDTH, Game.HEIGHT, new SpriteSheet[]{ iconsSheet });
        } catch (IOException e) {
            LOGGER.error("Failed to initialize screens", e);
        }
    }

    public void render() {
        int xScroll = 0;
        int yScroll = 0;

        if (this.game.player != null && this.game.world != null) {
            xScroll = this.game.player.x - this.screen.w / 2;
            yScroll = this.game.player.y - (this.screen.h - 8) / 2;
            if (xScroll < 16) {
                xScroll = 16;
            }
            if (yScroll < 16) {
                yScroll = 16;
            }
            if (xScroll > this.game.world.level.w * 16 - this.screen.w - 16) {
                xScroll = this.game.world.level.w * 16 - this.screen.w - 16;
            }
            if (yScroll > this.game.world.level.h * 16 - this.screen.h - 16) {
                yScroll = this.game.world.level.h * 16 - this.screen.h - 16;
            }
        }

        if (this.game.world != null && this.game.player != null) {
            this.game.world.render(this.screen, this.lightScreen, xScroll, yScroll);
        }

        this.renderGui();

        if (!this.canvas.hasFocus()) {
            this.renderFocusNagger();
        }
    }

    public void renderToCanvas() {
        BufferStrategy bs = this.canvas.getBufferStrategy();
        if (bs == null) {
            this.canvas.createBufferStrategy(3);
            this.canvas.requestFocus();
            return;
        }

        for (int y = 0; y < this.screen.h; y++) {
            for (int x = 0; x < this.screen.w; x++) {
                this.pixels[x + y * Game.WIDTH] = this.screen.pixels[x + y * this.screen.w] | 255 << 24;
            }
        }

        Graphics g = bs.getDrawGraphics();
        g.fillRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());

        int ww = Game.WIDTH * Game.SCALE;
        int hh = Game.HEIGHT * Game.SCALE;
        int xo = (this.canvas.getWidth() - ww) / 2;
        int yo = (this.canvas.getHeight() - hh) / 2;
        g.drawImage(this.image, xo, yo, ww, hh, null);
        g.dispose();
        bs.show();
    }

    private void renderGui() {
        if (this.game.player != null) {
            this.game.font.draw(String.format("XY: %.2f / %.2f", (this.game.player.x / 16f), (this.game.player.y / 16.0f)), this.screen, 1, 1, 0xFFFFFF);

            this.screen.renderColored(0, this.screen.h - 16, 160, 16, 0x000000);

            for (int i = 0; i < 10; i++) {
                if (i < this.game.player.health) {
                    this.screen.renderSprite(i * 8, this.screen.h - 16, 2 * 32, 2, 0xFFFFFF, 0);
                } else {
                    this.screen.renderSprite(i * 8, this.screen.h - 16, 2 * 32 + 2, 2, 0xFFFFFF, 0);
                }

                if (this.game.player.staminaRechargeDelay > 0) {
                    if (this.game.player.staminaRechargeDelay / 4 % 2 == 0) {
                        this.screen.renderSprite(i * 8, this.screen.h - 8, 2 * 32 + 4, 2, 0xFFFFFF, 0);
                    } else {
                        this.screen.renderSprite(i * 8, this.screen.h - 8, 2 * 32 + 3, 2, 0xFFFFFF, 0);
                    }
                } else {
                    if (i < this.game.player.stamina) {
                        this.screen.renderSprite(i * 8, this.screen.h - 8, 2 * 32 + 1, 2, 0xFFFFFF, 0);
                    } else {
                        this.screen.renderSprite(i * 8, this.screen.h - 8, 2 * 32 + 3, 2, 0xFFFFFF, 0);
                    }
                }
            }
            if (this.game.player.activeItem != null) {
                this.game.player.activeItem.renderInventory(this.screen, 10 * 8, this.screen.h - 16);
            }
        }

        if (this.game.menu != null) {
            this.game.menu.render(this.screen);
        }
    }

    private void renderFocusNagger() {
        String msg = "Click to focus!";
        int xx = (Game.WIDTH - msg.length() * 8) / 2;
        int yy = (Game.HEIGHT - 8) / 2;
        int w = msg.length();
        int h = 1;

        this.screen.renderSprite(xx - 8, yy - 8, 3 * 32, 2, 0);
        this.screen.renderSprite(xx + w * 8, yy - 8, 3 * 32, 2, 1);
        this.screen.renderSprite(xx - 8, yy + 8, 3 * 32, 2, 2);
        this.screen.renderSprite(xx + w * 8, yy + 8, 3 * 32, 2, 3);
        for (int x = 0; x < w; x++) {
            this.screen.renderSprite(xx + x * 8, yy - 8, 1 + 3 * 32, 2, 0);
            this.screen.renderSprite(xx + x * 8, yy + 8, 1 + 3 * 32, 2, 2);
        }
        for (int y = 0; y < h; y++) {
            this.screen.renderSprite(xx - 8, yy + y * 8, 2 + 3 * 32, 2, 0);
            this.screen.renderSprite(xx + w * 8, yy + y * 8, 2 + 3 * 32, 2, 1);
        }

        if ((this.game.tickCount / 20) % 2 == 0) {
            this.game.font.drawWithBackground(msg, this.screen, xx, yy, 0x898989);
        } else {
            this.game.font.drawWithBackground(msg, this.screen, xx, yy, 0xFFFFFF);
        }
    }
}
