package starsnapper.commands;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 05/09/2015
 */
public class ReadPixelsDelayed extends CameraCommand {
    @Override
    protected byte getCommandType() {
        return (byte)0xC0;
    }

    @Override
    protected byte getCommandCode() {
        return 2;
    }

    @Override
    protected short getCommandValue() {
        return 0;
    }

    @Override
    protected short getCommandIndex() {
        return 0;
    }

    @Override
    protected short getCommandLength() {
        return 14;
    }

    @Override
    protected byte[] getCommandTransportData() {
        return new byte[] { 0, 0, 0, 0, (byte)100, 0, (byte)100, 0,  1, 1, 50, 0, 0, 0 };
    }

    @Override
    public int getExpectedReplySize() {
        return 0;
    }
}
