package main;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class JuliaSetViewer extends JFrame {
    private JPanel juliaSetVisual;
    private JPanel Mandlebrot;
    private JPanel viewJPanel;

    private JTextField realField;
    private JTextField imaginaryField;

    private static final int FRAME_WIDTH = 1200;
    private static final int FRAME_HEIGHT = 600;

    public JuliaSetViewer() {
        createViewPanel(); // containes the Mandelbrot and Julia set visualizations
        createControlPanel(); // contains the text fields and buttons
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
    }

    public void createViewPanel() {
        viewJPanel = new JPanel();
        viewJPanel.setLayout(new GridLayout(1, 2));

        Mandlebrot = new Mandlebrot(FRAME_WIDTH / 2, FRAME_HEIGHT);
        juliaSetVisual = new Plotter(FRAME_WIDTH / 2, FRAME_HEIGHT, new JuliaSet(new ComplexNumber(-0.7, 0.27015), 1000));

        Mandlebrot.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    handleMandelbrotClick(evt);
                }
            });

        viewJPanel.add(Mandlebrot);
        viewJPanel.add(juliaSetVisual);

        add(viewJPanel, BorderLayout.CENTER); // Add the view panel to the frame
    }

    public void createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(1, 5));

        JLabel realLabel = new JLabel("Real:");
        realField = new JTextField();
        realField.setText("-0.7");
        controlPanel.add(realLabel);
        controlPanel.add(realField);

        JLabel imaginaryLabel = new JLabel("Imaginary:");
        imaginaryField = new JTextField();
        imaginaryField.setText("0.27001");
        controlPanel.add(imaginaryLabel);
        controlPanel.add(imaginaryField);

        JLabel iterationsLabel = new JLabel("Iterations:");
        JTextField iterationsField = new JTextField();
        iterationsField.setText("1000");
        controlPanel.add(iterationsLabel);
        controlPanel.add(iterationsField);

        JButton drawButton = new JButton("Draw");
        controlPanel.add(drawButton);

        JButton resetZoom = new JButton("Reset Zoom");
        controlPanel.add(resetZoom);

        add(controlPanel, BorderLayout.SOUTH);

        drawButton.addActionListener(e -> {
            double real = Double.parseDouble(realField.getText());
            double imaginary = Double.parseDouble(imaginaryField.getText());
            int iterations = Integer.parseInt(iterationsField.getText());
            JuliaSet juliaSet = new JuliaSet(new ComplexNumber(real, imaginary), iterations);
            JPanel newJuliaSetVisual = new Plotter(FRAME_WIDTH / 2, FRAME_HEIGHT, juliaSet);

            // Remove the old panel from viewJPanel
            viewJPanel.remove(juliaSetVisual);

            // Update the reference and add the new panel
            juliaSetVisual = newJuliaSetVisual;
            viewJPanel.add(juliaSetVisual);

            // Revalidate and repaint the viewJPanel to reflect changes
            viewJPanel.revalidate();
            viewJPanel.repaint();
        });

        resetZoom.addActionListener(e -> {
            ((Plotter) juliaSetVisual).resetZoom();
            viewJPanel.revalidate();
            viewJPanel.repaint();
        });
    }

        // Method to handle clicks on the Mandelbrot set
        private void handleMandelbrotClick(java.awt.event.MouseEvent evt) {
                // Convert mouse click to complex plane coordinates
                //System.out.println("Clicked");
                //System.out.println(evt.getX() + " " + evt.getY());
                double real = convertXToReal(evt.getX());
                double imaginary = convertYToImaginary(evt.getY());
                //System.out.println(real + " " + imaginary);

                // Update the text fields
                realField.setText(String.format("%.3f", real));
                imaginaryField.setText(String.format("%.3f", imaginary));
        }

        private double convertXToReal(double x) {
        return ((2 * x / FRAME_WIDTH) * 3 - 2);
        }

        private double convertYToImaginary(double y) {
        return((y / FRAME_HEIGHT) * 2.9 - 1.3);
        }

    public static void main(String[] args) {
        JuliaSetViewer frame = new JuliaSetViewer();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
    }
}
