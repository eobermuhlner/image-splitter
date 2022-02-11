package ch.obermuhlner.imagesplitter;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageWriterSpi;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Math.ceil;
import static java.lang.Math.min;

public class ImageSplitter {

  private static final String VERSION = "0.2";

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
          case "--supported":
            printSupportedFormats();
            System.exit(0);
            break;
          case "-v":
          case "--version":
            System.out.println("image-splitter " + VERSION);
            System.exit(0);
          case "--help":
            printHelp();
            System.exit(0);
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
      System.exit(0);
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
            System.out.println("Writing " + outputName + " x=" + x + " y=" + y + " width=" + width + " height=" + height);
            ImageIO.write(subImage, format.toLowerCase(), new File(outputName));
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
            + "\n"
            + "Splits the input image files into multiple slices with a specified width and/or height.\n"
            + "\n"
            + "OPTIONS\n"
            + "  -w pixels\n"
            + "  --width pixels\n"
            + "    Splits the input image into images with the specified width in pixels.\n"
            + "    If this argument is not specified the full width of each image is used.\n"
            + "\n"
            + "  -h pixels\n"
            + "  --height pixels\n"
            + "    Splits the input image into images with the specified height in pixels.\n"
            + "    If this argument is not specified the full height of each image is used.\n"
            + "\n"
            + "  -f format\n"
            + "  --format format\n"
            + "    Format of the output images (png, jpg, bmp).\n"
            + "\n"
            + "  -v\n"
            + "  --version\n"
            + "    Prints the version number.\n"
            + "\n"
            + "  --help\n"
            + "    Prints this help text.\n"
    );
  }

  private static void printSupportedFormats() {
    Set<String> formats = new LinkedHashSet<>();

    Iterator<ImageWriterSpi> serviceProviders = IIORegistry.getDefaultInstance()
        .getServiceProviders(ImageWriterSpi.class, true);

    while (serviceProviders.hasNext()) {
      ImageWriterSpi serviceProvider = serviceProviders.next();
      formats.add(serviceProvider.getFileSuffixes()[0]);
    }

    for (String format : formats) {
      System.out.println("  " + format);
    }
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
