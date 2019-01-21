package com.zeejfps.sr;

import com.zeejfps.sr.rasterizer.Raster;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public class AwtDisplay {

    private static GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getScreenDevices()[0];

    private JFrame frame;
    private BufferedImage frameBuffer;
    private JPanel drawingComponent;

    private int width;
    private int height;

    public AwtDisplay(Config config, Raster raster) {

        // Create the frame to hold the canvas
        frame = new JFrame();

        drawingComponent = new JPanel();
        drawingComponent.setFocusable(true);
        drawingComponent.setBackground(Color.BLACK);

        if (config.fullscreen) {
            frame.setUndecorated(true);
            device.setFullScreenWindow(frame);
            this.width = device.getDisplayMode().getWidth();
            this.height = device.getDisplayMode().getHeight();
        }
        else {
            this.width = config.windowWidth;
            this.height = config.windowHeight;
        }

        drawingComponent.setPreferredSize(new Dimension(this.width, this.height));

        frame.setContentPane(drawingComponent);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);

        DataBufferInt dataBuffer = new DataBufferInt(raster.getColorBuffer(), raster.getColorBuffer().length);
        DirectColorModel colorModel = new DirectColorModel(32, 0xFF0000, 0xFF00, 0xFF);
        WritableRaster wr = java.awt.image.Raster.createWritableRaster(colorModel.createCompatibleSampleModel(raster.getWidth(), raster.getHeight()), dataBuffer, null);
        this.frameBuffer = new BufferedImage(colorModel, wr, false, new Hashtable<>());
    }

    public Component getComponent() {
        return drawingComponent;
    }

    public void show() {
        frame.setVisible(true);
    }

    public void swapBuffers() {
        Graphics g = drawingComponent.getGraphics();
        g.drawImage(frameBuffer, 0, 0, drawingComponent.getWidth(), drawingComponent.getHeight(), null);
    }

    public BufferedImage getFrameBuffer() {
        return frameBuffer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
