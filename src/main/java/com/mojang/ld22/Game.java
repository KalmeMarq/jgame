package com.mojang.ld22;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.gfx.SpriteSheet;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;
import com.mojang.ld22.screen.DeadMenu;
import com.mojang.ld22.screen.LevelTransitionMenu;
import com.mojang.ld22.screen.Menu;
import com.mojang.ld22.screen.TitleMenu;
import com.mojang.ld22.screen.WonMenu;
import me.kalmemarq.jgame.logging.LogManager;

public class Game extends Canvas implements Runnable {
    public static final String NAME = "Minicraft Minus";
    public static final int HEIGHT = 120;
    public static final int WIDTH = 160;
    private static final int SCALE = 3;

    private static Game instance;

    private final BufferedImage image = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_RGB);
    private final int[] pixels = ((DataBufferInt) this.image.getRaster().getDataBuffer()).getData();
    private boolean running = false;
    private Screen screen;
    private Screen lightScreen;
    private final InputHandler input = new InputHandler(this);

    private final int[] colors = new int[256];
    private int tickCount = 0;
    public int gameTime = 0;

    private Level level;
    private Level[] levels;
    private int currentLevel;
    public Player player;

    public Menu menu;
    private int playerDeadTime;
    private int pendingLevelChange;
    private int wonTimer = 0;
    public boolean hasWon = false;

    private final JFrame frame;
    public final Font font;

    public Game(JFrame frame) {
        Game.instance = this;
        this.frame = frame;
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                Game.this.running = false;
            }
        });
        this.font = new Font();
    }

    public static Game getInstance() {
        return Game.instance;
    }

    public void leaveWorld() {
        this.player = null;
        this.levels = null;
        this.level = null;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
        if (menu != null) {
            menu.init(this, this.input);
        }
    }

    public void start() {
        this.running = true;
        new Thread(this).start();
    }

    public void stop() {
        this.running = false;
    }

    public void resetGame() {
        this.playerDeadTime = 0;
        this.wonTimer = 0;
        this.gameTime = 0;
        this.hasWon = false;

        this.levels = new Level[5];
        this.currentLevel = 3;

        this.levels[4] = new Level(128, 128, 1, null);
        this.levels[3] = new Level(128, 128, 0, this.levels[4]);
        this.levels[2] = new Level(128, 128, -1, this.levels[3]);
        this.levels[1] = new Level(128, 128, -2, this.levels[2]);
        this.levels[0] = new Level(128, 128, -3, this.levels[1]);

        this.level = this.levels[this.currentLevel];
        this.player = new Player(this, this.input);
        this.player.findStartPos(this.level);

        this.level.add(this.player);

        for (int i = 0; i < 5; i++) {
            this.levels[i].trySpawn(5000);
        }
    }

    private void init() {
        int pp = 0;
        for (int r = 0;
             r < 6;
             r++) {
            for (int g = 0;
                 g < 6;
                 g++) {
                for (int b = 0;
                     b < 6;
                     b++) {
                    int rr = (r * 255 / 5);
                    int gg = (g * 255 / 5);
                    int bb = (b * 255 / 5);
                    int mid = (rr * 30 + gg * 59 + bb * 11) / 100;

                    int r1 = ((rr + mid) / 2) * 230 / 255 + 10;
                    int g1 = ((gg + mid) / 2) * 230 / 255 + 10;
                    int b1 = ((bb + mid) / 2) * 230 / 255 + 10;
                    this.colors[pp++] = r1 << 16 | g1 << 8 | b1;

                }
            }
        }
        try {
            this.screen = new Screen(Game.WIDTH, Game.HEIGHT, new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/icons.png")))));
            this.lightScreen = new Screen(Game.WIDTH, Game.HEIGHT, new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/icons.png")))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setMenu(new TitleMenu());
    }

    public void run() {
        long lastTime = System.nanoTime();
        double unprocessed = 0;
        double nsPerTick = 1000000000.0 / 60;
        int frames = 0;
        int ticks = 0;
        long lastTimer1 = System.currentTimeMillis();

        init();

        while (this.running) {
            long now = System.nanoTime();
            unprocessed += (now - lastTime) / nsPerTick;
            lastTime = now;
            boolean shouldRender = true;
            while (unprocessed >= 1) {
                ticks++;
                tick();
                unprocessed -= 1;
                shouldRender = true;
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (shouldRender) {
                frames++;
                render();
            }

            if (System.currentTimeMillis() - lastTimer1 > 1000) {
                lastTimer1 += 1000;
                this.frame.setTitle(Game.NAME + " " + ticks + " ticks, " + frames + " fps");
                frames = 0;
                ticks = 0;
            }
        }

        System.out.println("Closing game");
        this.frame.dispose();
    }

    public void tick() {
        this.tickCount++;
        if (!hasFocus()) {
            this.input.releaseAll();
        } else {
            if (this.player != null && !this.player.removed && !this.hasWon) {
                this.gameTime++;
            }

            this.input.tick();
            if (this.menu != null) {
                this.menu.tick();
            } else {
                if (this.player.removed) {
                    this.playerDeadTime++;
                    if (this.playerDeadTime > 60) {
                        setMenu(new DeadMenu());
                    }
                } else {
                    if (this.pendingLevelChange != 0) {
                        setMenu(new LevelTransitionMenu(this.pendingLevelChange));
                        this.pendingLevelChange = 0;
                    }
                }
                if (this.wonTimer > 0) {
                    if (--this.wonTimer == 0) {
                        setMenu(new WonMenu());
                    }
                }
                this.level.tick();
                Tile.tickCount++;
            }
        }
    }

    public void changeLevel(int dir) {
        this.level.remove(this.player);
        this.currentLevel += dir;
        this.level = this.levels[this.currentLevel];
        this.player.x = (this.player.x >> 4) * 16 + 8;
        this.player.y = (this.player.y >> 4) * 16 + 8;
        this.level.add(this.player);
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            requestFocus();
            return;
        }

        int xScroll = 0;
        int yScroll = 0;

        if (this.player != null) {
            xScroll = this.player.x - this.screen.w / 2;
            yScroll = this.player.y - (this.screen.h - 8) / 2;
            if (xScroll < 16) {
                xScroll = 16;
            }
            if (yScroll < 16) {
                yScroll = 16;
            }
            if (xScroll > this.level.w * 16 - this.screen.w - 16) {
                xScroll = this.level.w * 16 - this.screen.w - 16;
            }
            if (yScroll > this.level.h * 16 - this.screen.h - 16) {
                yScroll = this.level.h * 16 - this.screen.h - 16;
            }
        }

        if (this.level != null && this.player != null) {
            if (this.currentLevel > 3) {
                int col = Color.get(20, 20, 121, 121);
                for (int y = 0;
                     y < 14;
                     y++)
                    for (int x = 0;
                         x < 24;
                         x++) {
                        this.screen.render(x * 8 - ((xScroll / 4) & 7), y * 8 - ((yScroll / 4) & 7), 0, col, 0);
                    }
            }

            this.level.renderBackground(this.screen, xScroll, yScroll);
            this.level.renderSprites(this.screen, xScroll, yScroll);

            if (this.currentLevel < 3) {
                this.lightScreen.clear(0);
                this.level.renderLight(this.lightScreen, xScroll, yScroll);
                this.screen.overlay(this.lightScreen, xScroll, yScroll);
            }
        }

        renderGui();

        if (!hasFocus()) {
            renderFocusNagger();
        }

        for (int y = 0;
             y < this.screen.h;
             y++) {
            for (int x = 0;
                 x < this.screen.w;
                 x++) {
                int cc = this.screen.pixels[x + y * this.screen.w];
                if (cc < 255) {
                    this.pixels[x + y * Game.WIDTH] = this.colors[cc];
                }
            }
        }

        Graphics g = bs.getDrawGraphics();
        g.fillRect(0, 0, getWidth(), getHeight());

        int ww = Game.WIDTH * Game.SCALE;
        int hh = Game.HEIGHT * Game.SCALE;
        int xo = (getWidth() - ww) / 2;
        int yo = (getHeight() - hh) / 2;
        g.drawImage(this.image, xo, yo, ww, hh, null);
        g.dispose();
        bs.show();
    }

    private void renderGui() {
        if (this.player != null) {
            for (int y = 0;
                 y < 2;
                 y++) {
                for (int x = 0;
                     x < 20;
                     x++) {
                    this.screen.render(x * 8, this.screen.h - 16 + y * 8, 12 * 32, Color.get(000, 000, 000, 000), 0);
                }
            }

            for (int i = 0; i < 10; i++) {
                if (i < this.player.health) {
                    this.screen.render(i * 8, this.screen.h - 16, 12 * 32, Color.get(000, 200, 500, 533), 0);
                } else {
                    this.screen.render(i * 8, this.screen.h - 16, 12 * 32, Color.get(000, 100, 000, 000), 0);
                }

                if (this.player.staminaRechargeDelay > 0) {
                    if (this.player.staminaRechargeDelay / 4 % 2 == 0) {
                        this.screen.render(i * 8, this.screen.h - 8, 1 + 12 * 32, Color.get(000, 555, 000, 000), 0);
                    } else {
                        this.screen.render(i * 8, this.screen.h - 8, 1 + 12 * 32, Color.get(000, 110, 000, 000), 0);
                    }
                } else {
                    if (i < this.player.stamina) {
                        this.screen.render(i * 8, this.screen.h - 8, 1 + 12 * 32, Color.get(000, 220, 550, 553), 0);
                    } else {
                        this.screen.render(i * 8, this.screen.h - 8, 1 + 12 * 32, Color.get(000, 110, 000, 000), 0);
                    }
                }
            }
            if (this.player.activeItem != null) {
                this.player.activeItem.renderInventory(this.screen, 10 * 8, this.screen.h - 16);
            }
        }

        if (this.menu != null) {
            this.menu.render(this.screen);
        }
    }

    private void renderFocusNagger() {
        String msg = "Click to focus!";
        int xx = (Game.WIDTH - msg.length() * 8) / 2;
        int yy = (Game.HEIGHT - 8) / 2;
        int w = msg.length();
        int h = 1;

        this.screen.render(xx - 8, yy - 8, 13 * 32, Color.get(-1, 1, 5, 445), 0);
        this.screen.render(xx + w * 8, yy - 8, 13 * 32, Color.get(-1, 1, 5, 445), 1);
        this.screen.render(xx - 8, yy + 8, 13 * 32, Color.get(-1, 1, 5, 445), 2);
        this.screen.render(xx + w * 8, yy + 8, 13 * 32, Color.get(-1, 1, 5, 445), 3);
        for (int x = 0;
             x < w;
             x++) {
            this.screen.render(xx + x * 8, yy - 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 0);
            this.screen.render(xx + x * 8, yy + 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 2);
        }
        for (int y = 0;
             y < h;
             y++) {
            this.screen.render(xx - 8, yy + y * 8, 2 + 13 * 32, Color.get(-1, 1, 5, 445), 0);
            this.screen.render(xx + w * 8, yy + y * 8, 2 + 13 * 32, Color.get(-1, 1, 5, 445), 1);
        }

        if ((this.tickCount / 20) % 2 == 0) {
            this.font.draw(msg, this.screen, xx, yy, Color.get(5, 333, 333, 333));
        } else {
            this.font.draw(msg, this.screen, xx, yy, Color.get(5, 555, 555, 555));
        }
    }

    public void scheduleLevelChange(int dir) {
        this.pendingLevelChange = dir;
    }

    public static void main(String[] args) {
        LogManager.addStream(System.out);
        LogManager.enableAnsi();

        JFrame frame = new JFrame(Game.NAME);

        try {
            frame.setIconImages(Arrays.asList(ImageIO.read(Game.class.getResourceAsStream("/icon16.png")), ImageIO.read(Game.class.getResourceAsStream("/icon32.png")), ImageIO.read(Game.class.getResourceAsStream("/icon48.png")), ImageIO.read(Game.class.getResourceAsStream("/icon64.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Game game = new Game(frame);
        game.setMinimumSize(new Dimension(Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE));
        game.setMaximumSize(new Dimension(Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE));
        game.setPreferredSize(new Dimension(Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE));

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(game, BorderLayout.CENTER);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        game.start();
    }

    public void won() {
        this.wonTimer = 60 * 3;
        this.hasWon = true;
    }
}
