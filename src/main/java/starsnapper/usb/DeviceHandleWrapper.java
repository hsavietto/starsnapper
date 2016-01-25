package starsnapper.usb;

import org.usb4java.DeviceHandle;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 08/10/2015.
 */
public class DeviceHandleWrapper implements IDeviceHandle {

    private final DeviceHandle deviceHandle;

    /**
     *
     * @param deviceHandle
     */
    public DeviceHandleWrapper(DeviceHandle deviceHandle) {
        this.deviceHandle = deviceHandle;
    }

    @Override
    public long getPointer() {
        return this.deviceHandle.getPointer();
    }

    public int hashCode() {
        return this.deviceHandle.hashCode();
    }

    public boolean equals(final Object obj) {
        return this.deviceHandle.equals(obj);
    }

    public String toString() {
        return this.deviceHandle.toString();
    }

    public DeviceHandle get() {
        return this.deviceHandle;
    }
}
