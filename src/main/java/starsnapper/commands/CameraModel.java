package starsnapper.commands;

/**
 * @auhtor Helder Savietto (helder.savietto@gmail.com)
 * @date  05/09/2015
 */
public class CameraModel extends CameraCommand {
    @Override
    protected byte getCommandType() {
        return (byte)0xC0;
    }

    @Override
    protected byte getCommandCode() {
        return 14;
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
        return 2;
    }

    @Override
    protected byte[] getCommandTransportData() {
        return new byte[] {0, 0};
    }

    @Override
    public int getExpectedReplySize() {
        return 2;
    }
}
