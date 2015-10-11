package starsnapper.commands;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 11/10/2015.
 */
public class GetCCDParametersReply extends CameraReply {

    @Override
    public byte getCommandCode() {
        return 0x08;
    }

    public byte getHorizontalFrontPorch() {
        // offset 0
        return getByte(0);
    }

    public byte getHorizontalBackPorch() {
        // offset 1
        return getByte(1);
    }

    public short getWidth() {
        // offset 2
        return getShort(2);
    }

    public byte getVerticalFrontPorch() {
        // offset 4
        return getByte(4);
    }

    public byte getVerticalBackPorch() {
        // offset 5
        return getByte(5);
    }

    public short getHeight() {
        // offset 6
        return getShort(6);
    }

    public short getPixelWidth() {
        // offset 8
        return getShort(8);
    }

    public short getPixelHeight() {
        // offset 10
        return getShort(8);
    }

    public short getColorMatrix() {
        // offset 12
        return getShort(12);
    }

    public byte bitsPerPixel() {
        // offset 14
        return getByte(14);
    }

    public byte getNumberSerialPorts() {
        // offset 15
        return getByte(15);
    }

    public byte getExtraCapabilities() {
        // offset 16
        return getByte(16);
    }
}
