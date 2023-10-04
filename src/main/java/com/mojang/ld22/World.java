package com.mojang.ld22;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.gfx.SpriteSheet;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.screen.DeadMenu;
import com.mojang.ld22.screen.LevelTransitionMenu;
import com.mojang.ld22.screen.WonMenu;

public class World {
    private final Game game;
    protected Level level;
    private Level[] levels;
    private int currentLevel;
    private int playerDeadTime;
    private int pendingLevelChange;
    private int wonTimer = 0;
    public boolean hasWon = false;
    public int gameTime = 0;

    public World(Game game) {
        this.game = game;
    }

    public void render(Screen screen, Screen lightScreen, int xScroll, int yScroll) {
        if (this.currentLevel > 3) {
            for (int y = 0; y < 14; y++)
                for (int x = 0; x < 24; x++) {
                    screen.renderSprite(x * 8 - ((xScroll / 4) & 7), y * 8 - ((yScroll / 4) & 7), SpriteSheet.uvTile(224, 32), 2, 0);
                }
        }

        this.level.renderBackground(screen, xScroll, yScroll);
        this.level.renderSprites(screen, xScroll, yScroll);

        if (this.currentLevel < 3) {
            lightScreen.clear(0);
            this.level.renderLight(lightScreen, xScroll, yScroll);
            screen.overlay(lightScreen, xScroll, yScroll);
        }
    }

    public void tick() {
        if (this.game.player != null && !this.game.player.removed && !this.hasWon) {
            this.gameTime++;
        }

        if (this.game.player != null && this.game.player.removed) {
            this.playerDeadTime++;
            if (this.playerDeadTime > 60) {
                this.game.setMenu(new DeadMenu());
            }
        } else {
            if (this.pendingLevelChange != 0) {
                this.game.setMenu(new LevelTransitionMenu(this.pendingLevelChange));
                this.pendingLevelChange = 0;
            } else {
                if (this.game.input.debug_stair_up.clicked) {
                    this.game.changeLevel(1);
                }
                if (this.game.input.debug_stair_down.clicked) {
                    this.game.changeLevel(-1);
                }
            }
        }
        if (this.wonTimer > 0) {
            if (--this.wonTimer == 0) {
                this.game.setMenu(new WonMenu());
            }
        }
        this.level.tick();
    }

    public void won() {
        this.wonTimer = 60 * 3;
        this.hasWon = true;
    }

    public void scheduleLevelChange(int dir) {
        this.pendingLevelChange = dir;
    }

    public void changeLevel(int dir) {
        this.level.remove(this.game.player);
        this.currentLevel += dir;
        this.level = this.levels[this.currentLevel];
        this.game.player.x = (this.game.player.x >> 4) * 16 + 8;
        this.game.player.y = (this.game.player.y >> 4) * 16 + 8;
        this.level.add(this.game.player);
    }

    public void resetGame() {
        this.playerDeadTime = 0;
        this.wonTimer = 0;
        this.gameTime = 0;
        this.hasWon = false;

        this.levels = new Level[5];
        this.currentLevel = 3;

        this.levels[4] = new Level(256, 256, 1, null);
        this.levels[3] = new Level(256, 256, 0, this.levels[4]);
        this.levels[2] = new Level(256, 256, -1, this.levels[3]);
        this.levels[1] = new Level(256, 256, -2, this.levels[2]);
        this.levels[0] = new Level(256, 256, -3, this.levels[1]);

        this.level = this.levels[this.currentLevel];
        this.game.player = new Player(this.game, this.game.input);
        this.game.player.findStartPos(this.level);

        this.level.add(this.game.player);

        for (int i = 0; i < 5; i++) {
            this.levels[i].trySpawn(5000);
        }
    }
}
