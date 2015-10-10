package starsnapper.usb;

/**
 * Interface for usb4java.Device
 *
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 08/10/2015.
 */
public interface IDevice {

    /**
     * Returns the native pointer to the device structure.
     *
     * @return The native pointer to the device structure.
     */
    long getPointer();

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);

    @Override
    String toString();
}
