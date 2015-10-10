package starsnapper.usb;

import org.usb4java.*;

/**
 * Wrapper class to usb4java.Device, to be able to create an interface for it
 *
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 08/10/2015.
 */
public class DeviceWrapper implements IDevice {

    private Device device;

    public DeviceWrapper(Device device) {
        this.device = device;
    }

    public long getPointer() {
        return device.getPointer();
    }

    public int hashCode() {
        return this.device.hashCode();
    }

    public boolean equals(final Object obj) {
        return this.device.equals(obj);
    }

    public String toString() {
        return this.device.toString();
    }

    public Device get() {
        return this.device;
    }
}
