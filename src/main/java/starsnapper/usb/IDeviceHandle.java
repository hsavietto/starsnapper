package starsnapper.usb;

/**
 * Interface for usb4java.DeviceHandle
 *
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 08/10/2015.
 */
public interface IDeviceHandle {
    long getPointer();

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);

    @Override
    String toString();
}
