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

    private final int width;
    private final int height;
    private final int bytesPerPixel;

    /**
     * Constructor
     *
     * @param width the width of the image in pixels
     * @param height the height of the image in pixels
     * @param bytesPerPixel number of bytes per pixel (not bits!)
     */
    public RawToGrayscalePixels(int width, int height, int bytesPerPixel) {
        this.width = width;
        this.height = height;
        this.bytesPerPixel = bytesPerPixel;
    }

    /**
     * Converts the raw data into pixel values.
     *
     * @param raw the raw data from the camera
     * @return an array with the pixel values
     */
    public int[] convertRawToGrayscalePixels(byte[] raw) {
        int[] pixels = new int[this.width * this.height];
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        for(int row = 0; row < this.height; row++) {
            int lineStart = this.width * bytesPerPixel * row;
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

    /**
     * Converts the interlaced raw data into pixel values.
     *
     * @param raw the raw data from the camera
     * @param normalization normalization of the fields
     * @return an array with the pixel values
     */
    public int[] convertRawInterlacedToGrayscalePixels(byte[][] raw, double[] normalization) {
        int numberOfFields = raw.length;
        int[] pixels = new int[this.width * this.height * numberOfFields];
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        for(int row = 0; row < this.height * numberOfFields; row++) {
            int lineStart = (row / numberOfFields) * this.width * bytesPerPixel;
            int field = row % numberOfFields;
            int targetStart = row * this.width;

            for (int column = 0; column < this.width; column++) {
                bb.clear();

                for (int i = 0; i < bytesPerPixel; i++) {
                    bb.put(raw[field][lineStart + column * bytesPerPixel + i]);
                }

                for (int i = 0; i < (4 - bytesPerPixel); i++) {
                    bb.put((byte) 0);
                }

                pixels[targetStart + column] = (int)((double)bb.getInt(0) * normalization[field]);
            }
        }

        return pixels;
    }
}
