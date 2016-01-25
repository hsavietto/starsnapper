package starsnapper.camera;

import starsnapper.commands.*;
import starsnapper.usb.IDevice;
import starsnapper.usb.IDeviceHandle;
import starsnapper.usb.IUsbController;

import java.nio.ByteBuffer;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 03/09/2015
 */
public class Camera implements ICamera {

    private IDevice cameraDevice;
    private IDeviceHandle cameraHandle;
    private final IUsbController usbController;

    /**
     * Constructor
     */
    public Camera(IUsbController controller) {
        this.cameraDevice = null;
        this.cameraHandle = null;
        this.usbController = controller;
    }

    /**
     * Destructor
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        if(this.cameraHandle != null) {
            this.usbController.releaseInterface(this.cameraHandle, 0);
            this.usbController.close(this.cameraHandle);
        }

        super.finalize();
    }

    /**
     *
     * @throws RuntimeException
     */
    @Override
    public void initCommunications() throws RuntimeException {
        try {
            this.cameraDevice = usbController.findDevice((short) 0x1278, (short) 0x0507);
        } catch(Exception e) {
            throw new RuntimeException("Error locating camera", e);
        }

        if(this.cameraDevice == null) {
            throw new RuntimeException("No compatible camera found");
        }

        this.cameraHandle = this.usbController.createDeviceHandle();
        this.usbController.open(this.cameraDevice, this.cameraHandle);
        this.usbController.claimInterface(this.cameraHandle, 0);
    }

    /**
     *
     * @param command
     * @return
     */
    @Override
    public byte[] sendCommand(CameraCommand command) {
        byte[] dataBlock = command.getCommandDataBlock();
        ByteBuffer sendBuffer = ByteBuffer.allocateDirect(dataBlock.length);
        sendBuffer.put(dataBlock);
        this.usbController.bulkTransfer(this.cameraHandle, (byte)0x01, sendBuffer, 0);
        int expectedReplyLength = command.getExpectedReplySize();

        if(expectedReplyLength > 0) {
            ByteBuffer receiveBuffer = ByteBuffer.allocateDirect(expectedReplyLength);
            int received = this.usbController.bulkTransfer(this.cameraHandle, (byte)0x82, receiveBuffer, 0);

            if(received != expectedReplyLength) {
                System.err.println("Warning: expected " + expectedReplyLength + " bytes, got " + received);
            }

            byte[] receivedDataBlock = new byte[received];
            receiveBuffer.get(receivedDataBlock);
            return receivedDataBlock;
        } else {
            return null;
        }
    }
}
