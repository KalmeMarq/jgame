package com.mojang.ld22;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.level.tile.Tile;
import com.mojang.ld22.screen.PauseMenu;
import com.mojang.ld22.sound.Sound;
import me.kalmemarq.jgame.logging.Logger;
import me.kalmemarq.jgame.resource.VanillaResourcePack;
import com.mojang.ld22.screen.Menu;
import com.mojang.ld22.screen.TitleMenu;
import me.kalmemarq.jgame.logging.LogManager;
import me.kalmemarq.jgame.argoption.ArgOption;
import me.kalmemarq.jgame.argoption.ArgOptionParser;

public class Game implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String NAME = "Minicraft Minus";
    public static final int HEIGHT = 120;
    public static final int WIDTH = 160;
    public static final int SCALE = 3;
    private static Game instance;
    private boolean running = false;

    protected final InputHandler input;
    protected int tickCount = 0;
    public World world;
    public Player player;
    public Menu menu;
    private final JFrame frame;
    public final Font font;
    public final VanillaResourcePack vanillaResourcePack;
    private final Path savePath;
    public final GameSettings settings;
    public GameRenderer gameRenderer;

    public Game(JFrame frame, Path savePath) {
        Game.instance = this;
        this.frame = frame;
        this.savePath = savePath;
        this.settings = new GameSettings(this.savePath);
        this.gameRenderer = new GameRenderer(this);
        this.input = new InputHandler(this);
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
        this.world = null;
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

    public void startGame() {
        this.world = new World(this);
        this.world.resetGame();
    }

    private void init() {
        this.gameRenderer.init();
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
                frames++;
                this.render();
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                LOGGER.error("Could not thread sleep", e);
            }

            this.gameRenderer.renderToCanvas();

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
        if (!this.gameRenderer.canvas.hasFocus()) {
            this.input.releaseAll();
        } else {
            this.input.tick();
            if (this.menu != null) {
                this.menu.tick();
            } else {
                this.world.tick();
                if (this.input.pause_game.clicked) {
                    this.setMenu(new PauseMenu());
                }
                Tile.tickCount++;
            }
        }
    }

    public void changeLevel(int dir) {
        this.world.changeLevel(dir);
    }

    public void render() {
        this.gameRenderer.render();
    }

    public void won() {
        this.world.won();
    }

    public void scheduleLevelChange(int dir) {
        this.world.scheduleLevelChange(dir);
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
        Canvas canvas = game.gameRenderer.canvas;
        canvas.setMinimumSize(new Dimension(Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE));
        canvas.setMaximumSize(new Dimension(Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE));
        canvas.setPreferredSize(new Dimension(Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE));

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        game.start();
    }
}
