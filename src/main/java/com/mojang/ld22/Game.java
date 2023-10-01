package com.mojang.ld22;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.gfx.SpriteSheet;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;
import com.mojang.ld22.sound.Sound;
import me.kalmemarq.jgame.logging.Logger;
import me.kalmemarq.jgame.resource.VanillaResourcePack;
import com.mojang.ld22.screen.DeadMenu;
import com.mojang.ld22.screen.LevelTransitionMenu;
import com.mojang.ld22.screen.Menu;
import com.mojang.ld22.screen.TitleMenu;
import com.mojang.ld22.screen.WonMenu;
import me.kalmemarq.jgame.logging.LogManager;
import me.kalmemarq.jgame.argoption.ArgOption;
import me.kalmemarq.jgame.argoption.ArgOptionParser;

public class Game extends Canvas implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String NAME = "Minicraft Minus";
    public static final int HEIGHT = 120;
    public static final int WIDTH = 160;
    private static final int SCALE = 3;

    private static Game instance;

    private final BufferedImage image = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_ARGB);
    private final int[] pixels = ((DataBufferInt) this.image.getRaster().getDataBuffer()).getData();
    private boolean running = false;
    private Screen screen;
    private Screen lightScreen;
    private final InputHandler input = new InputHandler(this);

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
    public final VanillaResourcePack vanillaResourcePack;
    private final Path savePath;
    public final GameSettings settings;

    public Game(JFrame frame, Path savePath) {
        Game.instance = this;
        this.frame = frame;
        this.savePath = savePath;
        this.settings = new GameSettings(this.savePath);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                Game.this.running = false;
            }
        });
        this.font = new Font();
        this.vanillaResourcePack = new VanillaResourcePack();
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
        try {
            SpriteSheet iconsSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/icons.png"))));
            SpriteSheet newIconsSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/new_icons.png"))));
            SpriteSheet icons2Sheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/icons2.png"))));
            this.screen = new Screen(Game.WIDTH, Game.HEIGHT, new SpriteSheet[]{ iconsSheet, icons2Sheet, newIconsSheet });
            this.lightScreen = new Screen(Game.WIDTH, Game.HEIGHT, new SpriteSheet[]{ iconsSheet });
        } catch (IOException e) {
            LOGGER.error("Failed to initialize screens", e);
        }
        this.setMenu(new TitleMenu());
    }

    public void run() {
        long lastTime = System.nanoTime();
        double unprocessed = 0;
        double nsPerTick = 1000000000.0 / 60;
        int frames = 0;
        int ticks = 0;
        long lastTimer1 = System.currentTimeMillis();

        Sound.load(this.vanillaResourcePack);
        this.settings.load();
        Language.load(this.vanillaResourcePack);
        this.init();

        while (this.running) {
            long now = System.nanoTime();
            unprocessed += (now - lastTime) / nsPerTick;
            lastTime = now;
            while (unprocessed >= 1) {
                ticks++;
                this.tick();
                unprocessed -= 1;
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                LOGGER.error("Could not thread sleep", e);
            }

            frames++;
            this.render();

            if (System.currentTimeMillis() - lastTimer1 > 1000) {
                lastTimer1 += 1000;
                this.frame.setTitle(Game.NAME + " " + ticks + " ticks, " + frames + " fps");
                frames = 0;
                ticks = 0;
            }
        }

        System.out.println("Closing game");
        this.settings.save();
        this.frame.dispose();
    }

    public void tick() {
        this.tickCount++;
        Sound.tick();
        if (!this.hasFocus()) {
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
                        this.setMenu(new DeadMenu());
                    }
                } else {
                    if (this.pendingLevelChange != 0) {
                        this.setMenu(new LevelTransitionMenu(this.pendingLevelChange));
                        this.pendingLevelChange = 0;
                    } else {
                        if (this.input.debug_stair_up.clicked) {
                            this.changeLevel(1);
                        }
                        if (this.input.debug_stair_down.clicked) {
                            this.changeLevel(-1);
                        }
                    }
                }
                if (this.wonTimer > 0) {
                    if (--this.wonTimer == 0) {
                        this.setMenu(new WonMenu());
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
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            this.requestFocus();
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
                for (int y = 0; y < 14; y++)
                    for (int x = 0; x < 24; x++) {
                        this.screen.renderSprite(x * 8 - ((xScroll / 4) & 7), y * 8 - ((yScroll / 4) & 7), SpriteSheet.uvTile(224, 32), 2, 0);
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

        this.renderGui();

        if (!this.hasFocus()) {
            this.renderFocusNagger();
        }

        for (int y = 0; y < this.screen.h; y++) {
            for (int x = 0; x < this.screen.w; x++) {
                this.pixels[x + y * Game.WIDTH] = this.screen.pixels[x + y * this.screen.w] | 255 << 24;
            }
        }

        Graphics g = bs.getDrawGraphics();
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        int ww = Game.WIDTH * Game.SCALE;
        int hh = Game.HEIGHT * Game.SCALE;
        int xo = (this.getWidth() - ww) / 2;
        int yo = (this.getHeight() - hh) / 2;
        g.drawImage(this.image, xo, yo, ww, hh, null);
        g.dispose();
        bs.show();
    }

    private void renderGui() {
        if (this.player != null) {
            this.screen.renderColored(0, this.screen.h - 16, 160, 16, 0x000000);

            for (int i = 0; i < 10; i++) {
                if (i < this.player.health) {
                    this.screen.renderSprite(i * 8, this.screen.h - 16, 2 * 32, 2, 0xFFFFFF, 0);
                } else {
                    this.screen.renderSprite(i * 8, this.screen.h - 16, 2 * 32 + 2, 2, 0xFFFFFF, 0);
                }

                if (this.player.staminaRechargeDelay > 0) {
                    if (this.player.staminaRechargeDelay / 4 % 2 == 0) {
                        this.screen.renderSprite(i * 8, this.screen.h - 8, 2 * 32 + 4, 2, 0xFFFFFF, 0);
                    } else {
                        this.screen.renderSprite(i * 8, this.screen.h - 8, 2 * 32 + 3, 2, 0xFFFFFF, 0);
                    }
                } else {
                    if (i < this.player.stamina) {
                        this.screen.renderSprite(i * 8, this.screen.h - 8, 2 * 32 + 1, 2, 0xFFFFFF, 0);
                    } else {
                        this.screen.renderSprite(i * 8, this.screen.h - 8, 2 * 32 + 3, 2, 0xFFFFFF, 0);
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

        if ((this.tickCount / 20) % 2 == 0) {
            this.font.drawWithBackground(msg, this.screen, xx, yy, 0x898989);
        } else {
            this.font.drawWithBackground(msg, this.screen, xx, yy, 0xFFFFFF);
        }
    }

    public void scheduleLevelChange(int dir) {
        this.pendingLevelChange = dir;
    }

    public static void main(String[] args) {
        LogManager.addStream(System.out);
        LogManager.enableAnsi();

        ArgOptionParser optionParser = new ArgOptionParser();
        ArgOption<Path> savePathArg = optionParser.add("savePath", Path.class).defaultsTo(Path.of(".")).creator(Path::of);
        optionParser.parseArgs(args);

        Path savePath = optionParser.getValue(savePathArg).toAbsolutePath();

        JFrame frame = new JFrame(Game.NAME);

        try {
            frame.setIconImages(Arrays.asList(
                ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/icons/icon16.png"))),
                ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/icons/icon32.png"))),
                ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/icons/icon48.png"))),
                ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/icons/icon64.png")))));
        } catch (IOException e) {
            LOGGER.warn("Could not set window frame icon", e);
        }

        Game game = new Game(frame, savePath);
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
