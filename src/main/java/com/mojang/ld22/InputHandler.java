package com.mojang.ld22;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class InputHandler implements KeyListener {
    public class Key {
        public int[] keys;
        public int presses, absorbs;
        public boolean down, clicked;

        public Key(int... keys) {
            this.keys = keys;
            InputHandler.this.keys.add(this);
        }

        public boolean isPressed(int keycode) {
            for (int key : this.keys) {
                if (key == keycode) {
                    return true;
                }
            }
            return false;
        }

        public void toggle(boolean pressed) {
            if (pressed != this.down) {
                this.down = pressed;
            }
            if (pressed) {
                this.presses++;
            }
        }

        public void tick() {
            if (this.absorbs < this.presses) {
                this.absorbs++;
                this.clicked = true;
            } else {
                this.clicked = false;
            }
        }
    }

    public List<Key> keys = new ArrayList<>();

    public Key up = new Key(KeyEvent.VK_NUMPAD8, KeyEvent.VK_W, KeyEvent.VK_UP);
    public Key down = new Key(KeyEvent.VK_NUMPAD2, KeyEvent.VK_S, KeyEvent.VK_DOWN);
    public Key left = new Key(KeyEvent.VK_NUMPAD4, KeyEvent.VK_A, KeyEvent.VK_LEFT);
    public Key right = new Key(KeyEvent.VK_NUMPAD6, KeyEvent.VK_D, KeyEvent.VK_RIGHT);
    public Key attack = new Key(KeyEvent.VK_SPACE, KeyEvent.VK_CONTROL, KeyEvent.VK_NUMPAD0, KeyEvent.VK_INSERT, KeyEvent.VK_C);
    public Key menu = new Key(KeyEvent.VK_TAB, KeyEvent.VK_ALT, KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_ENTER, KeyEvent.VK_X);

    public Key debug_stair_up = new Key(KeyEvent.VK_F3);
    public Key debug_stair_down = new Key(KeyEvent.VK_F4);
    public Key pause_game = new Key(KeyEvent.VK_ESCAPE);

    public void releaseAll() {
        for (Key key : this.keys) {
            key.down = false;
        }
    }

    public void tick() {
        for (Key key : this.keys) {
            key.tick();
        }
    }

    public InputHandler(Game game) {
        game.gameRenderer.canvas.addKeyListener(this);
    }

    public void keyPressed(KeyEvent ke) {
        toggle(ke, true);
    }

    public void keyReleased(KeyEvent ke) {
        toggle(ke, false);
    }

    private void toggle(KeyEvent ke, boolean pressed) {
        for (Key key : this.keys) {
            if (key.isPressed(ke.getKeyCode())) {
                key.toggle(pressed);
            }
        }
    }

    public void keyTyped(KeyEvent ke) {
    }
}
