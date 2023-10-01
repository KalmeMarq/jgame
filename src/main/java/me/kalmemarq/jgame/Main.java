package me.kalmemarq.jgame;

import com.mojang.ld22.Game;
import com.mojang.ld22.InputHandler;
import com.mojang.ld22.Language;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.gfx.SpriteSheet;
import com.mojang.ld22.screen.Menu;
import com.mojang.ld22.screen.TitleMenu;
import me.kalmemarq.jgame.resource.VanillaResourcePack;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

public class Main {
    public static final int HEIGHT = 120;
    public static final int WIDTH = 160;
    private static final int SCALE = 3;

    public static void main(String[] args) throws IOException {
        if (!GLFW.glfwInit()) {
            System.out.println("Could not initialized GLFW!");
            return;
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);

        long handle = GLFW.glfwCreateWindow(WIDTH * SCALE, HEIGHT * SCALE, "GLFW Window", 0L, 0L);

        if (handle == 0L) {
            System.out.println("Could not create GLFW window!");
            GLFW.glfwTerminate();
            return;
        }

        GLFW.glfwMakeContextCurrent(handle);
        GL.createCapabilities();
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(handle);

        VanillaResourcePack vanillaResourcePack = new VanillaResourcePack();
        Language.load(vanillaResourcePack);
        Font fontt = new Font();
        OpenGlSpriteSheet iconsSheet = new OpenGlSpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/icons.png"))));
        OpenGlSpriteSheet newIconsSheet = new OpenGlSpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/new_icons.png"))));
        OpenGlSpriteSheet icons2Sheet = new OpenGlSpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/icons2.png"))));

        OpenGlScreen screen = new OpenGlScreen(WIDTH, HEIGHT, new OpenGlSpriteSheet[] { iconsSheet, icons2Sheet, newIconsSheet });

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        Menu menu = new TitleMenu() {
            @Override
            public void init(Game game, InputHandler input) {
                this.input = null;
                this.game = null;
                this.font = fontt;
            }

            @Override
            public void tick() {
            }
        };
        menu.init(null, null);

        long lastFrameTime = milliTime();
        int frameCount = 0;

        while (!GLFW.glfwWindowShouldClose(handle)) {
            GLFW.glfwPollEvents();

            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(0, WIDTH, HEIGHT, 0, 1000.0f, 3000.0f);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0, 0, -2000.0f);

            menu.render(screen);

            GLFW.glfwSwapBuffers(handle);

            ++frameCount;

            if (milliTime() - lastFrameTime > 1000L) {
                lastFrameTime += 1000L;
                GLFW.glfwSetWindowTitle(handle, "GLFW Window " + frameCount + " FPS");
                frameCount = 0;
            }
        }

        screen.destroy();

        GLFW.glfwTerminate();
    }

    private static long milliTime() {
        return System.nanoTime() / 1_000_000L;
    }

    private static class OpenGlScreen extends Screen {
        public OpenGlScreen(int w, int h, OpenGlSpriteSheet[] sheets) {
            super(w, h, sheets);
            this.loadSpriteSheetTextures();
        }

        public void loadSpriteSheetTextures() {
            for (OpenGlSpriteSheet sheet : (OpenGlSpriteSheet[]) this.sheets) {
                sheet.load();
            }
        }

        @Override
        public void clear(int color) {
        }

        @Override
        public void renderTextured(int xp, int yp, int width, int height, int u, int v, int sheetIndex, int whiteTint, boolean fullbright, int bits) {
            OpenGlSpriteSheet sheet = (OpenGlSpriteSheet) this.sheets[sheetIndex];
            sheet.bind();
            float u0 = u / 256.0f;
            float u1 = (u + width) / 256.0f;
            float v0 = v / 256.0f;
            float v1 = (v + height) / 256.0f;

            float r = (whiteTint >> 16 & 0xFF) / 255.0f;
            float g = (whiteTint >> 8 & 0xFF) / 255.0f;
            float b = (whiteTint & 0xFF) / 255.0f;

            GL11.glColor4f(r, g, b, 1.0f);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(u0, v0);
            GL11.glVertex3f(xp, yp, 0);
            GL11.glTexCoord2f(u0, v1);
            GL11.glVertex3f(xp, yp + height, 0);
            GL11.glTexCoord2f(u1, v1);
            GL11.glVertex3f(xp + width, yp + height, 0);
            GL11.glTexCoord2f(u1, v0);
            GL11.glVertex3f(xp + width, yp, 0);
            GL11.glEnd();

            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }

        @Override
        public void renderSprite(int xp, int yp, int tile, int sheetIndex, int tint, int bits) {
            OpenGlSpriteSheet sheet = (OpenGlSpriteSheet) this.sheets[sheetIndex];
            sheet.bind();

            int u = (tile % 32) * 8;
            int v = (tile / 32) * 8;
            float u0 = u / 256.0f;
            float u1 = (u + 8) / 256.0f;
            float v0 = v / 256.0f;
            float v1 = (v + 8) / 256.0f;

            float r = (tint >> 16 & 0xFF) / 255.0f;
            float g = (tint >> 8 & 0xFF) / 255.0f;
            float b = (tint & 0xFF) / 255.0f;

            GL11.glColor4f(r, g, b, 1.0f);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(u0, v0);
            GL11.glVertex3f(xp, yp, 0);
            GL11.glTexCoord2f(u0, v1);
            GL11.glVertex3f(xp, yp + 8, 0);
            GL11.glTexCoord2f(u1, v1);
            GL11.glVertex3f(xp + 8, yp + 8, 0);
            GL11.glTexCoord2f(u1, v0);
            GL11.glVertex3f(xp + 8, yp, 0);
            GL11.glEnd();

            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }

        @Override
        public void renderSprite(int xp, int yp, int tile, int sheet, int bits) {
        }

        public void destroy() {
            for (OpenGlSpriteSheet sheet : (OpenGlSpriteSheet[]) this.sheets) {
                sheet.destroy();
            }
        }
    }

    public static class OpenGlSpriteSheet extends SpriteSheet {
        private int id = -1;

        public OpenGlSpriteSheet(BufferedImage image) {
            super(image);
        }

        public void load() {
            this.bind();
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
            ByteBuffer buffer = ByteBuffer.allocateDirect(this.width * this.height * 4);

            for(int h = 0; h < this.height; h++) {
                for(int w = 0; w < this.width; w++) {
                    int pixel = this.pixels[h * this.width + w];

                    buffer.put((byte) ((pixel >> 16) & 0xFF));
                    buffer.put((byte) ((pixel >> 8) & 0xFF));
                    buffer.put((byte) (pixel & 0xFF));
                    buffer.put((byte) ((pixel >> 24) & 0xFF));
                }
            }

            buffer.flip();

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, this.width, this.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        }

        public void bind() {
            if (this.id == -1) {
                this.id = GL11.glGenTextures();
            }
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.id);
        }

        public void destroy() {
            GL11.glDeleteTextures(this.id);
        }
    }
}
