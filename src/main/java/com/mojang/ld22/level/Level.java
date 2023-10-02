package com.mojang.ld22.level;

import com.mojang.ld22.entity.AirWizard;
import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.Mob;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.entity.Slime;
import com.mojang.ld22.entity.Zombie;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.level.levelgen.LevelGen;
import com.mojang.ld22.level.tile.Tile;
import me.kalmemarq.jgame.bso.BsoList;
import me.kalmemarq.jgame.bso.BsoMap;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Level {
    private final Random random = new Random();

    public int w, h;

    public byte[] tiles;
    public byte[] data;
    public List<Entity>[] entitiesInTiles;

    public int grassColor = 141;
    public int dirtColor = 322;
    public int sandColor = 550;
    public final int depth;
    public int monsterDensity = 8;

    public List<Entity> entities = new ArrayList<>();
    private final Comparator<Entity> spriteSorter = Comparator.comparingInt(e0 -> e0.y);

    public Level(int w, int h, int level, Level parentLevel) {
        this(w, h, level, parentLevel, true);
    }

    @SuppressWarnings("unchecked")
    public Level(int w, int h, int level, Level parentLevel, boolean create) {
        if (level < 0) {
            this.dirtColor = 222;
        }
        this.depth = level;
        this.w = w;
        this.h = h;

        if (!create) {
            return;
        }

        byte[][] maps;

        if (level == 1) {
            this.dirtColor = 444;
        }
        if (level == 0) {
            maps = LevelGen.createAndValidateTopMap(w, h);
        } else if (level < 0) {
            maps = LevelGen.createAndValidateUndergroundMap(w, h, -level);
            this.monsterDensity = 4;
        } else {
            maps = LevelGen.createAndValidateSkyMap(w, h); // Sky level
            this.monsterDensity = 4;
        }

        this.tiles = maps[0];
        this.data = maps[1];

        if (parentLevel != null) {
            for (int y = 0; y < h; y++)
                for (int x = 0; x < w; x++) {
                    if (parentLevel.getTile(x, y) == Tile.stairsDown) {

                        setTile(x, y, Tile.stairsUp, 0);
                        if (level == 0) {
                            setTile(x - 1, y, Tile.hardRock, 0);
                            setTile(x + 1, y, Tile.hardRock, 0);
                            setTile(x, y - 1, Tile.hardRock, 0);
                            setTile(x, y + 1, Tile.hardRock, 0);
                            setTile(x - 1, y - 1, Tile.hardRock, 0);
                            setTile(x - 1, y + 1, Tile.hardRock, 0);
                            setTile(x + 1, y - 1, Tile.hardRock, 0);
                            setTile(x + 1, y + 1, Tile.hardRock, 0);
                        } else {
                            setTile(x - 1, y, Tile.dirt, 0);
                            setTile(x + 1, y, Tile.dirt, 0);
                            setTile(x, y - 1, Tile.dirt, 0);
                            setTile(x, y + 1, Tile.dirt, 0);
                            setTile(x - 1, y - 1, Tile.dirt, 0);
                            setTile(x - 1, y + 1, Tile.dirt, 0);
                            setTile(x + 1, y - 1, Tile.dirt, 0);
                            setTile(x + 1, y + 1, Tile.dirt, 0);
                        }
                    }

                }
        }

        this.entitiesInTiles = new ArrayList[w * h];
        for (int i = 0; i < w * h; i++) {
            this.entitiesInTiles[i] = new ArrayList<>();
        }

        if (level == 1) {
            AirWizard aw = new AirWizard();
            aw.x = w * 8;
            aw.y = h * 8;
            add(aw);
        }
    }

    public void writeData(BsoMap map) {
        map.putInt("width", this.w);
        map.putInt("height", this.h);
        map.putInt("depth", this.depth);
        map.putByteArray("tiles", this.tiles);
        map.putByteArray("data", this.data);
        map.putInt("monsterDensity", this.monsterDensity);
        BsoList entityList = new BsoList();
        for (Entity entity : this.entities) {
            if (entity instanceof Player) continue;
            BsoMap entityData = new BsoMap();
            entityData.putString("className", entity.getClass().getName());
            entity.writeData(entityData);
            entityList.add(entityData);
        }
        map.put("entities", entityList);
    }

    public void loadData(BsoMap map) {
        this.w = map.getInt("width");
        this.h = map.getInt("height");
        this.tiles = map.getByteArray("tiles");
        this.data = map.getByteArray("data");
        this.monsterDensity = map.getInt("monsterDensity");
        BsoList entityList = map.getList("entities");
        for (int i = 0; i < entityList.size(); ++i) {
            BsoMap entityData = entityList.getMap(i);
            try {
                Entity entity = (Entity) Class.forName(entityData.getString("className")).getConstructor(new Class[0]).newInstance();
                entity.level = this;
                entity.loadData(entityData);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void renderBackground(Screen screen, int xScroll, int yScroll) {
        int xo = xScroll >> 4;
        int yo = yScroll >> 4;
        int w = (screen.w + 15) >> 4;
        int h = (screen.h + 15) >> 4;
        screen.setOffset(xScroll, yScroll);
        for (int y = yo; y <= h + yo; y++) {
            for (int x = xo;
                 x <= w + xo;
                 x++) {
                getTile(x, y).render(screen, this, x, y);
            }
        }
        screen.setOffset(0, 0);
    }

    private final List<Entity> rowSprites = new ArrayList<>();

    public Player player;

    public void renderSprites(Screen screen, int xScroll, int yScroll) {
        int xo = xScroll >> 4;
        int yo = yScroll >> 4;
        int w = (screen.w + 15) >> 4;
        int h = (screen.h + 15) >> 4;

        screen.setOffset(xScroll, yScroll);
        for (int y = yo; y <= h + yo; y++) {
            for (int x = xo; x <= w + xo; x++) {
                if (x < 0 || y < 0 || x >= this.w || y >= this.h) {
                    continue;
                }
                this.rowSprites.addAll(this.entitiesInTiles[x + y * this.w]);
            }
            if (!this.rowSprites.isEmpty()) {
                sortAndRender(screen, this.rowSprites);
            }
            this.rowSprites.clear();
        }
        screen.setOffset(0, 0);
    }

    public void renderLight(Screen screen, int xScroll, int yScroll) {
        int xo = xScroll >> 4;
        int yo = yScroll >> 4;
        int w = (screen.w + 15) >> 4;
        int h = (screen.h + 15) >> 4;

        screen.setOffset(xScroll, yScroll);
        int r = 4;
        for (int y = yo - r; y <= h + yo + r; y++) {
            for (int x = xo - r; x <= w + xo + r; x++) {
                if (x < 0 || y < 0 || x >= this.w || y >= this.h) {
                    continue;
                }
                List<Entity> entities = this.entitiesInTiles[x + y * this.w];
                for (Entity e : entities) {
                    // e.render(screen);
                    int lr = e.getLightRadius();
                    if (lr > 0) {
                        screen.renderLight(e.x - 1, e.y - 4, lr * 8);
                    }
                }
                int lr = getTile(x, y).getLightRadius(this, x, y);
                if (lr > 0) {
                    screen.renderLight(x * 16 + 8, y * 16 + 8, lr * 8);
                }
            }
        }
        screen.setOffset(0, 0);
    }

    // private void renderLight(Screen screen, int x, int y, int r) {
    // screen.renderLight(x, y, r);
    // }

    private void sortAndRender(Screen screen, List<Entity> list) {
        list.sort(this.spriteSorter);
        for (Entity entity : list) {
            entity.render(screen);
        }
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= this.w || y >= this.h) {
            return Tile.rock;
        }
        return Tile.tiles[this.tiles[x + y * this.w]];
    }

    public void setTile(int x, int y, Tile t, int dataVal) {
        if (x < 0 || y < 0 || x >= this.w || y >= this.h) {
            return;
        }
        this.tiles[x + y * this.w] = t.id;
        this.data[x + y * this.w] = (byte) dataVal;
    }

    public int getData(int x, int y) {
        if (x < 0 || y < 0 || x >= this.w || y >= this.h) {
            return 0;
        }
        return this.data[x + y * this.w] & 0xff;
    }

    public void setData(int x, int y, int val) {
        if (x < 0 || y < 0 || x >= this.w || y >= this.h) {
            return;
        }
        this.data[x + y * this.w] = (byte) val;
    }

    public void add(Entity entity) {
        if (entity instanceof Player) {
            this.player = (Player) entity;
        }
        entity.removed = false;
        this.entities.add(entity);
        entity.init(this);

        insertEntity(entity.x >> 4, entity.y >> 4, entity);
    }

    public void remove(Entity e) {
        this.entities.remove(e);
        int xto = e.x >> 4;
        int yto = e.y >> 4;
        removeEntity(xto, yto, e);
    }

    private void insertEntity(int x, int y, Entity e) {
        if (x < 0 || y < 0 || x >= this.w || y >= this.h) {
            return;
        }
        this.entitiesInTiles[x + y * this.w].add(e);
    }

    private void removeEntity(int x, int y, Entity e) {
        if (x < 0 || y < 0 || x >= this.w || y >= this.h) {
            return;
        }
        this.entitiesInTiles[x + y * this.w].remove(e);
    }

    public void trySpawn(int count) {
        for (int i = 0; i < count; i++) {
            Mob mob;

            int minLevel = 1;
            int maxLevel = 1;
            if (this.depth < 0) {
                maxLevel = (-this.depth) + 1;
            }
            if (this.depth > 0) {
                minLevel = maxLevel = 4;
            }

            int lvl = this.random.nextInt(maxLevel - minLevel + 1) + minLevel;
            if (this.random.nextInt(2) == 0) {
                mob = new Slime(lvl);
            } else {
                mob = new Zombie(lvl);
            }

            if (mob.findStartPos(this)) {
                this.add(mob);
            }
        }
    }

    public void tick() {
        trySpawn(1);

        for (int i = 0; i < this.w * this.h / 50; i++) {
            int xt = this.random.nextInt(this.w);
            int yt = this.random.nextInt(this.w);
            getTile(xt, yt).tick(this, xt, yt);
        }
        for (int i = 0; i < this.entities.size(); i++) {
            Entity e = this.entities.get(i);
            int xto = e.x >> 4;
            int yto = e.y >> 4;

            e.tick();

            if (e.removed) {
                this.entities.remove(i--);
                removeEntity(xto, yto, e);
            } else {
                int xt = e.x >> 4;
                int yt = e.y >> 4;

                if (xto != xt || yto != yt) {
                    removeEntity(xto, yto, e);
                    insertEntity(xt, yt, e);
                }
            }
        }
    }

    public List<Entity> getEntities(int x0, int y0, int x1, int y1) {
        List<Entity> result = new ArrayList<>();
        int xt0 = (x0 >> 4) - 1;
        int yt0 = (y0 >> 4) - 1;
        int xt1 = (x1 >> 4) + 1;
        int yt1 = (y1 >> 4) + 1;
        for (int y = yt0; y <= yt1; y++) {
            for (int x = xt0; x <= xt1; x++) {
                if (x < 0 || y < 0 || x >= this.w || y >= this.h) {
                    continue;
                }
                List<Entity> entities = this.entitiesInTiles[x + y * this.w];
                for (Entity e : entities) {
                    if (e.intersects(x0, y0, x1, y1)) {
                        result.add(e);
                    }
                }
            }
        }
        return result;
    }
}
