package starsnapper.usb;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 31/01/2016.
 */
public class MockClock implements IClock {

    private long currentTime;

    @Override
    public long getTime() {
        return this.currentTime;
    }

    public void advanceClock(long millis) {
        this.currentTime += millis;
    }
}
