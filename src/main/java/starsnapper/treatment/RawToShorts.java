package starsnapper.treatment;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Class that converts raw data from the camera into a float matrix
 *
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 01/02/2016
 */
public class RawToShorts {

    private final int width;
    private final int height;
    private final int bytesPerValue;

    /**
     * Constructor
     *
     * @param width the width of the image in pixels
     * @param height the height of the image in pixels
     * @param bytesPerValue number of bytes per value
     */
    public RawToShorts(int width, int height, int bytesPerValue) {
        this.width = width;
        this.height = height;
        this.bytesPerValue = bytesPerValue;
    }

    /**
     * Converts the interlaced raw data into pixel values.
     *
     * @param raw the raw data from the camera
     * @param normalization normalization of the fields
     * @return an array with the pixel values
     */
    public short[][] convertRawInterlacedToShorts(byte[][] raw, double[] normalization) {
        int numberOfFields = raw.length;
        short[][] pixels = new short[this.height * numberOfFields][this.width];
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        for(int row = 0; row < this.height; row++) {
            int lineStart = row * this.width * bytesPerValue;

            for(int field = 0; field < numberOfFields; field++) {
                for (int column = 0; column < this.width; column++) {
                    bb.clear();

                    for (int i = 0; i < bytesPerValue; i++) {
                        bb.put(raw[field][lineStart + column * bytesPerValue + i]);
                    }

                    for (int i = 0; i < (4 - bytesPerValue); i++) {
                        bb.put((byte) 0);
                    }

                    int normalized = (int)((double)bb.getInt(0) * normalization[field]);
                    pixels[row * numberOfFields + field][column] = (short)(normalized & 0xffff);
                }
            }
        }

        return pixels;
    }
    /**

     * Converts the raw data into float values
     *
     * @param raw the raw data from the camera
     * @return an array with the pixel values
     */
    public short[][] convertRawToShorts(byte[] raw, double normalization) {
        short[][] pixels = new short[this.height * 2][this.width];
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        for(int row = 0; row < this.height; row++) {
            int lineStart = this.width * bytesPerValue * row;

            for(int column = 0; column < this.width; column++) {
                bb.clear();

                for(int i = 0; i < bytesPerValue; i++) {
                    bb.put(raw[lineStart + column * bytesPerValue + i]);
                }

                for(int i = 0; i < (4 - bytesPerValue); i++) {
                    bb.put((byte)0);
                }

                pixels[row][column] = (short)((int)((double)bb.getInt(0) * normalization) & 0xffff);
            }
        }

        return pixels;
    }
}
