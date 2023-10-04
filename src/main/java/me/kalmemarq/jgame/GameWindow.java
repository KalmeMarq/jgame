package me.kalmemarq.jgame;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GameWindow {
    private long handle;
    private int width;
    private int height;
    private int framebufferWidth;
    private int framebufferHeight;
    private String title;
    private boolean focused;
    private boolean vsync;
    private boolean maximized;
    @Nullable
    private WindowEventHandler windowEventHandler;
    @Nullable
    private MouseEventHandler mouseEventHandler;
    @Nullable
    private KeyboardEventHandler keyboardEventHandler;

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
        GLFW.glfwSetMouseButtonCallback(this.handle, this::onMouseButton);
        GLFW.glfwSetCursorPosCallback(this.handle, this::onCursorPos);
        GLFW.glfwSetKeyCallback(this.handle, this::onKey);
        GLFW.glfwSetCharCallback(this.handle, this::onCharTyped);
        GLFW.glfwSetDropCallback(this.handle, this::onDrop);

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

    public void setClipboardContent(String content) {
        GLFW.glfwSetClipboardString(MemoryUtil.NULL, content);
    }

    @Nullable
    public String getClipboardContent(String content) {
        return GLFW.glfwGetClipboardString(MemoryUtil.NULL);
    }

    public void setWindowHandler(WindowEventHandler handler) {
        this.windowEventHandler = handler;
    }

    public void setMouseHandler(MouseEventHandler handler) {
        this.mouseEventHandler = handler;
    }

    public void setKeyboardHandler(KeyboardEventHandler handler) {
        this.keyboardEventHandler = handler;
    }

    private void onMouseButton(long window, int button, int action, int modifiers) {
        if (this.mouseEventHandler != null) {
            this.mouseEventHandler.onMouseButton(button, action, modifiers);
        }
    }

    private void onCursorPos(long window, double x, double y) {
        if (this.mouseEventHandler != null) {
            this.mouseEventHandler.onCursorPos(x, y);
        }
    }

    private void onScroll(long window, double offsetX, double offsetY) {
        if (this.mouseEventHandler != null) {
            this.mouseEventHandler.onScroll(offsetX, offsetY);
        }
    }

    private void onDrop(long window, int count, long names) {
        if (this.mouseEventHandler == null) return;
        List<Path> paths = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            paths.add(Path.of(GLFWDropCallback.getName(names, i)));
        }
        this.mouseEventHandler.onDrop(paths);
    }

    private void onKey(long window, int key, int scancode, int action, int modifiers) {
        if (this.keyboardEventHandler != null) {
            this.keyboardEventHandler.onKey(key, scancode, action, modifiers);
        }
    }

    private void onCharTyped(long window, int codepoint) {
        if (this.keyboardEventHandler != null) {
            this.keyboardEventHandler.onCharTyped(codepoint);
        }
    }

    public void setSizeLimit(int minWidth, int minHeight, int maxWidth, int maxHeight) {
        GLFW.glfwSetWindowSizeLimits(this.handle, minWidth, minHeight, maxWidth, maxHeight);
    }

    private void onMaximized(long window, boolean maximized) {
        this.maximized = maximized;
    }

    private void onIconify(long window, boolean iconified) {
    }

    private void onFocus(long window, boolean focused) {
        this.focused = focused;
        if (this.windowEventHandler != null) {
            this.windowEventHandler.onFocusChanged();
        }
    }

    private void onWindowPos(long window, int x, int y) {
    }

    private void onWindowSize(long window, int width, int height) {
        this.width = width;
        this.height = height;
    }

    private void onFramebufferSize(long window, int width, int height) {
        this.framebufferWidth = width;
        this.framebufferHeight = height;
        if (this.windowEventHandler != null) {
            this.windowEventHandler.onSizeChanged();
        }
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
        if (this.handle == 0L) {
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

    public interface WindowEventHandler {
        void onFocusChanged();
        void onSizeChanged();
    }

    public interface MouseEventHandler {
        void onMouseButton(int button, int action, int modifiers);
        void onCursorPos(double x, double y);
        void onScroll(double offsetX, double offsetY);
        void onDrop(List<Path> paths);
    }

    public interface KeyboardEventHandler {
        void onKey(int key, int scancode, int action, int modifiers);
        void onCharTyped(int codepoint);
    }
}
