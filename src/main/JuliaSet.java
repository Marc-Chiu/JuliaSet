package main;

import java.awt.image.BufferedImage;
import java.util.concurrent.*;
import java.awt.Color;


/*
 * A class to represent the Julia set of a complex number c.
 * It also generates the fractal image of the Julia set using multithreading.
 */

public class JuliaSet {
    private ComplexNumber c;
    private int maxIterations;
    private BufferedImage image;

    private final int THREADS = 16;

    public class Worker extends Thread {
        private int width, height, i, j;
        private double xStep, yStep, xMin, xMax, yMin, yMax;
        public static int count = 0;

        public Worker(int width, int height, int i, int j, double xMin, double xMax, double yMin, double yMax) {
            this.width = width;
            this.height = height;
            this.i = i;
            this.j = j;
            this.xMin = xMin;
            this.xMax = xMax;
            this.yMin = yMin;
            this.yMax = yMax;
        }

        @Override
        public void run() {
            //int threadCount = count++;
            xStep = (xMax - xMin) / width;
            yStep = (yMax - yMin) / height;
            int blockHeight = height / THREADS;
            int blockWidth = width / THREADS;

            for (int x = i*blockWidth; x < (i+1)*(blockWidth); x++) {
                double xVal = xMin + x * this.xStep;
                for (int y = j*blockHeight; y < (j+1)*blockHeight; y++) {
                    double yVal = yMin + y * this.yStep;
                    ComplexNumber z = new ComplexNumber(xVal, yVal);
                    int iterations = escapeTime(z);
                    image.setRGB(x, y, getColor(iterations).getRGB());
                }
            }
           // System.out.println("Thread " + threadCount + " finished");
        }

        /* Computes how long it took the point diverge towards infinity */
        private int escapeTime(ComplexNumber z) {
            int iterations = 0;

            while (z.magnitude() <= 2 && iterations < maxIterations) {
                z = z.square().add(c);
                iterations++;
            }

            return iterations;
        }

        /* Assigns a color to a point based on the number of iterations it took to diverge
         * Assigns black to points inside the Julia set
         * Assigns a gradient of colors to points outside the Julia set
         * Normalizes the iteration count to a value between 0 and 1
        */
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
    }

    public JuliaSet(ComplexNumber c, int maxIters) {
        this.c = c;
        this.maxIterations = maxIters;
    }

    public ComplexNumber getC() {
        return c;
    }

    public void setC(ComplexNumber c) {
        this.c = c;
    }

    /*
     * Splits the Image up into chunks to be processed by multiple threads
     */
    public BufferedImage generateJuliaSetImage(int width, int height, double xMin, double xMax, double yMin, double yMax) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        for (int i = 0; i < THREADS; i++) {
            for (int j = 0; j < THREADS; j++) {
                executor.execute(new Worker(width, height, i, j, xMin, xMax, yMin, yMax));
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return image;
    }
}
