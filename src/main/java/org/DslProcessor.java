package org; // Adjust package as needed, e.g., org.imageprocessor

import com.parsingfiles.*; // Import ANTLR generated classes
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;

public class DslProcessor {

    // Callback interface for the DSL processor to interact with the GUI
    public interface ImageOperationHandler {
        BufferedImage loadImageFromFile(String filePath);
        BufferedImage resizeImage(BufferedImage image, int width, int height);
        BufferedImage applyGrayscale(BufferedImage image);
        BufferedImage applyInvertColors(BufferedImage image);
        BufferedImage applyRotation(BufferedImage image, double angleDegrees);
        BufferedImage applyFlip(BufferedImage image, String direction);
        void saveImageToFile(BufferedImage image, String filePath, String format);
        void showErrorMessage(String message);
        void showStatusMessage(String message);
        void updateDisplayImage(BufferedImage image); // To update the main GUI display
    }

    private final ImageOperationHandler handler;
    private final Map<String, BufferedImage> imageVariables; // To store images by ID

    public DslProcessor(ImageOperationHandler handler) {
        this.handler = handler;
        this.imageVariables = new HashMap<>();
    }

    public void processScript(String scriptText) {
        if (scriptText == null || scriptText.trim().isEmpty()) {
            handler.showErrorMessage("No DSL script entered.");
            return;
        }

        // Clear previous variables for a new script run
        imageVariables.clear();
        handler.showStatusMessage("Processing DSL script...");

        try {
            CharStream input = CharStreams.fromString(scriptText);
            ImagescriptLexer lexer = new ImagescriptLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ImagescriptParser parser = new ImagescriptParser(tokens);

            // Add custom error listener
            parser.removeErrorListeners();
            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                    throw new IllegalArgumentException("DSL Syntax Error at line " + line + ", position " + charPositionInLine + ": " + msg);
                }
            });

            ParseTree tree = parser.script();

            // Create and walk the custom listener
            ImagescriptCustomListener listener = new ImagescriptCustomListener(handler, imageVariables);
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(listener, tree);

            handler.showStatusMessage("DSL script executed successfully.");

        } catch (IllegalArgumentException e) {
            handler.showErrorMessage("DSL Error: " + e.getMessage());
        } catch (Exception e) {
            handler.showErrorMessage("Error processing DSL script: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Custom ANTLR Listener implementation
    private static class ImagescriptCustomListener extends ImagescriptBaseListener {
        private final ImageOperationHandler handler;
        private final Map<String, BufferedImage> imageVariables;

        public ImagescriptCustomListener(ImageOperationHandler handler, Map<String, BufferedImage> imageVariables) {
            this.handler = handler;
            this.imageVariables = imageVariables;
        }

        private String stripQuotes(String s) {
            return s.substring(1, s.length() - 1).replace("\"\"", "\"");
        }

        private BufferedImage getMandatoryImage(String varName) {
            BufferedImage img = imageVariables.get(varName);
            if (img == null) {
                throw new IllegalStateException("Image variable '" + varName + "' not found. Load or create it first.");
            }
            return img;
        }

        @Override
        public void exitLoadStatement(ImagescriptParser.LoadStatementContext ctx) {
            String filePath = stripQuotes(ctx.filePath.getText());
            String varName = ctx.varName.getText();
            try {
                BufferedImage image = handler.loadImageFromFile(filePath);
                if (image != null) {
                    imageVariables.put(varName, image);
                    handler.updateDisplayImage(image); // Show loaded image on GUI
                    handler.showStatusMessage("Loaded '" + filePath + "' as '" + varName + "'");
                } else {
                    handler.showErrorMessage("Failed to load image: " + filePath);
                }
            } catch (Exception e) {
                handler.showErrorMessage("Error loading image '" + filePath + "': " + e.getMessage());
            }
        }

        @Override
        public void exitResizeStatement(ImagescriptParser.ResizeStatementContext ctx) {
            String inputVar = ctx.inputVar.getText();
            String outputVar = ctx.outputVar.getText();
            int width = Integer.parseInt(ctx.width.getText()); // Now expecting an INT from grammar
            int height = Integer.parseInt(ctx.height.getText()); // Now expecting an INT from grammar

            try {
                BufferedImage inputImage = getMandatoryImage(inputVar);
                BufferedImage resizedImage = handler.resizeImage(inputImage, width, height);
                imageVariables.put(outputVar, resizedImage);
                handler.updateDisplayImage(resizedImage); // Show result
                handler.showStatusMessage("Resized '" + inputVar + "' to " + width + "x" + height + " as '" + outputVar + "'");
            } catch (Exception e) {
                handler.showErrorMessage("Error resizing image: " + e.getMessage());
            }
        }

        @Override
        public void exitGrayscaleStatement(ImagescriptParser.GrayscaleStatementContext ctx) {
            String inputVar = ctx.inputVar.getText();
            String outputVar = ctx.outputVar.getText();

            try {
                BufferedImage inputImage = getMandatoryImage(inputVar);
                BufferedImage grayscaleImage = handler.applyGrayscale(inputImage);
                imageVariables.put(outputVar, grayscaleImage);
                handler.updateDisplayImage(grayscaleImage); // Show result
                handler.showStatusMessage("Applied grayscale to '" + inputVar + "' as '" + outputVar + "'");
            } catch (Exception e) {
                handler.showErrorMessage("Error applying grayscale: " + e.getMessage());
            }
        }

        @Override
        public void exitRotateStatement(ImagescriptParser.RotateStatementContext ctx) {
            String inputVar = ctx.inputVar.getText();
            String outputVar = ctx.outputVar.getText();
            double angle = Double.parseDouble(ctx.angle.getText()); // Uses NUMBER which allows decimals

            try {
                BufferedImage inputImage = getMandatoryImage(inputVar);
                BufferedImage rotatedImage = handler.applyRotation(inputImage, angle);
                imageVariables.put(outputVar, rotatedImage);
                handler.updateDisplayImage(rotatedImage); // Show result
                handler.showStatusMessage("Rotated '" + inputVar + "' by " + angle + " degrees as '" + outputVar + "'");
            } catch (Exception e) {
                handler.showErrorMessage("Error rotating image: " + e.getMessage());
            }
        }

        @Override
        public void exitFlipStatement(ImagescriptParser.FlipStatementContext ctx) {
            String inputVar = ctx.inputVar.getText();
            String outputVar = ctx.outputVar.getText();
            String direction = ctx.flipDirection().getText().toUpperCase(); // HORIZONTAL, VERTICAL, BOTH

            try {
                BufferedImage inputImage = getMandatoryImage(inputVar);
                BufferedImage flippedImage = handler.applyFlip(inputImage, direction);
                imageVariables.put(outputVar, flippedImage);
                handler.updateDisplayImage(flippedImage); // Show result
                handler.showStatusMessage("Flipped '" + inputVar + "' " + direction.toLowerCase() + " as '" + outputVar + "'");
            } catch (Exception e) {
                handler.showErrorMessage("Error flipping image: " + e.getMessage());
            }
        }

        @Override
        public void exitSaveStatement(ImagescriptParser.SaveStatementContext ctx) {
            String varName = ctx.varName.getText();
            String filePath = stripQuotes(ctx.filePath.getText()); // Access using label
            String formatName = stripQuotes(ctx.formatName.getText()); // Access using label

            try {
                BufferedImage imageToSave = getMandatoryImage(varName);
                handler.saveImageToFile(imageToSave, filePath, formatName);
                handler.showStatusMessage("Saved '" + varName + "' to '" + filePath + "' in " + formatName + " format.");
            } catch (Exception e) {
                handler.showErrorMessage("Error saving image: " + e.getMessage());
            }
        }
    }
}