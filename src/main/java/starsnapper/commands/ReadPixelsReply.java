package starsnapper.commands;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 18/10/2015.
 */
public class ReadPixelsReply extends CameraReply {

    public byte[] getRawImage() {
        return this.receivedData;
    }

    public boolean isInterlaced() {
        return true;
    }

}
