//package com.imagecore;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.awt.Graphics2D;
//import java.awt.Color;
//import java.awt.geom.AffineTransform;
//import java.io.File;
//import java.io.IOException;
//
//public class ImageUtils {
//
//    public static BufferedImage loadImage(String filePath) {
//        try {
//            BufferedImage img = ImageIO.read(new File(filePath));
//            if (img == null) {
//                System.err.println("ImageUtils: Could not read image from: " + filePath + ". Format not supported or file corrupt.");
//            }
//            return img;
//        } catch (IOException e) {
//            System.err.println("ImageUtils: Error loading image " + filePath + ": " + e.getMessage());
//            return null;
//        }
//    }
//
//    public static boolean saveImage(BufferedImage image, String filePath, String format) {
//        if (image == null) {
//            System.err.println("ImageUtils: Cannot save null image.");
//            return false;
//        }
//        try {
//            File outputFile = new File(filePath);
//
//            File parentDir = outputFile.getParentFile();
//            if (parentDir != null && !parentDir.exists()) {
//                parentDir.mkdirs();
//            }
//            return ImageIO.write(image, format, outputFile);
//        } catch (IOException e) {
//            System.err.println("ImageUtils: Error saving image to " + filePath + " in format " + format + ": " + e.getMessage());
//            return false;
//        }
//    }
//
//    public static BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) {
//        if (originalImage == null) return null;
//        BufferedImage resized = new BufferedImage(newWidth, newHeight, originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType());
//        Graphics2D g = resized.createGraphics();
//        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
//        g.dispose();
//        return resized;
//    }
//
//    public static BufferedImage convertToGrayscale(BufferedImage originalImage) {
//        if (originalImage == null) return null;
//
//        BufferedImage grayImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
//        for (int i = 0; i < originalImage.getWidth(); i++) {
//            for (int j = 0; j < originalImage.getHeight(); j++) {
//                Color c = new Color(originalImage.getRGB(i, j), true); // true for alpha
//                int gray = (int)(0.3 * c.getRed() + 0.59 * c.getGreen() + 0.11 * c.getBlue());
//                Color newColor = new Color(gray, gray, gray, c.getAlpha()); // Preserve alpha
//                grayImage.setRGB(i, j, newColor.getRGB());
//            }
//        }
//        return grayImage;
//    }
//
//    public static BufferedImage rotateImage(BufferedImage originalImage, double angleDegrees) {
//        if (originalImage == null) return null;
//        double radians = Math.toRadians(angleDegrees);
//        int w = originalImage.getWidth();
//        int h = originalImage.getHeight();
//
//
//        double sin = Math.abs(Math.sin(radians));
//        double cos = Math.abs(Math.cos(radians));
//        int newW = (int) Math.floor(w * cos + h * sin);
//        int newH = (int) Math.floor(w * sin + h * cos);
//
//        BufferedImage rotated = new BufferedImage(newW, newH, originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType());
//        Graphics2D g = rotated.createGraphics();
//
//
//        g.translate((newW - w) / 2, (newH - h) / 2);
//        g.rotate(radians, w / 2, h / 2);
//        g.drawImage(originalImage, 0, 0, null);
//        g.dispose();
//        return rotated;
//    }
//
//    public static BufferedImage flipImage(BufferedImage originalImage, boolean horizontal, boolean vertical) {
//        if (originalImage == null) return null;
//        BufferedImage flipped = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType());
//        Graphics2D g = flipped.createGraphics();
//
//        AffineTransform tx = new AffineTransform();
//
//        if (horizontal) {
//            tx.concatenate(AffineTransform.getScaleInstance(-1, 1));
//            tx.concatenate(AffineTransform.getTranslateInstance(-originalImage.getWidth(), 0));
//        }
//        if (vertical) {
//            tx.concatenate(AffineTransform.getScaleInstance(1, -1));
//            tx.concatenate(AffineTransform.getTranslateInstance(0, -originalImage.getHeight()));
//        }
//
//        g.drawImage(originalImage, tx, null);
//        g.dispose();
//        return flipped;
//    }
//}

package com.imagecore;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    public static BufferedImage loadImage(String filePath) {
        try {
            BufferedImage img = ImageIO.read(new File(filePath));
            if (img == null) {
                System.err.println("ImageUtils: Could not read image from: " + filePath + ". Format not supported or file corrupt.");
            }
            return img;
        } catch (IOException e) {
            System.err.println("ImageUtils: Error loading image " + filePath + ": " + e.getMessage());
            return null;
        }
    }

    public static boolean saveImage(BufferedImage image, String filePath, String format) {
        if (image == null) {
            System.err.println("ImageUtils: Cannot save null image.");
            return false;
        }
        try {
            File outputFile = new File(filePath);

            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            return ImageIO.write(image, format, outputFile);
        } catch (IOException e) {
            System.err.println("ImageUtils: Error saving image to " + filePath + " in format " + format + ": " + e.getMessage());
            return false;
        }
    }

    public static BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) {
        if (originalImage == null) return null;
        BufferedImage resized = new BufferedImage(newWidth, newHeight,
                originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType());
        Graphics2D g = resized.createGraphics();

        // Add anti-aliasing for better quality
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return resized;
    }

    public static BufferedImage convertToGrayscale(BufferedImage originalImage) {
        if (originalImage == null) return null;

        BufferedImage grayImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < originalImage.getWidth(); i++) {
            for (int j = 0; j < originalImage.getHeight(); j++) {
                Color c = new Color(originalImage.getRGB(i, j), true); // true for alpha
                int gray = (int)(0.3 * c.getRed() + 0.59 * c.getGreen() + 0.11 * c.getBlue());
                Color newColor = new Color(gray, gray, gray, c.getAlpha()); // Preserve alpha
                grayImage.setRGB(i, j, newColor.getRGB());
            }
        }
        return grayImage;
    }

    public static BufferedImage rotateImage(BufferedImage originalImage, double angleDegrees) {
        if (originalImage == null) return null;
        double radians = Math.toRadians(angleDegrees);
        int w = originalImage.getWidth();
        int h = originalImage.getHeight();

        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int newW = (int) Math.floor(w * cos + h * sin);
        int newH = (int) Math.floor(w * sin + h * cos);

        BufferedImage rotated = new BufferedImage(newW, newH,
                originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType());
        Graphics2D g = rotated.createGraphics();

        // Add anti-aliasing
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.translate((newW - w) / 2, (newH - h) / 2);
        g.rotate(radians, w / 2, h / 2);
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();
        return rotated;
    }

    public static BufferedImage flipImage(BufferedImage originalImage, boolean horizontal, boolean vertical) {
        if (originalImage == null) return null;
        BufferedImage flipped = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(),
                originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType());
        Graphics2D g = flipped.createGraphics();

        AffineTransform tx = new AffineTransform();

        if (horizontal) {
            tx.concatenate(AffineTransform.getScaleInstance(-1, 1));
            tx.concatenate(AffineTransform.getTranslateInstance(-originalImage.getWidth(), 0));
        }
        if (vertical) {
            tx.concatenate(AffineTransform.getScaleInstance(1, -1));
            tx.concatenate(AffineTransform.getTranslateInstance(0, -originalImage.getHeight()));
        }

        g.drawImage(originalImage, tx, null);
        g.dispose();
        return flipped;
    }
}