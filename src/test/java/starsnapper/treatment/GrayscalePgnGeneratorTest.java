package starsnapper.treatment;

import org.junit.Test;
import sun.misc.IOUtils;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 18/10/2015
 */
public class GrayscalePgnGeneratorTest {

    @Test
    public void testPgnGeneration() throws IOException {
        int[] pixels = new int[256 * 256];

        for(int row = 0; row < 256; row++) {
            for(int column = 0; column < 256; column++) {
                double distance = Math.sqrt(row * row + column * column) / 363.0;
                pixels[row * 256 + column] = (int)(distance * 65535);
            }
        }

        GrayscalePngGenerator generator = new GrayscalePngGenerator(256, 256, 16);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        generator.writePixelsToStream(pixels, out);
        byte[] generated = out.toByteArray();
        byte[] reference = IOUtils.readFully(GrayscalePngGenerator.class.getClassLoader().getResourceAsStream("reference_image.png"), -1, true);
        assertArrayEquals(reference, generated);
    }
}
