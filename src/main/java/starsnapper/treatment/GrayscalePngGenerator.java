package starsnapper.treatment;

import ar.com.hjg.pngj.*;

import java.io.OutputStream;

/**
 * A class that generates grayscale PNG files from an array of pixel values
 *
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 15/10/2015.
 */
public class GrayscalePngGenerator {

    protected int width;
    protected int height;
    protected int bitsPerPixel;

    /**
     * Constructor
     *
     * @param width width of the image, in pixels
     * @param height height of the image, in pixels
     * @param bitsPerPixel number of bits per pixel (values other than 8 and 16 result in undefined behaviour)
     */
    public GrayscalePngGenerator(int width, int height, int bitsPerPixel) {
        this.width = width;
        this.height = height;
        this.bitsPerPixel = bitsPerPixel;
    }

    /**
     * Converts an array of pixels into a grayscale PNG image into a stream
     *
     * @param pixels array of pixels. These must represent lines from the top to the bottom and each line is from left to right
     * @param stream Stream in which the PNG data will be written
     */
    public void writePixelsToStream(int[] pixels, OutputStream stream) {
        ImageInfo info = new ImageInfo(this.width, this.height, this.bitsPerPixel, false, true, false);
        PngWriter pngw = new PngWriter(stream, info);
        
        for(int row = 0; row < this.height; row++) {
            int[] lineData = new int[this.width];
            int lineStart = row * this.width;

            for(int column = 0; column < this.width; column++) {
                lineData[column] = pixels[lineStart + column];
            }

            ImageLineInt line = new ImageLineInt(info, lineData);
            pngw.writeRow(line);
        }

        pngw.close();
    }
}
