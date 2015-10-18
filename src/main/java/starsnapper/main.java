package starsnapper;

import starsnapper.camera.Camera;
import starsnapper.commands.GetCCDParameters;
import starsnapper.commands.GetCCDParametersReply;
import starsnapper.commands.Reset;
import starsnapper.usb.IUsbController;
import starsnapper.usb.UsbController;

import java.io.IOException;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 03/09/2015
 */
public class main {

    public static void main(String[] args) throws IOException {
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
    }
}
