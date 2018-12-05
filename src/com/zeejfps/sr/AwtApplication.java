package com.zeejfps.sr;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public abstract class AwtApplication extends Application {

    private static GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getScreenDevices()[0];

    private JFrame frame;
    private Canvas canvas;
    private BufferedImage img;
    private BufferStrategy bs;

    private Config config;

    public final Bitmap colorBuffer;
    public final Rasterizer rasterizer;

    public AwtApplication() {
        config = new Config();
        this.setup(config);

        // Create the canvas we are going to draw on
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(config.windowWidth, config.windowHeight));
        canvas.setBackground(Color.BLACK);
        canvas.setForeground(Color.BLACK);
        canvas.setFocusable(true);

        // Create the frame to hold the canvas
        frame = new JFrame();
        frame.setUndecorated(true);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.getContentPane().add(canvas);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        device.setFullScreenWindow(frame);

        // Create our colorBuffer bitmap
        int resolutionX = (int)(config.windowWidth * config.renderScale + 0.5f);
        int resolutionY = (int)(config.windowHeight * config.renderScale + 0.5f);
        this.img = new BufferedImage(resolutionX, resolutionY, BufferedImage.TYPE_INT_RGB);
        this.colorBuffer = Bitmap.attach(img, true);

        rasterizer = new SimpleRasterizer(this.colorBuffer);
    }

    @Override
    public void launch() {
        frame.setVisible(true);
        super.launch();
    }

    @Override
    protected void tick() {
        Arrays.fill(colorBuffer.pixels, 0x002233);

        super.tick();

        if (bs == null) {
            canvas.createBufferStrategy(2);
            bs = canvas.getBufferStrategy();
        }

        Graphics2D g = (Graphics2D)bs.getDrawGraphics();
        float aspect = img.getWidth() / (float)img.getHeight();
        int w = (int)(canvas.getHeight() * aspect);
        int h = canvas.getHeight();
        g.drawImage(img, (int)((canvas.getWidth() -w) * 0.5f), 0, w, h, null);
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
        bs.show();
    }

    protected abstract void setup(Config config);

}
