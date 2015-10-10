package starsnapper.usb;

import org.usb4java.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 05/09/2015
 */
public class UsbController implements IUsbController {

    /**
     * Constructor
     */
    public UsbController() {
        LibUsb.init(null);
    }

    /**
     * Destructor
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        LibUsb.exit(null);
        super.finalize();
    }

    /**
     *
     * @param vendorId
     * @param productId
     * @return
     * @throws LibUsbException
     */
    protected IDevice internalFindDevice(short vendorId, short productId) throws LibUsbException {
        IDevice device = null;
        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(null, list);

        if (result < 0) {
            throw new LibUsbException("Unable to get device list", result);
        }

        try {
            // Iterate over all devices and scan for the right one
            for (Device dev: list) {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(dev, descriptor);

                if (result != LibUsb.SUCCESS) {
                    throw new LibUsbException("Unable to read device descriptor", result);
                }

                if((vendorId == descriptor.idVendor()) && (productId == -1 || productId == descriptor.idProduct())) {
                    device = new DeviceWrapper(dev);
                    break;
                }
            }
        }
        finally {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, false);
        }

        return device;
    }

    public IDevice findDevice(short vendorId) throws LibUsbException {
        return this.internalFindDevice(vendorId, (short)-1);
    }

    public IDevice findDevice(short vendorId, short productId) throws LibUsbException {
        return this.internalFindDevice(vendorId, productId);
    }

    public void releaseInterface(final IDeviceHandle handle, final int iface) {
        int result = LibUsb.releaseInterface(((DeviceHandleWrapper) handle).get(), iface);

        if (result != LibUsb.SUCCESS) {
            throw new RuntimeException(new LibUsbException("Unable to release interface", result));
        }
    }

    public void open(final IDevice device, final IDeviceHandle handle) {
        int result = LibUsb.open(((DeviceWrapper) device).get(), ((DeviceHandleWrapper) handle).get());

        if (result != LibUsb.SUCCESS) {
            throw new RuntimeException(new LibUsbException("Error opening device", result));
        }
    }

    public void close(final IDeviceHandle handle) {
        LibUsb.close(((DeviceHandleWrapper)handle).get());
    }

    public IDeviceHandle createDeviceHandle() {
        return new DeviceHandleWrapper(new DeviceHandle());
    }

    public void claimInterface(final IDeviceHandle handle, final int iface) {
        int result = LibUsb.claimInterface(((DeviceHandleWrapper)handle).get(), iface);

        if(result != LibUsb.SUCCESS) {
            throw new RuntimeException(new LibUsbException("Unable to claim interface", result));
        }
    }

    public int bulkTransfer(final IDeviceHandle handle, final byte endpoint, final ByteBuffer data, final long timeout) {
        IntBuffer transferred = IntBuffer.allocate(1);
        int result = LibUsb.bulkTransfer(((DeviceHandleWrapper)handle).get(), endpoint, data, transferred, timeout);

        if(result != LibUsb.SUCCESS) {
            throw new RuntimeException(new LibUsbException("Error sending data", result));
        }

        return transferred.get();
    }
}
