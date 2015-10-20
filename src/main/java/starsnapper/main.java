package starsnapper;

import starsnapper.camera.Camera;
import starsnapper.commands.*;
import starsnapper.treatment.GrayscalePngGenerator;
import starsnapper.treatment.RawToGrayscalePixels;
import starsnapper.usb.IUsbController;
import starsnapper.usb.UsbController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 03/09/2015
 */
public class main {

    public static void main(String[] args) throws IOException, InterruptedException {
        IUsbController controller = new UsbController();
        Camera camera = new Camera(controller);
        camera.initCommunications();
        camera.sendCommand(new Reset());

        GetCCDParametersReply ccdParameters = new GetCCDParametersReply();
        ccdParameters.setData(camera.sendCommand(new GetCCDParameters()));
        System.out.println("Horizontal front porch = " + ccdParameters.getHorizontalFrontPorch());
        System.out.println("Horizontal back porch = " + ccdParameters.getHorizontalBackPorch());
        System.out.println("Width = " + ccdParameters.getWidth());
        System.out.println("Vertical front porch = " + ccdParameters.getVerticalFrontPorch());
        System.out.println("Vertical back porch = " + ccdParameters.getVerticalBackPorch());
        System.out.println("Height = " + ccdParameters.getHeight());
        System.out.println("Pixel width = " + ccdParameters.getPixelWidth());
        System.out.println("Pixel height = " + ccdParameters.getPixelHeight());
        System.out.println("Color matrix = " + ccdParameters.getColorMatrix());
        System.out.println("Bits per pixel = " + ccdParameters.bitsPerPixel());
        System.out.println("Number of serial ports = " + ccdParameters.getNumberSerialPorts());
        System.out.println("Extra capabilities = " + ccdParameters.getExtraCapabilities());

        final short width = ccdParameters.getWidth();
        final short height = ccdParameters.getHeight();

        long start = System.currentTimeMillis();
        long totalSleeping = 0;
        long totalSaving = 0;

        for(int i = 0; i < 20; i++) {
            // clear the pixels and start the acquiring
            camera.sendCommand(new ClearPixels());
            Thread.sleep(500);

            // obtain even field
            ReadPixels readEvenFieldCommand = new ReadPixels(width, height);
            readEvenFieldCommand.setFlag(CommandFlags.CCD_FLAGS_FIELD_EVEN);
            ReadPixelsReply readEvenReply = new ReadPixelsReply();
            readEvenReply.setData(camera.sendCommand(readEvenFieldCommand));

            // obtain odd field
            ReadPixels readOddFieldCommand = new ReadPixels(width, height);
            readOddFieldCommand.setFlag(CommandFlags.CCD_FLAGS_FIELD_ODD);
            ReadPixelsReply readOddReply = new ReadPixelsReply();
            readOddReply.setData(camera.sendCommand(readOddFieldCommand));

            final byte[][] rawData = new byte[][] { readEvenReply.getRawImage(), readEvenReply.getRawImage() };
            final int counter = i;

            Thread savingThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        RawToGrayscalePixels pixelsGenerator = new RawToGrayscalePixels(width, height, 2);
                        int[] pixels = pixelsGenerator.convertRawInterlacedToGrayscalePixels(rawData);
                        GrayscalePngGenerator pngGenerator = new GrayscalePngGenerator(width, height * 2, 16);
                        OutputStream out = new FileOutputStream("c:\\temp\\camera_" + counter + ".png");
                        pngGenerator.writePixelsToStream(pixels, out);
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            savingThread.start();
        }

        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("Elapsed time: " + elapsed + " ms");
        System.out.println("Sleeping time: " + totalSleeping + " ms");
        System.out.println("Saving time: " + totalSaving + " ms");
    }
}
