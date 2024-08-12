package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 *A class to represent the Mandlebrot set.
 * The user can use the Madnlebrot set to create a fractal image from the julia set by clicking on a point in the Mandlebrot set.
 */
public class Mandlebrot extends JPanel{
        private final double xMin = -2;
        private final double xMax = 1;
        private final double yMin = -1.3;
        private final double yMax = 1.6;
        private final int maxIterations = 500;
        private Image MandlebrotImage;

        private int FRAME_WIDTH = 800;
        private int FRAME_HEIGHT = 800;


        public Mandlebrot(int width, int height) {
            this.FRAME_WIDTH = width;
            this.FRAME_HEIGHT = height;

            BufferedImage highResImage = generateMandlebrotImage(FRAME_WIDTH*4, FRAME_HEIGHT*4, xMin, xMax, yMin, yMax);
            this.MandlebrotImage = highResImage.getScaledInstance(FRAME_WIDTH, FRAME_HEIGHT, Image.SCALE_SMOOTH);
        }

        public BufferedImage generateMandlebrotImage(int width, int height, double xMin, double xMax, double yMin, double yMax) {
            BufferedImage Image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            double xStep = (xMax - xMin) / width;
            double yStep = (yMax - yMin) / height;

            for (int x = 0; x < width; x++) {
                double xVal = xMin + x * xStep;
                for (int y = 0; y < height; y++) {
                    double yVal = yMin + y * yStep;
                    ComplexNumber z = new ComplexNumber(xVal, yVal);
                    int iterations = escapeTime(z);
                    Image.setRGB(x, y, getColor(iterations).getRGB());
                }
            }
            return Image;
        }

        private int escapeTime(ComplexNumber z) {
                int iterations = 0;
                ComplexNumber z0 = z;
                while (z.magnitude() <= 2 && iterations < maxIterations) {
                    z = z.square().add(z0);
                    iterations++;
                }
                return iterations;
            }

        private Color getColor(int iterations) {
            if (iterations == maxIterations) {
                return new Color(0, 0, 0);
            }

            double t = (double) iterations / maxIterations;

            int red = (int) (9 * (1 - t) * t * t * t * 255);
            int green = (int) (15 * (1 - t) * (1 - t) * t * t * 255);
            int blue = (int) (8.5 * (1 - t) * (1 - t) * (1 - t) * t * 255);

            return new Color(red, green, blue);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            if (MandlebrotImage != null) {
                g.drawImage(MandlebrotImage, 0, 0, this);
            }
        }

        public Image getMandlebrotImage() {
            return MandlebrotImage;
        }


        public static void main(String[] args) {
            // Example usage
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 800);
            frame.add(new Mandlebrot(800, 800));
            frame.setVisible(true);
            frame.setResizable(false);
        }
}
