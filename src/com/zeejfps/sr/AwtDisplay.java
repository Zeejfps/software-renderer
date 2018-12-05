package com.zeejfps.sr;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class AwtDisplay {

    private static GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getScreenDevices()[0];

    private JFrame frame;
    private Canvas canvas;
    private BufferedImage buffer;
    private BufferStrategy bs;

    private int width;
    private int height;

    public AwtDisplay(Config config) {

        // Create the canvas we are going to draw on
        canvas = new Canvas();
        canvas.setBackground(Color.BLACK);
        canvas.setForeground(Color.BLACK);
        canvas.setFocusable(true);

        // Create the frame to hold the canvas
        frame = new JFrame();

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
        canvas.setPreferredSize(new Dimension(this.width, this.height));
        frame.getContentPane().setBackground(Color.BLACK);
        frame.getContentPane().add(canvas);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);

        this.buffer = new BufferedImage(resolutionX, resolutionY, BufferedImage.TYPE_INT_RGB);
    }

    public void show() {
        frame.setVisible(true);
    }

    public void swapBuffers() {
        if (bs == null) {
            canvas.createBufferStrategy(2);
            bs = canvas.getBufferStrategy();
        }

        Graphics2D g = (Graphics2D)bs.getDrawGraphics();
        g.drawImage(buffer, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
        g.dispose();
        Toolkit.getDefaultToolkit().sync();
        bs.show();
    }

    public BufferedImage getBuffer() {
        return buffer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
