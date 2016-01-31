package starsnapper.treatment;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A class that generates grayscale PNG files from an array of pixel values
 *
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 15/10/2015.
 */
public class GrayscalePngGenerator {

    private final int width;
    private final int height;
    private final int bitsPerPixel;

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
        BufferedImage bi = new BufferedImage(this.width, this.height, BufferedImage.TYPE_USHORT_GRAY);
        Raster raster = bi.getRaster();
        DataBuffer db = raster.getDataBuffer();

        for(int i = 0; i < pixels.length; i++) {
            db.setElem(i, pixels[i]);
        }

        try {
            ImageIO.write(bi, "png", stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
