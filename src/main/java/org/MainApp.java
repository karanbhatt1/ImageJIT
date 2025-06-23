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
