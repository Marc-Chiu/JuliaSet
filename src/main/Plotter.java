package main;

import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/*
 * This class is responsible for plotting the Julia set and handling zooming in and out
 * It extends JPanel and overrides the paint method to draw the Julia set image
 * It is displayed by the JuliaSetViewer class
 */
public class Plotter extends JPanel {
    private int frameWidth;
    private int frameHeight;
    private JuliaSet juliaSet;
    private int startX, startY, currentX, currentY;
    private boolean dragging = false;
    private Image juliaImage, OriginalJuliaSetImage;

    private double xMin = -1.5;
    private double xMax = 1.5;
    private double yMin = -1.5;
    private double yMax = 1.5;


    public Plotter(int width, int height, JuliaSet juliaSet) {
        this.juliaSet = juliaSet;
        this.frameWidth = width;
        this.frameHeight = height;
        this.juliaImage = generateBufferedImage();
        this.OriginalJuliaSetImage = juliaImage;

        // Add mouse listeners to handle zooming
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
        });
    }

    private Image generateBufferedImage() {
        BufferedImage highResImage = juliaSet.generateJuliaSetImage(frameWidth*4, frameHeight*4, xMin, xMax, yMin, yMax);
        return highResImage.getScaledInstance(frameWidth, frameHeight, Image.SCALE_SMOOTH);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (juliaImage != null) {
            g.drawImage(juliaImage, 0, 0, this);
        }

        if (dragging) {
            createBox(g, startX, startY, currentX, currentY);
        }
    }

    private void handleMousePressed(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
        dragging = true;
    }

    private void handleMouseReleased(MouseEvent e) {
        currentX = e.getX();
        currentY = e.getY();
        dragging = false;
        updateView();
        repaint();
    }

    private void handleMouseDragged(MouseEvent e) {
        currentX = e.getX();
        currentY = e.getY();
        repaint();
    }

    public void createBox(Graphics g, int x1, int y1, int x2, int y2) {
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int width = Math.abs(x2 - x1);
        int height = Math.abs(y2 - y1);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.WHITE); // Border color
        g2d.drawRect(x, y, width, height);
        g2d.dispose();
    }


    // rescale the coordinates
    public void updateView(){
        double xNewMin = (double) Math.min(startX, currentX) / frameWidth * (xMax - xMin) + xMin;
        double xNewMax = (double) Math.max(startX, currentX) / frameWidth * (xMax - xMin) + xMin;
        double yNewMin = (double) Math.min(startY, currentY) / frameHeight* (yMax - yMin) + yMin;
        double yNewMax = (double) Math.max(startY, currentY) / frameHeight* (yMax - yMin) + yMin;

        xMin = xNewMin;
        xMax = xNewMax;
        yMin = yNewMin;
        yMax = yNewMax;


        // Ensure that the zoom is not too extreme (don't want floating point errors)
        System.out.println(xMin + " " + xMax + " " + yMin + " " + yMax);
        if (xMax-xMin < .00000001 || yMax-yMin < .00000001) {
            JOptionPane.showMessageDialog(this, "Reached max zoom", "Warning", JOptionPane.WARNING_MESSAGE);
        }

        BufferedImage highResImage = juliaSet.generateJuliaSetImage(frameWidth*4, frameHeight*4, xMin, xMax, yMin, yMax);
        juliaImage = highResImage.getScaledInstance(frameWidth, frameHeight, Image.SCALE_SMOOTH);
    }

    public void resetZoom(){
        this.juliaImage = OriginalJuliaSetImage;
        this.xMin = -1.5;
        this.xMax = 1.5;
        this.yMin = -1.5;
        this.yMax = 1.5;
    }


    public static void main(String[] args) {
        // Example usage
        JuliaSet juliaSet = new JuliaSet(new ComplexNumber(-0.7, 0.27015), 1000);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.add(new Plotter(800, 800, juliaSet));
        frame.setVisible(true);
        frame.setResizable(false);
    }
}
