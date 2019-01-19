package com.zeejfps.sr;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

public class AwtDisplay {

    private static GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getScreenDevices()[0];

    private JFrame frame;
    private BufferedImage frameBuffer;
    private JPanel drawingComponent;

    private int width;
    private int height;

    public AwtDisplay(Config config) {

        // Create the frame to hold the canvas
        frame = new JFrame();

        drawingComponent = new JPanel();
        drawingComponent.setFocusable(true);
        drawingComponent.setBackground(Color.BLACK);

        int resolutionX, resolutionY;
        if (config.fullscreen) {
            frame.setUndecorated(true);
            device.setFullScreenWindow(frame);
            this.width = device.getDisplayMode().getWidth();
            this.height = device.getDisplayMode().getHeight();
            resolutionX = (int)(width * config.renderScale + 0.5f);
            resolutionY = (int)(height * config.renderScale + 0.5f);
        }
        else {
            this.width = config.windowWidth;
            this.height = config.windowHeight;
            resolutionX = (int)(config.windowWidth * config.renderScale + 0.5f);
            resolutionY = (int)(config.windowHeight * config.renderScale + 0.5f);
        }
        drawingComponent.setPreferredSize(new Dimension(this.width, this.height));

        frame.setContentPane(drawingComponent);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);

        this.frameBuffer = new BufferedImage(resolutionX, resolutionY, BufferedImage.TYPE_INT_RGB);
        /*pixels = new int[resolutionX * resolutionY + 1];
        DataBufferInt dataBuffer = new DataBufferInt(pixels, pixels.length);
        DirectColorModel colorModel = new DirectColorModel(32, 0xFF0000, 0xFF00, 0xFF);
        WritableRaster raster = Raster.createWritableRaster(colorModel.createCompatibleSampleModel(resolutionX, resolutionY), dataBuffer, null);
        this.frameBuffer = new BufferedImage(colorModel, raster, false, new Hashtable<>());*/
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
