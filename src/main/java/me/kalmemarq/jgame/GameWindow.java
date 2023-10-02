package me.kalmemarq.jgame;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

public class GameWindow {
    private long handle;
    private int x;
    private int y;
    private int width;
    private int height;
    private int windowedWidth;
    private int windowedHeight;
    private int framebufferWidth;
    private int framebufferHeight;
    private String title;
    private boolean focused;
    private boolean vsync;
    private boolean maximized;

    public GameWindow(int width, int height, String title) {
        this.title = title;
        this.width = width;
        this.height = height;
    }

    public boolean init() {
        if (!GLFW.glfwInit()) {
            return false;
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        this.handle = GLFW.glfwCreateWindow(this.width, this.height, this.title, 0L, 0L);

        if (this.handle == 0L) {
            GLFW.glfwTerminate();
            return false;
        }

        GLFW.glfwMakeContextCurrent(this.handle);
        GLFW.glfwSwapInterval(this.vsync ? 1 : 0);
        GL.createCapabilities();
        GLFW.glfwShowWindow(this.handle);

        GLFW.glfwSetWindowPosCallback(this.handle, this::onWindowPos);
        GLFW.glfwSetWindowFocusCallback(this.handle, this::onFocus);
        GLFW.glfwSetWindowIconifyCallback(this.handle, this::onIconify);
        GLFW.glfwSetWindowSizeCallback(this.handle, this::onWindowSize);
        GLFW.glfwSetFramebufferSizeCallback(this.handle, this::onFramebufferSize);
        GLFW.glfwSetWindowMaximizeCallback(this.handle, this::onMaximized);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pW = stack.mallocInt(1);
            IntBuffer pH = stack.mallocInt(1);
            IntBuffer pFW = stack.mallocInt(1);
            IntBuffer pFH = stack.mallocInt(1);
            IntBuffer pX = stack.mallocInt(1);
            IntBuffer pY = stack.mallocInt(1);

            GLFW.glfwGetWindowPos(this.handle, pX, pY);
            GLFW.glfwGetWindowSize(this.handle, pW, pH);
            GLFW.glfwGetFramebufferSize(this.handle, pFW, pFH);
            this.x = pX.get(0);
            this.y = pY.get(0);
            this.width = pW.get(0);
            this.height = pH.get(0);
            this.framebufferWidth = pFW.get(0);
            this.framebufferHeight = pFH.get(0);
        }

        if (this.maximized) {
            GLFW.glfwMaximizeWindow(this.handle);
        }

        return true;
    }

    private void onMaximized(long window, boolean maximized) {
        this.maximized = maximized;
        System.out.println("maximized[" + maximized + "]");
    }

    private void onIconify(long window, boolean iconified) {
        System.out.println("iconify[" + iconified + "]");
    }

    private void onFocus(long window, boolean focused) {
        this.focused = focused;
        System.out.println("focus[" + focused + "]");
    }

    private void onWindowPos(long window, int x, int y) {
        this.x = x;
        this.y = y;
        System.out.println("windowpos[" + x + ";" + y + "]");
    }

    private void onWindowSize(long window, int width, int height) {
        this.width = width;
        this.height = height;
        System.out.println("windowsize[" + width + ";" + height + "]");
    }

    private void onFramebufferSize(long window, int width, int height) {
        this.framebufferWidth = width;
        this.framebufferHeight = height;
        System.out.println("framebuffersize[" + width + ";" + height + "]");
    }

    public void setTitle(String title) {
        this.title = title;
        if (this.handle != 0L) {
            GLFW.glfwSetWindowTitle(this.handle, title);
        }
    }

    public void setVsync(boolean vsync) {
        this.vsync = vsync;
        if (this.handle != 0L) {
            GLFW.glfwSwapInterval(vsync ? 1 : 0);
        }
    }

    public void setFullscreen(boolean fullscreen) {
    }

    public void setMaximized(boolean maximized) {
        if (this.handle != 0L) {
            if (maximized) {
                GLFW.glfwMaximizeWindow(this.handle);
            } else {
                GLFW.glfwRestoreWindow(this.handle);
            }
        } else {
            this.maximized = maximized;
        }
    }

    public void update() {
        GLFW.glfwSwapBuffers(this.handle);
        GLFW.glfwPollEvents();
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(this.handle);
    }

    public void destroy() {
        GLFW.glfwDestroyWindow(this.handle);
        GLFW.glfwTerminate();
    }

    public int getFramebufferWidth() {
        return this.framebufferWidth;
    }

    public int getFramebufferHeight() {
        return this.framebufferHeight;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public long getHandle() {
        return this.handle;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean isFocused() {
        return this.focused;
    }

    public boolean isMaximized() {
        return this.maximized;
    }

    public boolean isVsyncEnabled() {
        return this.vsync;
    }
}
