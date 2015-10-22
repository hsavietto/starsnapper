package starsnapper.treatment;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Class that converts raw data from the camera into a float matrix
 *
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 22/10/2015.
 */
public class RawToFloats {

    protected int width;
    protected int height;
    protected int bytesPerValue;

    /**
     * Constructor
     *
     * @param width the width of the image in pixels
     * @param height the height of the image in pixels
     * @param bytesPerValue number of bytes per value
     */
    public RawToFloats(int width, int height, int bytesPerValue) {
        this.width = width;
        this.height = height;
        this.bytesPerValue = bytesPerValue;
    }

    /**
     * Converts the raw data into float values
     *
     * @param raw the raw data from the camera
     * @return an array with the pixel values
     */
    public float[][] convertRawToFloats(byte[] raw) {
        float[][] pixels = new float[this.height * 2][this.width];
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

                pixels[row][column] = (float)bb.getInt(0);
            }
        }

        return pixels;
    }

    /**
     * Converts the interlaced raw data into pixel values.
     *
     * @param raw the raw data from the camera
     * @return an array with the pixel values
     */
    public float[][] convertRawInterlacedToFloats(byte[][] raw) {
        int numberOfFields = raw.length;
        float[][] pixels = new float[this.height * numberOfFields][this.width];
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

                    pixels[row * numberOfFields + field][column] = (float)bb.getInt(0);
                }
            }
        }

        return pixels;
    }
}
