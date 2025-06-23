package org;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MainApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                processUserSelectedImage();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void processUserSelectedImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an Image File");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image Files", "jpg", "jpeg", "png", "gif", "bmp");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());

            try {
                BufferedImage originalImage = ImageIO.read(selectedFile);

                if (originalImage == null) {
                    JOptionPane.showMessageDialog(null, "Could not read image: " + selectedFile.getName(), "Image Load Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                System.out.println("Image loaded. Dimensions: " + originalImage.getWidth() + "x" + originalImage.getHeight());

                // --- YOUR IMAGE PROCESSING LOGIC GOES HERE ---
                // Replace the 'invertImage' call with your actual processing.
                // For example:
                // BufferedImage processedImage = YourImageProcessor.applyGrayscale(originalImage);
                BufferedImage processedImage = invertImage(originalImage); // Example processing

                displayImage(originalImage, "Original Image - " + selectedFile.getName());
                displayImage(processedImage, "Processed Image - " + selectedFile.getName());

                String outputFileName = "processed_" + selectedFile.getName();
                String fileExtension = getFileExtension(selectedFile);
                if (fileExtension.isEmpty()) {
                    fileExtension = "png";
                }
                saveProcessedImage(processedImage, outputFileName, fileExtension);

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error loading/processing image: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Unexpected error: " + e.getMessage(), "Processing Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("File selection cancelled.");
            JOptionPane.showMessageDialog(null, "No image selected.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static BufferedImage invertImage(BufferedImage originalImage) {
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

    private static void displayImage(BufferedImage image, String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JLabel label = new JLabel(new ImageIcon(image));
        frame.getContentPane().add(label);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void saveProcessedImage(BufferedImage image, String fileName, String format) throws IOException {
        File outputFile = new File(fileName);
        ImageIO.write(image, format, outputFile);
        System.out.println("Processed image saved to: " + outputFile.getAbsolutePath());
        JOptionPane.showMessageDialog(null, "Processed image saved as " + fileName, "Save Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0 && lastDot < name.length() - 1) {
            return name.substring(lastDot + 1).toLowerCase();
        }
        return "";
    }
}