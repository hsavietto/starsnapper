package starsnapper.treatment;

import nom.tam.fits.*;
import nom.tam.util.BufferedFile;
import org.junit.Test;
import starsnapper.camera.Camera;
import starsnapper.camera.ICamera;
import starsnapper.commands.*;
import starsnapper.usb.IUsbController;
import starsnapper.usb.MockClock;
import starsnapper.usb.TestPatternUsbController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 27/01/2016.
 */
public class TestPatternCameraTest {

    @Test
    public void testPatternGeneration() throws IOException, InterruptedException, FitsException {
        long fullExposureMillis = 1000;
        MockClock clock = new MockClock();
        IUsbController controller = new TestPatternUsbController(clock, "test_pattern.png", fullExposureMillis);
        ICamera camera = new Camera(controller);

        camera.initCommunications();
        camera.sendCommand(new Reset());

        final short width = 800;
        final short height = 300;
        long exposureTime = 500;
        long secondFieldDelay = 250;

        // clear the pixels and start the acquiring
        camera.sendCommand(new ClearPixels());
        clock.advanceClock(exposureTime);

        // obtain even field
        ReadPixels readEvenFieldCommand = new ReadPixels(width, height);
        readEvenFieldCommand.setFlag(CommandFlags.CCD_FLAGS_FIELD_EVEN);
        ReadPixelsReply readEvenReply = new ReadPixelsReply();
        readEvenReply.setData(camera.sendCommand(readEvenFieldCommand));

        clock.advanceClock(secondFieldDelay);

        // obtain odd field
        ReadPixels readOddFieldCommand = new ReadPixels(width, height);
        readOddFieldCommand.setFlag(CommandFlags.CCD_FLAGS_FIELD_ODD);
        ReadPixelsReply readOddReply = new ReadPixelsReply();
        readOddReply.setData(camera.sendCommand(readOddFieldCommand));
        byte[][] rawData = new byte[][] { readEvenReply.getRawImage(), readOddReply.getRawImage() };
        double[] normalization = { (double)fullExposureMillis / (double)exposureTime, (double)fullExposureMillis / (double)(exposureTime + secondFieldDelay) };

        final String pngFileName = "c:\\temp\\simulated_pattern.png";
        File pngFile = new File(pngFileName);
        OutputStream pngOut = new FileOutputStream(pngFile);
        PngSaver pngSaver = new PngSaver(width, height, rawData, normalization, pngOut);
        pngSaver.run();

        final String fitsFileName = "c:\\temp\\simulated_pattern.fits";
        RawToFloats floatsGenerator = new RawToFloats(width, height, 2);
        float[][] data = floatsGenerator.convertRawInterlacedToFloats(rawData, normalization);
        Fits fitsFile = new Fits();
        BasicHDU<?> dataHUD = FitsFactory.hduFactory(data);
        Header header = dataHUD.getHeader();
        header.addValue("EXPOSURE", exposureTime, "Exposure time (s)");
        fitsFile.addHDU(dataHUD);
        File file = new File(fitsFileName);
        BufferedFile bufferedFile = new BufferedFile(file, "rw");
        fitsFile.write(bufferedFile);
        bufferedFile.close();
    }
}
