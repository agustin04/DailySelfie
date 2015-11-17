package com.example.sgarcia.selfieserver.backend.util;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * Created by sgarcia on 11/5/2015.
 */

//From: http://stackoverflow.com/questions/21899824/java-convert-a-greyscale-and-sepia-version-of-an-image-with-bufferedimage
public class ColorAlteration {

    /*public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }
                try {
                    BufferedImage master = ImageIO.read(new File("C:\\hold\\thumbnails\\_cg_836___Tilting_Windmills___by_Serena_Clearwater.png"));
                    BufferedImage gray = toGrayScale(master);
                    BufferedImage sepia = toSepia(master, 80);

                    JPanel panel = new JPanel(new GridBagLayout());
                    panel.add(new JLabel(new ImageIcon(master)));
                    panel.add(new JLabel(new ImageIcon(gray)));
                    panel.add(new JLabel(new ImageIcon(sepia)));

                    JOptionPane.showMessageDialog(null, panel);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }*/

    public static BufferedImage toBufferedImage(byte[] imageInByte){
        InputStream in = new ByteArrayInputStream(imageInByte);
        BufferedImage bImageFromConvert = null;

        try {
            bImageFromConvert = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bImageFromConvert;
    }

    public static byte[] convertToBytes(BufferedImage image) {
        byte[] imageInBytes = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            if (image != null) {
                ImageIO.write(image, "jpg", baos);
                imageInBytes = baos.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageInBytes;
    }

    public static BufferedImage toGrayScale(BufferedImage master) {
        BufferedImage gray = new BufferedImage(master.getWidth(), master.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // Automatic converstion....
        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        op.filter(master, gray);

        return gray;
    }

    public static BufferedImage toSepia(BufferedImage img, int sepiaIntensity) {

        BufferedImage sepia = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        // Play around with this.  20 works well and was recommended
        //   by another developer. 0 produces black/white image
        int sepiaDepth = 20;

        int w = img.getWidth();
        int h = img.getHeight();

        WritableRaster raster = sepia.getRaster();

        // We need 3 integers (for R,G,B color values) per pixel.
        int[] pixels = new int[w * h * 3];
        img.getRaster().getPixels(0, 0, w, h, pixels);

        //  Process 3 ints at a time for each pixel.  Each pixel has 3 RGB
        //    colors in array
        for (int i = 0; i < pixels.length; i += 3) {
            int r = pixels[i];
            int g = pixels[i + 1];
            int b = pixels[i + 2];

            int gry = (r + g + b) / 3;
            r = g = b = gry;
            r = r + (sepiaDepth * 2);
            g = g + sepiaDepth;

            if (r > 255) {
                r = 255;
            }
            if (g > 255) {
                g = 255;
            }
            if (b > 255) {
                b = 255;
            }

            // Darken blue color to increase sepia effect
            b -= sepiaIntensity;

            // normalize if out of bounds
            if (b < 0) {
                b = 0;
            }
            if (b > 255) {
                b = 255;
            }

            pixels[i] = r;
            pixels[i + 1] = g;
            pixels[i + 2] = b;
        }
        raster.setPixels(0, 0, w, h, pixels);

        return sepia;
    }
}
