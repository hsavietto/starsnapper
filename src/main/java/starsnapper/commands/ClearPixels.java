package starsnapper.commands;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 19/10/2015.
 */
public class ClearPixels extends CameraCommand {
    @Override
    protected byte getCommandType() {
        return 0x40;
    }

    @Override
    protected byte getCommandCode() {
        return 0x01;
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
        return 0;
    }

    @Override
    protected byte[] getCommandTransportData() {
        return new byte[0];
    }

    @Override
    public int getExpectedReplySize() {
        return 0;
    }
}
