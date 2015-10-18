package starsnapper.commands;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(receivedData[offset]);
        bb.put(receivedData[offset + 1]);
        bb.put((byte)0);
        bb.put((byte)0);
        return (short)(bb.getInt(0));
    }

    public abstract byte getCommandCode();
}
