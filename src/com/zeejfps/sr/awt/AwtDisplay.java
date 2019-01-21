package com.zeejfps.sr.awt;

import com.google.common.eventbus.EventBus;
import com.zeejfps.sr.rasterizer.Raster;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.WritableRaster;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

public class AwtDisplay {

    public static GraphicsDevice DEFAULT_GRAPHICS_DEVICE = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getScreenDevices()[0];

    private final JFrame window;
    private BufferedImage frameBuffer;
    private Container drawingComponent;
    private AwtEventDispatcher eventDispatcher;

    private int width;
    private int height;

    private AwtDisplay(JFrame window, Raster raster, AwtEventDispatcher dispatcher) {
        this.window = window;
        this.eventDispatcher = dispatcher;
        this.drawingComponent = window.getContentPane();

        DataBufferInt dataBuffer = new DataBufferInt(raster.getColorBuffer(), raster.getColorBuffer().length);
        DirectColorModel colorModel = new DirectColorModel(32, 0xFF0000, 0xFF00, 0xFF);
        WritableRaster wr = java.awt.image.Raster.createWritableRaster(colorModel.createCompatibleSampleModel(raster.getWidth(), raster.getHeight()), dataBuffer, null);
        this.frameBuffer = new BufferedImage(colorModel, wr, false, new Hashtable<>());
    }

    public void swapBuffers() {
        Graphics g = drawingComponent.getGraphics();
        g.drawImage(frameBuffer, 0, 0, drawingComponent.getWidth(), drawingComponent.getHeight(), null);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public static class Builder {

        private int windowWidth, windowHeight;
        private String title = "Untitled Application";
        private boolean hideCursor = false;
        private boolean fullscreen = false;
        private boolean resizeable = false;
        private boolean containCursor = false;
        private EventBus eventBus;
        private AwtDisplay display;

        public Builder withWindowSize(int windowWidth, int windowHeight) {
            this.windowWidth = windowWidth;
            this.windowHeight = windowHeight;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder hideCursor() {
            this.hideCursor = true;
            return this;
        }

        public Builder containCursor() {
            this.containCursor = true;
            return this;
        }

        public Builder resizeable() {
            this.resizeable = true;
            return this;
        }

        public Builder setFullscreen() {
            this.fullscreen = true;
            return this;
        }

        public AwtDisplay buildAndShow(EventBus eventBus, Raster raster) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {

                        JPanel contentPane = new JPanel();
                        contentPane.setFocusable(true);
                        contentPane.setBackground(Color.BLACK);

                        JFrame window = new JFrame(title);
                        if (fullscreen) {
                            window.setUndecorated(true);
                            DEFAULT_GRAPHICS_DEVICE.setFullScreenWindow(window);
                            windowWidth = DEFAULT_GRAPHICS_DEVICE.getDisplayMode().getWidth();
                            windowHeight = DEFAULT_GRAPHICS_DEVICE.getDisplayMode().getHeight();
                        }
                        contentPane.setPreferredSize(new Dimension(windowWidth, windowHeight));

                        AwtEventDispatcher eventDispatcher = new AwtEventDispatcher(eventBus, contentPane, containCursor);

                        window.setContentPane(contentPane);

                        if (hideCursor){
                            BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                                    cursorImg, new Point(0, 0), "blank cursor");
                            window.getContentPane().setCursor(blankCursor);
                        }

                        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                        window.setResizable(resizeable);
                        window.pack();
                        window.setLocationRelativeTo(null);
                        window.setVisible(true);

                        display = new AwtDisplay(window, raster, eventDispatcher);
                    }
                });
            } catch (InterruptedException | InvocationTargetException e) {
                e.printStackTrace();
                System.exit(1);
            }

            return display;
        }

    }

}
