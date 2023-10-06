package me.kalmemarq.jgame;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameWindow implements Destroyable {
    private boolean created;
    private long handle;
    private int x;
    private int y;
    private int windowedX;
    private int windowedY;
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
    private boolean iconified;
    private boolean fullscreen;
    private boolean currentFullscreen;
    @Nullable
    private WindowEventHandler windowEventHandler;
    @Nullable
    private MouseEventHandler mouseEventHandler;
    @Nullable
    private KeyboardEventHandler keyboardEventHandler;

    public GameWindow(int width, int height, @NotNull String title) {
        this.title = Objects.requireNonNull(title);
        this.width = width;
        this.height = height;
    }

    /**
     * Initializes GLFW and creates the game window.
     * <p>
     * If it has already been initialized it does nothing
     * </p>
     * @return true if it was initialized and false if it failed or already has been initialized
     */
    public boolean init() {
        if (this.handle != 0L) {
            return false;
        }

        if (!GLFW.glfwInit()) {
            return false;
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        this.handle = GLFW.glfwCreateWindow(this.width, this.height, this.title, this.fullscreen ? GLFW.glfwGetPrimaryMonitor() : 0L, 0L);

        if (this.handle == 0L) {
            GLFW.glfwTerminate();
            return false;
        }

        GLFW.glfwMakeContextCurrent(this.handle);
        GLFW.glfwSwapInterval(this.vsync ? 1 : 0);
        GL.createCapabilities();

        GLFW.glfwSetWindowPosCallback(this.handle, this::onWindowPosChanged);
        GLFW.glfwSetWindowFocusCallback(this.handle, this::onFocusChanged);
        GLFW.glfwSetWindowIconifyCallback(this.handle, this::onIconify);
        GLFW.glfwSetWindowSizeCallback(this.handle, this::onWindowSizeChanged);
        GLFW.glfwSetFramebufferSizeCallback(this.handle, this::onFramebufferSizeChanged);
        GLFW.glfwSetCursorEnterCallback(this.handle, this::onCursorEnterChanged);
        GLFW.glfwSetWindowMaximizeCallback(this.handle, this::onMaximized);
        GLFW.glfwSetMouseButtonCallback(this.handle, this::onMouseButton);
        GLFW.glfwSetCursorPosCallback(this.handle, this::onCursorPos);
        GLFW.glfwSetKeyCallback(this.handle, (_w, key, scancode, action, mods) -> this.onKey(key, scancode, action, mods));
        GLFW.glfwSetCharCallback(this.handle, this::onCharTyped);
        GLFW.glfwSetDropCallback(this.handle, this::onDrop);

        GLFW.glfwShowWindow(this.handle);

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

        this.created = true;
        return true;
    }

    /**
     * Adds the specified string to the system clipboard
     * @param content the string to add to the clipboard
     */
    public void setClipboardContent(String content) {
        GLFW.glfwSetClipboardString(MemoryUtil.NULL, content);
    }

    /**
     * Returns the contents of the clipboard or null if it's empty or an error occurred.
     * @return the contents of the clipboard or null
     */
    @Nullable
    public String getClipboardContent() {
        return GLFW.glfwGetClipboardString(MemoryUtil.NULL);
    }

    /**
     * Sets the handler used for window events.
     * @param handler the handler for window events
     */
    public void setWindowHandler(WindowEventHandler handler) {
        this.windowEventHandler = handler;
    }

    /**
     * Sets the handler used for mouse events.
     * @param handler the handler for mouse events
     */
    public void setMouseHandler(MouseEventHandler handler) {
        this.mouseEventHandler = handler;
    }

    /**
     * Sets the handler used for keyboard events.
     * @param handler the handler for keyboard events
     */
    public void setKeyboardHandler(KeyboardEventHandler handler) {
        this.keyboardEventHandler = handler;
    }

    public void setTitle(String title) {
        this.title = title;
        if (this.created) {
            GLFW.glfwSetWindowTitle(this.handle, title);
        }
    }

    public void setVsync(boolean vsync) {
        this.vsync = vsync;
        if (this.created) {
            GLFW.glfwSwapInterval(vsync ? 1 : 0);
        }
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public void setMaximized(boolean maximized) {
        if (!this.created) {
            this.maximized = maximized;
        } else {
            if (maximized) {
                GLFW.glfwMaximizeWindow(this.handle);
            } else {
                GLFW.glfwRestoreWindow(this.handle);
            }
        }
    }

    public void update() {
        GLFW.glfwSwapBuffers(this.handle);
        if (this.currentFullscreen != this.fullscreen) {
            this.currentFullscreen = this.fullscreen;

            long monitor = GLFW.glfwGetPrimaryMonitor();
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(monitor);

            if (this.currentFullscreen) {
                this.windowedX = this.x;
                this.windowedY = this.y;
                this.windowedWidth = this.width;
                this.windowedHeight = this.height;
                this.x = 0;
                this.y = 0;
                this.width = vidMode.width();
                this.height = vidMode.height();
                GLFW.glfwSetWindowMonitor(this.handle, monitor, this.x, this.y, this.width, this.height, vidMode.refreshRate());
            } else {
                this.x = this.windowedX;
                this.y = this.windowedY;
                this.width = this.windowedWidth;
                this.height = this.windowedHeight;
                GLFW.glfwSetWindowMonitor(this.handle, 0L, this.x, this.y, this.width, this.height, 0);
            }

            GLFW.glfwSwapBuffers(this.handle);
        }
        GLFW.glfwPollEvents();
    }

    /**
     * Returns true if this window was requested to be closed, otherwise false.
     * @return true if this window was requested to be closed, otherwise false
     */
    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(this.handle);
    }

    @Override
    public void destroy() {
        Callbacks.glfwFreeCallbacks(this.handle);
        GLFW.glfwDestroyWindow(this.handle);
        GL.setCapabilities(null);
        GLFW.glfwTerminate();
    }

    private static void throwGlfwError(int error, long description) {
        throw new RuntimeException("GLFW error (" + error + "): " + MemoryUtil.memUTF8(description));
    }

    private void glfwError(int error, long description) {
        System.out.println("GLFW error (" + error + "): " + MemoryUtil.memUTF8(description));
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

    private void onKey(int key, int scancode, int action, int modifiers) {
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
        this.iconified = iconified;
    }

    private void onFocusChanged(long window, boolean focused) {
        this.focused = focused;
        if (this.windowEventHandler != null) {
            if (focused) {
                this.windowEventHandler.onFocusGained();
            } else {

                this.windowEventHandler.onFocusLost();
            }
        }
    }

    private void onCursorEnterChanged(long window, boolean entered) {
        if (this.windowEventHandler != null) {
            if (entered) {
                this.windowEventHandler.onCursorEnter();
            }
        }
    }

    private void onWindowPosChanged(long window, int x, int y) {
        this.x = x;
        this.y = y;
    }

    private void onWindowSizeChanged(long window, int width, int height) {
        this.width = width;
        this.height = height;
    }

    private void onFramebufferSizeChanged(long window, int width, int height) {
        this.framebufferWidth = width;
        this.framebufferHeight = height;
        if (this.windowEventHandler != null) {
            this.windowEventHandler.onSizeChanged();
        }
    }

    /**
     * Returns the handler for this window.
     * @return the handler of this window
     */
    public long getHandle() {
        return this.handle;
    }

    /**
     * Returns the x-coordinate position of this window.
     * @return the x-coordinate position of this window
     */
    public int getX() {
        return this.x;
    }

    /**
     * Returns the y-coordinate position of this window.
     * @return the y-coordinate position of this window
     */
    public int getY() {
        return this.y;
    }

    /**
     * Returns the width of this window.
     * @return the width of this window
     */

    public int getWidth() {
        return this.width;
    }

    /**
     * Returns the height of this window.
     * @return the height of this window
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Returns the width of this window's content.
     * @return the width of this window's content
     */
    public int getFramebufferWidth() {
        return this.framebufferWidth;
    }

    /**
     * Returns the height of this window's content
     * @return the height of this window's content
     */
    public int getFramebufferHeight() {
        return this.framebufferHeight;
    }

    /**
     * Returns the current window title.
     * @return the current window title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Returns true if the window has focus, otherwise false.
     * @return true if the window has focus, otherwise false
     */
    public boolean isFocused() {
        return this.focused;
    }

    /**
     * Returns true if the window is maximized, otherwise false.
     * @return true if the window is maximized, otherwise false
     */
    public boolean isMaximized() {
        return this.maximized;
    }

    /**
     * Returns true if vertical synchronization is enabled, otherwise false.
     * @return true if vertical synchronization is enabled, otherwise false
     */
    public boolean isVsyncEnabled() {
        return this.vsync;
    }

    /**
     * Returns true if this window is in fullscreen mode, otherwise false.
     * @return true if this window is in fullscreen mode, otherwise false
     */
    public boolean isFullscreen() {
        return this.fullscreen;
    }

    /**
     * Returns true if this window is iconified, otherwise false.
     * @return true if this window is iconified, otherwise false
     */
    public boolean isIconified() {
        return this.iconified;
    }

    public interface WindowEventHandler {
        /**
         * Invoked when the window gains focus.
         */
        default void onFocusLost() {
        }

        /**
         * Invoked when the window loses focus.
         */
        default void onFocusGained() {
        }

        /**
         * Invoked when the window is resized.
         */
        default void onSizeChanged() {
        }

        default void onCursorEnter() {
        }
    }

    public interface MouseEventHandler {
        /**
         * Invoked when a mouse button is pressed or released.
         * @param button the mouse button that was pressed or released
         * @param action GLFW_PRESS or GLFW_RELEASE
         * @param modifiers bit field describing which modifiers keys were held down
         */
        default void onMouseButton(int button, int action, int modifiers) {
        }

        /**
         * Invoked when the cursor is moved.
         * @param x the new cursor x-coordinate
         * @param y the new cursor y-coordinate
         */
        default void onCursorPos(double x, double y) {
        }

        /**
         * Invoked when a scrolling device, such as mouse wheel or touchpad, is used.
         * @param offsetX the scroll offset along the x-axis
         * @param offsetY the scroll offset along the y-axis
         */
        default void onScroll(double offsetX, double offsetY) {
        }

        /**
         * Invoked when one or more dragged paths are dropped on the window.
         * @param paths list of the dropped paths
         */
        default void onDrop(List<Path> paths) {
        }
    }

    public interface KeyboardEventHandler {
        /**
         * Invoked when a key is pressed, repeated or released.
         * @param key the keyboard key that was pressed or released
         * @param scancode the system-specific scancode of the key
         * @param action GLFW_PRESS, GLFW_REPEAT or GLFW_RELEASE
         * @param modifiers bit field describing which modifier keys were held down
         */
        default void onKey(int key, int scancode, int action, int modifiers) {
        }

        /**
         * Invoked when a unicode character is input.
         * @param codepoint the unicode code point of a character
         */
        default void onCharTyped(int codepoint) {
        }
    }
}
