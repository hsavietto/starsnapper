package starsnapper.camera;

import starsnapper.commands.*;
import starsnapper.usb.IDevice;
import starsnapper.usb.IDeviceHandle;
import starsnapper.usb.IUsbController;
import starsnapper.usb.UsbController;

import java.nio.ByteBuffer;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 03/09/2015
 */
public class Camera {

    protected IDevice cameraDevice;
    protected IDeviceHandle cameraHandle;
    protected IUsbController IUsbController;

    /**
     * Constructor
     */
    public Camera(IUsbController controller) {
        this.cameraDevice = null;
        this.cameraHandle = null;
        this.IUsbController = controller;
    }

    /**
     * Destructor
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        if(this.cameraHandle != null) {
            this.IUsbController.releaseInterface(this.cameraHandle, 0);
            this.IUsbController.close(this.cameraHandle);
        }

        super.finalize();
    }

    /**
     *
     * @throws RuntimeException
     */
    public void initCommunications() throws RuntimeException {
        IUsbController controller;

        try {
            controller = new UsbController();
        } catch(Exception e) {
            throw new RuntimeException("Error creating controller", e);
        }

        try {
            this.cameraDevice = controller.findDevice((short) 0x1278, (short) 0x0507);
        } catch(Exception e) {
            throw new RuntimeException("Error locating camera", e);
        }

        if(this.cameraDevice == null) {
            throw new RuntimeException("No compatible camera found");
        }

        this.cameraHandle = this.IUsbController.createDeviceHandle();
        this.IUsbController.open(this.cameraDevice, this.cameraHandle);
        this.IUsbController.claimInterface(this.cameraHandle, 0);
    }

    /**
     *
     * @param command
     * @return
     */
    public byte[] sendCommand(CameraCommand command) {
        byte[] dataBlock = command.getCommandDataBlock();
        ByteBuffer sendBuffer = ByteBuffer.allocate(dataBlock.length);
        sendBuffer.put(dataBlock);
        this.IUsbController.bulkTransfer(this.cameraHandle, (byte)0x01, sendBuffer, 0);
        int expectedReplyLength = command.getExpectedReplySize();

        if(expectedReplyLength > 0) {
            ByteBuffer receiveBuffer = ByteBuffer.allocate(expectedReplyLength);
            int received = this.IUsbController.bulkTransfer(this.cameraHandle, (byte)0x82, receiveBuffer, 0);

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
