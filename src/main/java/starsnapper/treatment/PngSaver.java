package starsnapper.treatment;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 19/01/2016.
 */
public class PngSaver implements Runnable {

    private final short width;
    private final short height;
    private final byte[][] rawData;
    private final double[] normalization;
    private final OutputStream out;

    public PngSaver(short width, short heigth, byte[][] rawData, double[] normalization, OutputStream out) {
        this.width = width;
        this.height = heigth;
        this.rawData = rawData;
        this.normalization = normalization;
        this.out = out;
    }

    public void run() {
        try {
            RawToGrayscalePixels pixelsGenerator = new RawToGrayscalePixels(this.width, this.height, 2);
            int[] pixels = pixelsGenerator.convertRawInterlacedToGrayscalePixels(this.rawData, this.normalization);
            GrayscalePngGenerator pngGenerator = new GrayscalePngGenerator(this.width, this.height * 2, 16);
            pngGenerator.writePixelsToStream(pixels, this.out);
            this.out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
