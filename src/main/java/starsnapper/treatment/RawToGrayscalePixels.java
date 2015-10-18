package starsnapper.treatment;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Class that converts raw data from the camera into values of grayscale pixels
 *
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 17/10/2015.
 */
public class RawToGrayscalePixels {

    protected int width;
    protected int height;
    protected boolean interlaced;
    protected int bytesPerPixel;

    /**
     * Constructor
     *
     * @param width the width of the image in pixels
     * @param height the height of the image in pixels
     * @param bytesPerPixel number of bytes per pixel (not bits!)
     * @param interlaced whether the image in interlaced or not
     */
    public RawToGrayscalePixels(int width, int height, int bytesPerPixel, boolean interlaced) {
        this.width = width;
        this.height = height;
        this.bytesPerPixel = bytesPerPixel;
        this.interlaced = interlaced;
    }

    /**
     * Converts the raw data into pixel values. If the image is interlaced, the result is deinterlaced
     *
     * @param raw the raw data from the camera
     * @return an array with the pixel values
     */
    public int[] convertRawToGrayscalePixels(byte[] raw) {
        int[] pixels = new int[this.width * this.height];
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        for(int row = 0; row < this.height; row++) {
            int lineStart = this.width * bytesPerPixel * (this.interlaced ? (row + (row % 2 * this.height)) / 2 : row);
            int targetStart = row * this.width;

            for(int column = 0; column < this.width; column++) {
                bb.clear();

                for(int i = 0; i < bytesPerPixel; i++) {
                    bb.put(raw[lineStart + column * bytesPerPixel + i]);
                }

                for(int i = 0; i < (4 - bytesPerPixel); i++) {
                    bb.put((byte)0);
                }

                pixels[targetStart + column] = bb.getInt(0);
            }
        }

        return pixels;
    }
}
