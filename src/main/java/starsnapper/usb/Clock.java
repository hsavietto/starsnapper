package starsnapper.usb;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 31/01/2016.
 */
public class Clock implements IClock {

    @Override
    public long getTime() {
        return System.currentTimeMillis();
    }
}
