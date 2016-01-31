package starsnapper.usb;

import org.usb4java.LibUsbException;
import starsnapper.commands.CommandFlags;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 25/01/2016.
 */
public class TestPatternUsbController implements IUsbController {

    private final IClock clock;
    private final long millisForFullExposure;
    private final int[][] testPatternPixels;
    private long resetPixelsTimestamp;
    private BlockingQueue<byte[]> outputBuffer;

    public TestPatternUsbController(IClock clock, String resourceName, long millisForFullExposure) throws IOException {
        this.clock = clock;
        InputStream testPatternStream = getClass().getClassLoader().getResourceAsStream(resourceName);
        BufferedImage testPattern = ImageIO.read(testPatternStream);

        int width = testPattern.getWidth();
        int height = testPattern.getHeight();
        this.testPatternPixels = new int[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int rgb = testPattern.getRGB(col, row);
                int b = (rgb & 0xff);
                int g = ((rgb >> 8) & 0xff);
                int r = ((rgb >> 16) & 0xff);
                int grayscale = ((r * 2126 + g * 7152 + b * 722) / 10000) << 8;
                testPatternPixels[row][col] = grayscale;
            }
        }

        this.millisForFullExposure = millisForFullExposure;
        this.outputBuffer = new ArrayBlockingQueue<>(16);
    }

    @Override
    public IDevice findDevice(short vendorId) throws LibUsbException {
        return new MockDevice();
    }

    @Override
    public IDevice findDevice(short vendorId, short productId) throws LibUsbException {
        return new MockDevice();
    }

    @Override
    public void releaseInterface(IDeviceHandle handle, int iface) {

    }

    @Override
    public void open(IDevice device, IDeviceHandle handle) {

    }

    @Override
    public void close(IDeviceHandle handle) {

    }

    @Override
    public IDeviceHandle createDeviceHandle() {
        return null;
    }

    @Override
    public void claimInterface(IDeviceHandle handle, int iface) {

    }

    private byte[] getPixels(int field, long exposure) {
        int height = this.testPatternPixels.length / 2;
        int width = this.testPatternPixels[0].length;
        byte[] fieldPixels = new byte[height * width * 2];
        int cursor = 0;

        for(int r = 0; r < height; r++) {
            for(int c = 0; c < width; c++) {
                long flux = this.testPatternPixels[r * 2 + field][c] * exposure / this.millisForFullExposure;
                flux = Long.min(flux, 0xffff);
                fieldPixels[cursor++] = (byte)(flux & 0xff); // lower byte
                fieldPixels[cursor++] = (byte)((flux >> 8) & 0xff); // higher byte
            }
        }

        return fieldPixels;
    }

    @Override
    public int bulkTransfer(IDeviceHandle handle, byte endpoint, ByteBuffer data, long timeout) {
        boolean input = (endpoint & 0x80) != 0;

        if(input) {
            try {
                byte[] rawData = this.outputBuffer.take();
                data.put(rawData);
                data.rewind();
                return rawData.length;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            data.rewind();
            byte[] rawData = new byte[data.remaining()];
            data.get(rawData);
            byte commandCode = rawData[1];

            switch(commandCode) {
                case 0x01:
                    this.resetPixelsTimestamp = clock.getTime();
                    break;

                case 0x03:
                    int flags = rawData[3] << 8 | rawData[2];
                    long elapsed = clock.getTime() - this.resetPixelsTimestamp;

                    if((flags & CommandFlags.CCD_FLAGS_FIELD_EVEN.value) != 0) {
                        this.outputBuffer.add(this.getPixels(0, elapsed));
                    } else if((flags & CommandFlags.CCD_FLAGS_FIELD_ODD.value) != 0) {
                        this.outputBuffer.add(this.getPixels(1, elapsed));
                    }

                default:
            }
        }

        return 0;
    }
}
