package com.zeejfps.sr;

import com.google.common.eventbus.EventBus;
import com.zeejfps.sr.events.KeyPressedEvent;
import com.zeejfps.sr.events.KeyReleasedEvent;
import com.zeejfps.sr.events.MouseMoveEvent;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class AwtEventDispatcher {

    private final EventBus eventBus;
    private final InputHandler inputHandler;

    public AwtEventDispatcher(EventBus eventBus, Component component) {
        this.eventBus = eventBus;
        this.inputHandler = new InputHandler();
        component.addKeyListener(inputHandler);
        component.addMouseMotionListener(inputHandler);
    }

    private class InputHandler implements KeyListener, MouseMotionListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            eventBus.post(new KeyPressedEvent(e.getKeyCode(), false));
        }

        @Override
        public void keyReleased(KeyEvent e) {
            eventBus.post(new KeyReleasedEvent(e.getKeyCode()));
        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            eventBus.post(new MouseMoveEvent(e.getX(), e.getY()));
        }

    }

}
