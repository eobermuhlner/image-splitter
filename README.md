# image-splitter
Command line tool to split an image into multiple slices with a specified width and/or height.


# Usage

```
USAGE image-splitter [options] [files]
OPTIONS
  -w pixels
  --width pixels
    Splits the input image into images with the specified width in pixels.

  -h pixels
  --height pixels
    Splits the input image into images with the specified height in pixels.

  -f format
  --format format
    Format of the output images (png, jpg, bmp).

  -v
  --version
    Prints the version number.
```

# Examples

```shell
image-splitter --height 100 lena.png
```
Splits the input image into horizontal slices of 100 pixels.

```shell
image-splitter --width 400 lena.png
```
Splits the input image into vertical slices of 400 pixels.

```shell
image-splitter --width 400 --height 500 lena.png
```
Splits the input image vertically and horizontally into 400 by 500 pixel tiles.

```shell
image-splitter --width 50% --height 50% lena.png
```
Splits the input image vertically and horizontally into four (almost) equally sized tiles.
If the image size is not divisible by the given percentage then the result is rounded up
to avoid producing extra slices of a single pixel.

```shell
image-splitter --width 400 --format jpg lena.png
```
Splits the input image into vertical slices of 400 pixels, storing the result files in "jpg" format.

```shell
image-splitter --supported
```
Lists the supported image formats (depends on the installed Java version).

# Installation

Download the newest release package and unzip the downloaded content in an appropriate place.

The absolute path of the `bin` directory must be added to the `PATH` environment variable.

The `image-splitter` release 0.2 and later can run on any Java VM >= Java 8.

# Credit

Special thanks to Robert Aalderink for requesting and beta testing the `image-splitter`.
