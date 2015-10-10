package starsnapper;

import starsnapper.camera.Camera;
import starsnapper.usb.IUsbController;
import starsnapper.usb.UsbController;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 03/09/2015
 */
public class main {

    public static void main(String[] args) {
        IUsbController controller = new UsbController();
        Camera camera = new Camera(controller);
        camera.initCommunications();
    }
}
