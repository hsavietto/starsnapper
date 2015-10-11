package starsnapper.commands;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 11/10/2015.
 */
public class GetCCDParameters extends CameraCommand {
    @Override
    protected byte getCommandType() {
        return (byte)0xc0;
    }

    @Override
    protected byte getCommandCode() {
        return 0x08;
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
        return 17;
    }
}
