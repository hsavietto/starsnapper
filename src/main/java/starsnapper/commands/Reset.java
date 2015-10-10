package starsnapper.commands;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 05/09/2015
 */
public class Reset extends CameraCommand {

    @Override
    protected byte getCommandType() {
        return 0x40;
    }

    @Override
    protected byte getCommandCode() {
        return 6;
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
