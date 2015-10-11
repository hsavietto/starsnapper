package starsnapper.commands;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 11/10/2015.
 */
public abstract class CameraReply {

    protected byte[] receivedData;

    public void setData(byte[] data) {
        this.receivedData = data;
    }

    protected byte getByte(int offset) {
        return this.receivedData[offset];
    }

    protected short getShort(int offset) {
        return (short)(((this.receivedData[offset + 1] & 0xFF) << 8) | (this.receivedData[offset] & 0xFF));
    }

    public abstract byte getCommandCode();
}
