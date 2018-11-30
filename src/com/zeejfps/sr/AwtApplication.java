package com.zeejfps.sr;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class AwtApplication extends Application {

    private static GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getScreenDevices()[0];

    public final Bitmap colorBuffer;

    private final JFrame frame;
    private final Canvas canvas;
    private final BufferedImage img;
    private BufferStrategy bs;

    public final Rasterizer rasterizer;

    public AwtApplication(int width, int height, int resolutionX, int resolutionY) {
        // Create the canvas we are going to draw on
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
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
        this.img = new BufferedImage(resolutionX, resolutionY, BufferedImage.TYPE_INT_RGB);
        this.colorBuffer = Bitmap.attach(img, true);

        rasterizer = new SimpleRasterizer(this.colorBuffer);
    }

    @Override
    public void launch(ApplicationListener listener) {
        frame.setVisible(true);
        super.launch(listener);
    }

    @Override
    public Rasterizer getRasterizer() {
        return rasterizer;
    }

    @Override
    protected void update() {
        Arrays.fill(colorBuffer.pixels, 0x002233);
        super.update();
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

}
