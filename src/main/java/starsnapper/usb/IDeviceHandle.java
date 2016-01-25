package starsnapper.usb;

/**
 * Interface for usb4java.DeviceHandle
 *
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 08/10/2015.
 */
public interface IDeviceHandle {
    /**
     * Returns the native pointer to the device handle structure.
     *
     * @return The native pointer to the device handle structure.
     */
    long getPointer();

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);

    @Override
    String toString();
}
