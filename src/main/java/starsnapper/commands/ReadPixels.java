package starsnapper.commands;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 05/09/2015
 */
public class ReadPixels extends CameraCommand {

    protected short xOffset;
    protected short yOffset;
    protected short width;
    protected short height;

    public ReadPixels(short width, short height, short xOffset, short yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.width = width;
        this.height = height;
    }

    public ReadPixels(short width, short height) {
        this.xOffset = (short)0;
        this.yOffset = (short)0;
        this.width = width;
        this.height = height;
    }

    @Override
    protected byte getCommandType() {
        return (byte)0xC0;
    }

    @Override
    protected byte getCommandCode() {
        return 3;
    }

    @Override
    protected short getCommandValue() {
        return this.getFlagsValue();
    }

    @Override
    protected short getCommandIndex() {
        return 0;
    }

    @Override
    protected short getCommandLength() {
        return 10;
    }

    @Override
    protected byte[] getCommandTransportData() {
        byte[] data = new byte[10];
        this.setShortValue(data, 0, this.xOffset);
        this.setShortValue(data, 2, this.yOffset);
        this.setShortValue(data, 4, this.width);
        this.setShortValue(data, 6, this.height);
        data[8] = 1;
        data[9] = 1;
        return data;
    }

    @Override
    public int getExpectedReplySize() {
        return this.width * this.height * 2;
    }
}
