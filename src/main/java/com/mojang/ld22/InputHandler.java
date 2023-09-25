package com.mojang.ld22;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class InputHandler implements KeyListener {
    public class Key {
        public int presses, absorbs;
        public boolean down, clicked;

        public Key() {
            InputHandler.this.keys.add(this);
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

    public List<Key> keys = new ArrayList<Key>();

    public Key up = new Key();
    public Key down = new Key();
    public Key left = new Key();
    public Key right = new Key();
    public Key attack = new Key();
    public Key menu = new Key();

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
        game.addKeyListener(this);
    }

    public void keyPressed(KeyEvent ke) {
        toggle(ke, true);
    }

    public void keyReleased(KeyEvent ke) {
        toggle(ke, false);
    }

    private void toggle(KeyEvent ke, boolean pressed) {
        if (ke.getKeyCode() == KeyEvent.VK_NUMPAD8) {
            this.up.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_NUMPAD2) {
            this.down.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_NUMPAD4) {
            this.left.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_NUMPAD6) {
            this.right.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_W) {
            this.up.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_S) {
            this.down.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_A) {
            this.left.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_D) {
            this.right.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_UP) {
            this.up.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
            this.down.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
            this.left.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.right.toggle(pressed);
        }

        if (ke.getKeyCode() == KeyEvent.VK_TAB) {
            this.menu.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_ALT) {
            this.menu.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_ALT_GRAPH) {
            this.menu.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
            this.attack.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
            this.attack.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_NUMPAD0) {
            this.attack.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_INSERT) {
            this.attack.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
            this.menu.toggle(pressed);
        }

        if (ke.getKeyCode() == KeyEvent.VK_X) {
            this.menu.toggle(pressed);
        }
        if (ke.getKeyCode() == KeyEvent.VK_C) {
            this.attack.toggle(pressed);
        }
    }

    public void keyTyped(KeyEvent ke) {
    }
}
