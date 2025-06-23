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









package org;
import  com.parsingfiles.*;

import com.imagecore.ImageUtils;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class MainApp {
public static void main(String[] args) {
String outputDir = "script_output_images";
new File(outputDir).mkdirs();

        String scriptFilePath = "myscript.is";
        File scriptFile = new File(scriptFilePath);

        if (!scriptFile.exists()) {
            try {
                String inputImagePathForScript = outputDir + "/" + "input.png";
                File inputPng = new File(outputDir + File.separator + "input.png");
                if (!inputPng.exists()) {
                    BufferedImage dummyInput = new BufferedImage(200, 150, BufferedImage.TYPE_INT_ARGB);
                    for (int x = 0; x < dummyInput.getWidth(); x++) {
                        for (int y = 0; y < dummyInput.getHeight(); y++) {
                            dummyInput.setRGB(x, y, new Color(0, 0, 255, 255).getRGB());
                        }
                    }
                    ImageUtils.saveImage(dummyInput, inputPng.getAbsolutePath(), "png");
                }

                String dummyScriptContent =
                        "LOAD \"" + inputImagePathForScript + "\" AS originalImage;\n" +
                                "RESIZE originalImage WIDTH 150 HEIGHT 100 AS resizedImage;\n" +
                                "GRAYSCALE resizedImage AS grayImage;\n" +
                                "SAVE grayImage TO \"" + outputDir + "/" + "processed_gray.png\" FORMAT \"png\";\n" +
                                "ROTATE originalImage ANGLE 45 AS rotatedImage;\n" +
                                "SAVE rotatedImage TO \"" + outputDir + "/" + "processed_rotated.png\" FORMAT \"png\";\n" +
                                "FLIP originalImage HORIZONTAL AS flippedHImage;\n" +
                                "SAVE flippedHImage TO \"" + outputDir + "/" + "processed_flipped_h.png\" FORMAT \"png\";\n" +
                                "FLIP originalImage VERTICAL AS flippedVImage;\n" +
                                "SAVE flippedVImage TO \"" + outputDir + "/" + "processed_flipped_v.png\" FORMAT \"png\";\n" +
                                "FLIP originalImage BOTH AS flippedBImage;\n" +
                                "SAVE flippedBImage TO \"" + outputDir + "/" + "processed_flipped_b.png\" FORMAT \"png\";\n";
                Files.writeString(Paths.get(scriptFilePath), dummyScriptContent);
                return;
            } catch (IOException e) {
                System.err.println("Could not create dummy script or image: " + e.getMessage());
                return;
            }
        }

        try {
            String scriptContent = Files.readString(Paths.get(scriptFilePath), StandardCharsets.UTF_8);

            ImagescriptLexer lexer = new ImagescriptLexer(CharStreams.fromString(scriptContent));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ImagescriptParser parser = new ImagescriptParser(tokens);

            ParseTree tree = parser.script();
            ImagescriptBaseListener listener = new ImagescriptBaseListener();
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(listener, tree);

            System.out.println("Script execution finished. Check the '" + outputDir + "' directory for output images.");

        } catch (IOException e) {
            System.err.println("Error reading script file or processing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
