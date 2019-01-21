package com.zeejfps.sr.awt;

import com.google.common.eventbus.EventBus;
import com.zeejfps.sr.events.KeyPressedEvent;
import com.zeejfps.sr.events.KeyReleasedEvent;
import com.zeejfps.sr.events.MouseMoveEvent;

import java.awt.*;
import java.awt.event.*;

class AwtEventDispatcher {

    private final EventBus eventBus;
    private final Component component;
    private boolean containCursor;

    private Robot robot;

    public AwtEventDispatcher(EventBus eventBus, Component component, boolean containCursor) {
        this.eventBus = eventBus;
        this.component = component;
        this.containCursor = containCursor;

        InputHandler inputHandler = new InputHandler();

        component.addKeyListener(inputHandler);
        component.addMouseMotionListener(inputHandler);
        component.addMouseListener(inputHandler);

        try {
            robot = new Robot(AwtDisplay.DEFAULT_GRAPHICS_DEVICE);
        } catch (AWTException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private class InputHandler implements KeyListener, MouseMotionListener, MouseListener {

        private int prevMouseX, prevMouseY;
        private boolean firstMouse = true;

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
            if (firstMouse) {
                prevMouseX = e.getX();
                prevMouseY = e.getY();
                firstMouse = false;
                return;
            }
            eventBus.post(new MouseMoveEvent(e.getX(), e.getY(), e.getX() - prevMouseX, e.getY() - prevMouseY));
            prevMouseX = e.getX();
            prevMouseY = e.getY();
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (!containCursor)
                return;

            if (e.getX() < 0) {
                robot.mouseMove(component.getLocationOnScreen().x + component.getWidth() - 4, component.getLocationOnScreen().y + e.getY());
                prevMouseX = component.getWidth()-4;
                prevMouseY = e.getY();
            }
            else if (e.getX() >= component.getWidth()) {
                robot.mouseMove(component.getLocationOnScreen().x + 1, component.getLocationOnScreen().y + e.getY());
                prevMouseY = e.getY();
                prevMouseX = 1;
            }

            if (e.getY() < 0) {
                robot.mouseMove(component.getLocationOnScreen().x + e.getX(), component.getLocationOnScreen().y + component.getHeight()-4);
                prevMouseX = component.getHeight()-4;
                prevMouseY = e.getX();
            }
            else if (e.getY() >= component.getHeight()) {
                robot.mouseMove(component.getLocationOnScreen().x + e.getX(), component.getLocationOnScreen().y+1);
                prevMouseY = 1;
                prevMouseX = e.getX();
            }

            firstMouse = true;
            //eventBus.post(new MouseMoveEvent(e.getX(), e.getY(), 0, 0));
        }
    }

}
