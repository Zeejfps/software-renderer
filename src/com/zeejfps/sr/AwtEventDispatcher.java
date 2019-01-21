package com.zeejfps.sr;

import com.google.common.eventbus.EventBus;
import com.zeejfps.sr.events.KeyPressedEvent;
import com.zeejfps.sr.events.KeyReleasedEvent;
import com.zeejfps.sr.events.MouseMoveEvent;

import java.awt.*;
import java.awt.event.*;

public class AwtEventDispatcher {

    private final EventBus eventBus;
    private final InputHandler inputHandler;
    private final Component component;

    private Robot robot;

    public AwtEventDispatcher(EventBus eventBus, Component component) {
        this.eventBus = eventBus;
        this.inputHandler = new InputHandler();
        this.component = component;
        component.addKeyListener(inputHandler);
        component.addMouseMotionListener(inputHandler);
        component.addMouseListener(inputHandler);
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private int prevMouseX, prevMouseY;

    private class InputHandler implements KeyListener, MouseMotionListener, MouseListener {

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

        private boolean firstMouse = true;

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
            if (e.getX() < 0) {
                robot.mouseMove(component.getLocationOnScreen().x + component.getWidth(), component.getLocationOnScreen().y + e.getY());
                prevMouseX = component.getWidth()-1;
                prevMouseY = e.getY();
            }
            else if (e.getX() >= component.getWidth()) {
                robot.mouseMove(component.getLocationOnScreen().x, component.getLocationOnScreen().y + e.getY());
                prevMouseY = e.getY();
                prevMouseX = 0;
            }

            if (e.getY() < 0) {
                robot.mouseMove(component.getLocationOnScreen().x + e.getX(), component.getLocationOnScreen().y + component.getHeight());
                prevMouseX = component.getHeight()-1;
                prevMouseY = e.getX();
            }
            else if (e.getY() >= component.getHeight()) {
                robot.mouseMove(component.getLocationOnScreen().x + e.getX(), component.getLocationOnScreen().y);
                prevMouseY = 0;
                prevMouseX = e.getX();
            }

            firstMouse = true;
            //eventBus.post(new MouseMoveEvent(e.getX(), e.getY(), 0, 0));
        }
    }

}
