package org;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageProcessorGUI extends JFrame implements DslProcessor.ImageOperationHandler { // Implement the interface

    private BufferedImage originalImage;
    private BufferedImage currentImage;
    private JLabel imageDisplayLabel;
    private JScrollPane imageScrollPane;
    private JTextField resizeWidthField;
    private JTextField resizeHeightField;
    private JLabel statusLabel;
    private JTextField dslCommandField;
    private DslProcessor dslProcessor; // Instance of DSL processor

    public ImageProcessorGUI() {
        setTitle("Image Processor");
        setSize(1200, 800); // Increased window size for more content
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize DSL Processor, passing 'this' as the handler
        // Since ImageProcessorGUI now implements DslProcessor.ImageOperationHandler,
        // it can pass itself directly.
        dslProcessor = new DslProcessor(this);

        initComponents();
        setupLayout();
    }

    private void initComponents() {
        imageDisplayLabel = new JLabel();
        imageDisplayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageScrollPane = new JScrollPane(imageDisplayLabel);
        imageScrollPane.getViewport().setBackground(Color.LIGHT_GRAY);

        statusLabel = new JLabel("Ready. Please open an image or enter a DSL command.");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private void setupLayout() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        JPanel topActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topActionsPanel.add(createButton("Open Image (GUI)", e -> openImage()));
        topActionsPanel.add(createButton("Save Image (GUI)", e -> saveImage()));
        topActionsPanel.add(createButton("Reset Image (GUI)", e -> resetImage()));
        contentPane.add(topActionsPanel, BorderLayout.NORTH);

        imageDisplayLabel = new JLabel(); // Re-initialize as it's used in initComponents and setupLayout
        imageDisplayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageScrollPane = new JScrollPane(imageDisplayLabel);
        contentPane.add(imageScrollPane, BorderLayout.CENTER);

        JPanel operationsPanel = new JPanel();
        operationsPanel.setLayout(new BoxLayout(operationsPanel, BoxLayout.Y_AXIS));
        operationsPanel.setBorder(BorderFactory.createTitledBorder("Image Operations"));
        operationsPanel.setPreferredSize(new Dimension(250, getHeight())); // Increased preferred width

        // Manual Operations Section
        operationsPanel.add(new JLabel("Manual Operations:"));
        operationsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        operationsPanel.add(new JLabel("Resize:"));
        JPanel resizeInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resizeWidthField = new JTextField(5);
        resizeHeightField = new JTextField(5);
        resizeInputPanel.add(new JLabel("W:"));
        resizeInputPanel.add(resizeWidthField);
        resizeInputPanel.add(new JLabel("H:"));
        resizeInputPanel.add(resizeHeightField);
        operationsPanel.add(resizeInputPanel);
        JButton resizeButton = createButton("Apply Resize", e -> manualResize());
        operationsPanel.add(resizeButton);
        resizeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        resizeButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, resizeButton.getPreferredSize().height));

        operationsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        JButton grayscaleButton = createButton("Grayscale", e -> manualGrayscale());
        operationsPanel.add(grayscaleButton);
        grayscaleButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        grayscaleButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, grayscaleButton.getPreferredSize().height));

        operationsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        JButton invertButton = createButton("Invert Colors", e -> manualInvertColors());
        operationsPanel.add(invertButton);
        invertButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        invertButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, invertButton.getPreferredSize().height));

        operationsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        JButton rotateButton = createButton("Rotate 90° Clockwise", e -> manualRotate(90));
        operationsPanel.add(rotateButton);
        rotateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        rotateButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, rotateButton.getPreferredSize().height));

        operationsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        JButton flipHButton = createButton("Flip Horizontal", e -> manualFlip("HORIZONTAL"));
        operationsPanel.add(flipHButton);
        flipHButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        flipHButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, flipHButton.getPreferredSize().height));

        operationsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        JButton flipVButton = createButton("Flip Vertical", e -> manualFlip("VERTICAL"));
        operationsPanel.add(flipVButton);
        flipVButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        flipVButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, flipVButton.getPreferredSize().height));

        // DSL Input Section
        operationsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        operationsPanel.add(new JLabel("<html>DSL Commands:</html>"));
        operationsPanel.add(new JLabel("<html><small>LOAD \"path\" AS img1;</small></html>"));
        operationsPanel.add(new JLabel("<html><small>RESIZE img1 WIDTH 100 HEIGHT 200 AS img2;</small></html>"));
        operationsPanel.add(new JLabel("<html><small>GRAYSCALE img2 AS img3;</small></html>"));
        operationsPanel.add(new JLabel("<html><small>ROTATE img3 ANGLE 90 AS img4;</small></html>"));
        operationsPanel.add(new JLabel("<html><small>FLIP img4 HORIZONTAL AS img5;</small></html>"));
        operationsPanel.add(new JLabel("<html><small>SAVE img5 TO \"output.png\" FORMAT \"png\";</small></html>"));
        operationsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        dslCommandField = new JTextField(20);
        operationsPanel.add(dslCommandField);
        JButton executeDslButton = createButton("Execute DSL Script", e -> executeDslCommand());
        operationsPanel.add(executeDslButton);
        executeDslButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        executeDslButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, executeDslButton.getPreferredSize().height));

        operationsPanel.add(Box.createVerticalGlue());

        contentPane.add(operationsPanel, BorderLayout.EAST);

        statusLabel = new JLabel("Ready. Please open an image or enter a DSL command.");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPane.add(statusLabel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }

    // --- Implement DslProcessor.ImageOperationHandler methods ---
    @Override
    public BufferedImage loadImageFromFile(String filePath) {
        try {
            File file = new File(filePath);
            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                throw new IOException("Could not read image. Invalid format or path: " + filePath);
            }
            return image;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image from path: " + filePath + ". Error: " + e.getMessage(), e);
        }
    }

    @Override
    public BufferedImage resizeImage(BufferedImage image, int width, int height) {
        if (image == null) return null;
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Resize dimensions must be positive.");
        }
        BufferedImage resizedImage = new BufferedImage(width, height, image.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    @Override
    public BufferedImage applyGrayscale(BufferedImage image) {
        if (image == null) return null;
        BufferedImage grayscaleImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = grayscaleImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return grayscaleImage;
    }

    @Override
    public BufferedImage applyInvertColors(BufferedImage originalImage) {
        if (originalImage == null) return null;
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        BufferedImage invertedImage = new BufferedImage(width, height, originalImage.getType());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = originalImage.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xFF;
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                red = 255 - red;
                green = 255 - green;
                blue = 255 - blue;

                int invertedRgb = (alpha << 24) | (red << 16) | (green << 8) | blue;
                invertedImage.setRGB(x, y, invertedRgb);
            }
        }
        return invertedImage;
    }

    @Override
    public BufferedImage applyRotation(BufferedImage image, double angleDegrees) {
        if (image == null) return null;
        double angleRadians = Math.toRadians(angleDegrees);

        int newWidth = image.getWidth();
        int newHeight = image.getHeight();

        if (angleDegrees % 180 != 0) {
            newWidth = image.getHeight();
            newHeight = image.getWidth();
        }

        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, image.getType());
        Graphics2D g2d = rotatedImage.createGraphics();

        g2d.translate((double)newWidth / 2, (double)newHeight / 2);
        g2d.rotate(angleRadians);
        g2d.translate((double)-image.getWidth() / 2, (double)-image.getHeight() / 2);

        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return rotatedImage;
    }

    @Override
    public BufferedImage applyFlip(BufferedImage image, String direction) {
        if (image == null) return null;
        BufferedImage flippedImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g2d = flippedImage.createGraphics();

        switch (direction) {
            case "HORIZONTAL":
                g2d.drawImage(image, image.getWidth(), 0, -image.getWidth(), image.getHeight(), null);
                break;
            case "VERTICAL":
                g2d.drawImage(image, 0, image.getHeight(), image.getWidth(), -image.getHeight(), null);
                break;
            case "BOTH":
                g2d.drawImage(image, image.getWidth(), image.getHeight(), -image.getWidth(), -image.getHeight(), null);
                break;
            default:
                throw new IllegalArgumentException("Invalid flip direction: " + direction);
        }
        g2d.dispose();
        return flippedImage;
    }

    @Override
    public void saveImageToFile(BufferedImage image, String filePath, String format) {
        if (image == null) {
            throw new IllegalArgumentException("No image provided to save.");
        }
        try {
            File outputFile = new File(filePath);
            if (!ImageIO.write(image, format, outputFile)) {
                throw new IOException("No writer found for format: " + format + ". Or failed to write.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving image to '" + filePath + "' in format '" + format + "': " + e.getMessage(), e);
        }
    }

    @Override
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        statusLabel.setText("Error: " + message);
    }

    @Override
    public void showStatusMessage(String message) {
        statusLabel.setText(message);
    }

    @Override
    public void updateDisplayImage(BufferedImage image) {
        this.currentImage = image; // Update the GUI's current image
        if (image != null) {
            imageDisplayLabel.setIcon(new ImageIcon(image));
            imageDisplayLabel.revalidate();
            imageDisplayLabel.repaint();
            resizeWidthField.setText(String.valueOf(image.getWidth()));
            resizeHeightField.setText(String.valueOf(image.getHeight()));
        } else {
            imageDisplayLabel.setIcon(null);
            resizeWidthField.setText("");
            resizeHeightField.setText("");
        }
    }
    // --- End DslProcessor.ImageOperationHandler methods ---


    // --- Manual GUI operation methods (simplified to call handlers) ---
    private void manualResize() {
        if (currentImage == null) {
            JOptionPane.showMessageDialog(this, "No image to resize.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int newWidth = Integer.parseInt(resizeWidthField.getText());
            int newHeight = Integer.parseInt(resizeHeightField.getText());
            BufferedImage result = resizeImage(currentImage, newWidth, newHeight);
            updateDisplayImage(result);
            statusLabel.setText("Image manually resized to: " + newWidth + "x" + newHeight);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for width and height.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            showErrorMessage("Error during manual resize: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void manualGrayscale() {
        if (currentImage == null) {
            JOptionPane.showMessageDialog(this, "No image to process.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            BufferedImage result = applyGrayscale(currentImage);
            updateDisplayImage(result);
            statusLabel.setText("Manually applied Grayscale filter.");
        } catch (Exception e) {
            showErrorMessage("Error applying manual grayscale: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void manualInvertColors() {
        if (currentImage == null) {
            JOptionPane.showMessageDialog(this, "No image to process.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            BufferedImage result = applyInvertColors(currentImage);
            updateDisplayImage(result);
            statusLabel.setText("Manually applied Invert Colors filter.");
        } catch (Exception e) {
            showErrorMessage("Error applying manual invert colors: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void manualRotate(double angleDegrees) {
        if (currentImage == null) {
            JOptionPane.showMessageDialog(this, "No image to process.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            BufferedImage result = applyRotation(currentImage, angleDegrees);
            updateDisplayImage(result);
            statusLabel.setText("Manually applied " + angleDegrees + "° rotation.");
        } catch (Exception e) {
            showErrorMessage("Error applying manual rotation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void manualFlip(String direction) {
        if (currentImage == null) {
            JOptionPane.showMessageDialog(this, "No image to process.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            BufferedImage result = applyFlip(currentImage, direction);
            updateDisplayImage(result);
            statusLabel.setText("Manually applied " + direction.toLowerCase() + " flip.");
        } catch (Exception e) {
            showErrorMessage("Error applying manual flip: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // --- End Manual GUI operation methods ---


    private void openImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an Image File");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif", "bmp"));

        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                originalImage = loadImageFromFile(selectedFile.getAbsolutePath());
                if (originalImage != null) {
                    updateDisplayImage(originalImage);
                    statusLabel.setText("Image '" + selectedFile.getName() + "' loaded successfully via GUI.");
                } else {
                    statusLabel.setText("Failed to load image via GUI.");
                }
            } catch (Exception e) {
                showErrorMessage("Error loading image via GUI: " + e.getMessage());
            }
        } else {
            statusLabel.setText("Image selection cancelled via GUI.");
        }
    }

    private void saveImage() {
        if (currentImage == null) {
            JOptionPane.showMessageDialog(this, "No image to save.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Image As");
        String defaultFileName = "processed_image.png";
        if (originalImage != null && originalImage.getProperty("file_name") instanceof String) {
            String originalFileName = new File((String)originalImage.getProperty("file_name")).getName();
            String baseName = originalFileName.lastIndexOf('.') > 0 ? originalFileName.substring(0, originalFileName.lastIndexOf('.')) : originalFileName;
            defaultFileName = "processed_" + baseName + ".png";
        }
        fileChooser.setSelectedFile(new File(defaultFileName));

        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG Images (*.png)", "png"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JPEG Images (*.jpg, *.jpeg)", "jpg", "jpeg"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("GIF Images (*.gif)", "gif"));
        fileChooser.setAcceptAllFileFilterUsed(false);

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String format = "png";

            FileNameExtensionFilter selectedFilter = (FileNameExtensionFilter) fileChooser.getFileFilter();
            if (selectedFilter != null && selectedFilter.getExtensions().length > 0) {
                format = selectedFilter.getExtensions()[0];
            }

            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith("." + format)) {
                fileToSave = new File(filePath + "." + format);
            }

            try {
                saveImageToFile(currentImage, fileToSave.getAbsolutePath(), format); // Use handler method
                statusLabel.setText("Image saved successfully to: " + fileToSave.getName() + " via GUI.");
            } catch (Exception e) {
                showErrorMessage("Error saving image via GUI: " + e.getMessage());
            }
        } else {
            statusLabel.setText("Image save cancelled via GUI.");
        }
    }

    private void resetImage() {
        if (originalImage == null) {
            JOptionPane.showMessageDialog(this, "No original image to reset to.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        updateDisplayImage(originalImage);
        statusLabel.setText("Image reset to original via GUI.");
    }

    private void executeDslCommand() {
        String script = dslCommandField.getText();
        dslProcessor.processScript(script);
    }

    // Main method to run the GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ImageProcessorGUI().setVisible(true);
        });
    }
}