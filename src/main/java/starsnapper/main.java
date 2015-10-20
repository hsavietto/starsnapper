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

        short width = ccdParameters.getWidth();
        short height = ccdParameters.getHeight();

        // clear the pixels and start the acquiring
        camera.sendCommand(new ClearPixels());
        Thread.sleep(450);

        byte[][] rawData = new byte[2][];

        // obtain even field
        ReadPixels readEvenFieldCommand = new ReadPixels(width, height);
        readEvenFieldCommand.setFlag(CommandFlags.CCD_FLAGS_FIELD_EVEN);
        ReadPixelsReply readEvenReply = new ReadPixelsReply();
        readEvenReply.setData(camera.sendCommand(readEvenFieldCommand));
        rawData[0] = readEvenReply.getRawImage();

        // obtain odd field
        ReadPixels readOddFieldCommand = new ReadPixels(width, height);
        readOddFieldCommand.setFlag(CommandFlags.CCD_FLAGS_FIELD_ODD);
        ReadPixelsReply readOddReply = new ReadPixelsReply();
        readOddReply.setData(camera.sendCommand(readOddFieldCommand));
        rawData[1] = readEvenReply.getRawImage();

        RawToGrayscalePixels pixelsGenerator = new RawToGrayscalePixels(width, height, 2);
        int[] pixels = pixelsGenerator.convertRawInterlacedToGrayscalePixels(rawData);
        GrayscalePngGenerator pngGenerator = new GrayscalePngGenerator(width, height * 2, 16);
        OutputStream out = new FileOutputStream("c:\\temp\\camera.png");
        pngGenerator.writePixelsToStream(pixels, out);
        out.close();
    }
}
