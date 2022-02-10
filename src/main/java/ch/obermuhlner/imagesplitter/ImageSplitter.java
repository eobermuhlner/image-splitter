package ch.obermuhlner.imagesplitter;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.ceil;
import static java.lang.Math.min;

public class ImageSplitter {

  private static final String VERSION = "0.1";

  public static void main(String[] args) {
    Integer splitWidth = null;
    Integer splitHeight = null;
    Double splitWidthPercent = null;
    Double splitHeightPercent = null;
    String format = "png";
    List<String> filenames = new ArrayList<>();

    for (int i = 0; i < args.length; i++) {
      if (args[i].startsWith("-")) {
        switch (args[i]) {
          case "-w":
          case "--width":
            i++;
            if (args[i].endsWith("%")) {
              splitWidthPercent = Double.parseDouble(args[i].substring(0, args[i].length() - 1)) / 100;
            } else {
              splitWidth = Integer.parseInt(args[i]);
            }
            break;
          case "-h":
          case "--height":
            i++;
            if (args[i].endsWith("%")) {
              splitHeightPercent = Double.parseDouble(args[i].substring(0, args[i].length() - 1)) / 100;
            } else {
              splitHeight = Integer.parseInt(args[i]);
            }
            break;
          case "-f":
          case "--format":
            i++;
            format = args[i];
            break;
          case "-v":
          case "--version":
            System.out.println("image-splitter " + VERSION);
            System.exit(0);
          case "--help":
            printHelp();
            break;
          default:
            System.err.println("Unknown option: " + args[i]);
            System.exit(1);
        }
      } else {
        filenames.add(args[i]);
      }
    }

    if (splitWidth != null && splitWidth <= 0) {
      System.err.println("The width must be a positive number: " + splitWidth);
      System.exit(1);
    }
    if (splitHeight != null && splitHeight <= 0) {
      System.err.println("The height must be a positive number: " + splitHeight);
      System.exit(1);
    }
    if (splitWidthPercent != null && splitWidthPercent <= 0) {
      System.err.println("The width in percent must be a positive number: " + splitWidthPercent*100 + "%");
      System.exit(1);
    }
    if (splitHeightPercent != null && splitHeightPercent <= 0) {
      System.err.println("The height in percent must be a positive number: " + splitHeightPercent*100 + "%");
      System.exit(1);
    }

    if (filenames.isEmpty()) {
      printHelp();
    }

    for (String filename : filenames) {
      try {
        BufferedImage image = ImageIO.read(new File(filename));

        int counter = 1;
        int width = calculateSize(image.getWidth(), splitWidth, splitWidthPercent);
        int height = calculateSize(image.getHeight(), splitHeight, splitHeightPercent);
        for (int y = 0; y < image.getHeight(); y+=height) {
          for (int x = 0; x < image.getWidth(); x+=width) {
            BufferedImage subImage = image.getSubimage(
                x,
                y,
                min(width, image.getWidth() - x),
                min(height, image.getHeight() - y)
            );

            String outputName = filename + "_" + counter + "." + format;
            System.out.println("Writing " + outputName + " x=" + x + " y=" + y + " w=" + width + " h=" + height);
            ImageIO.write(subImage, format, new File(outputName));
            counter++;
          }
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  private static void printHelp() {
    System.out.println(
            "USAGE image-splitter [options] [files]\n"
            + "OPTIONS\n"
            + "  -w pixels\n"
            + "  --width pixels\n"
            + "    Splits the input image into images with the specified width in pixels.\n"
            + "\n"
            + "  -h pixels\n"
            + "  --height pixels\n"
            + "    Splits the input image into images with the specified height in pixels.\n"
            + "\n"
            + "  -f format\n"
            + "  --format format\n"
            + "    Format of the output images (png, jpg, bmp).\n"
            + "\n"
            + "  -v\n"
            + "  --version\n"
            + "    Prints the version number.\n"
    );
    System.exit(0);
  }

  private static int calculateSize(int imageSize, Integer splitSize, Double splitSizePercent) {
    if (splitSize != null) {
      return min(splitSize, imageSize);
    }
    if (splitSizePercent != null) {
      return min((int) ceil(imageSize * splitSizePercent), imageSize);
    }
    return imageSize;
  }
}
