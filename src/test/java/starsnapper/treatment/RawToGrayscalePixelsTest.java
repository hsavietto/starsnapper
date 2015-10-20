package starsnapper.treatment;

import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 18/10/2015
 */
public class RawToGrayscalePixelsTest {

    @Test
    public void testRawToPixelsNonInterlaced() throws IOException, ClassNotFoundException {
        byte[] pixelsInBytes = new byte[256 * 256 * 2];

        for(int row = 0; row < 256; row++) {
            int lineStart = row * 256 * 2;

            for(int column = 0; column < 256; column++) {
                double distance = Math.sqrt(row * row + column * column) / 363.0;
                int intensity = (int)(distance * 65535);
                pixelsInBytes[lineStart + column * 2] = (byte)(intensity & 0xff);
                pixelsInBytes[lineStart + column * 2 + 1] = (byte)((intensity >> 8) & 0xff);
            }
        }

        RawToGrayscalePixels convertor = new RawToGrayscalePixels(256, 256, 2);
        int[] pixels = convertor.convertRawToGrayscalePixels(pixelsInBytes);
        ObjectInputStream referenceStream = new ObjectInputStream(getClass().getClassLoader().getResourceAsStream("reference_pixels.bin"));
        int [] referencePixels = (int[])referenceStream.readObject();
        assertArrayEquals(referencePixels, pixels);
    }

    @Test
    public void testRawToPixelsInterlaced() throws IOException, ClassNotFoundException {
        byte[][] pixelsInBytes = new byte[2][256 * 128 * 2];

        for(int row = 0; row < 128; row++) {
            int lineStart = row * 256 * 2;

            for(int field = 0; field < 2; field++) {
                double interlacedRow = row + 128 * field;

                for(int column = 0; column < 256; column++) {
                    double distance = Math.sqrt(interlacedRow * interlacedRow + column * column) / 363.0;
                    int intensity = (int)(distance * 65535);
                    pixelsInBytes[field][lineStart + column * 2] = (byte)(intensity & 0xff);
                    pixelsInBytes[field][lineStart + column * 2 + 1] = (byte)((intensity >> 8) & 0xff);
                }
            }
        }

        RawToGrayscalePixels convertor = new RawToGrayscalePixels(256, 128, 2);
        int[] pixels = convertor.convertRawInterlacedToGrayscalePixels(pixelsInBytes);
        ObjectInputStream referenceStream = new ObjectInputStream(getClass().getClassLoader().getResourceAsStream("reference_pixels.bin"));
        int [] referencePixels = (int[])referenceStream.readObject();
        assertArrayEquals(referencePixels, pixels);
    }
}
