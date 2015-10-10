package starsnapper.usb;

import org.usb4java.LibUsbException;

import java.nio.ByteBuffer;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 10/10/2015.
 */
public interface IUsbController {

    /**
     * Probes the connected USB devices and returns the first device of the provided vendor ID
     *
     * @param vendorId the ID of the vendor
     * @return a USB Device object, null if no device is found
     * @throws LibUsbException
     */
    IDevice findDevice(short vendorId) throws LibUsbException;

    /**
     * Probes the connected USB devices and returns the one with the provided vendor ID and product ID
     *
     * @param vendorId the ID of the vendor
     * @param productId the  ID of the product
     * @return a USB device object, null if no device is found
     * @throws LibUsbException
     */
    IDevice findDevice(short vendorId, short productId) throws LibUsbException;

    /**
     *
     * @param handle
     * @param iface
     */
    void releaseInterface(IDeviceHandle handle, int iface);

    /**
     *
     * @param device
     * @param handle
     */
    void open(IDevice device, IDeviceHandle handle);

    /**
     *
     * @param handle
     */
    void close(IDeviceHandle handle);

    /**
     *
     * @return
     */
    IDeviceHandle createDeviceHandle();


    /**
     *
     * @param handle
     * @param iface
     */
    void claimInterface(IDeviceHandle handle, int iface);

    /**
     *
     * @param handle
     * @param endpoint
     * @param data
     * @param timeout
     * @return
     */
    int bulkTransfer(IDeviceHandle handle, byte endpoint, ByteBuffer data, long timeout);
}
