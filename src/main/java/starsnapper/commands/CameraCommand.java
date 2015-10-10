package starsnapper.commands;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 06/09/2015
 */
public abstract class CameraCommand {

    protected Set<CommandFlags> flags;

    /**
     * Gets the command type
     * @see Class-specific requests in http://www.usb.org/developers/docs/devclass_docs/usb_msc_cbi_1.1.pdf
     *
     * @return the command type
     */
    protected abstract byte getCommandType();

    /**
     * Gets the command code
     * @see Class-specific requests in http://www.usb.org/developers/docs/devclass_docs/usb_msc_cbi_1.1.pdf
     *
     * @return the command code
     */
    protected abstract byte getCommandCode();

    /**
     * Gets the command value
     * @see Class-specific requests in http://www.usb.org/developers/docs/devclass_docs/usb_msc_cbi_1.1.pdf
     *
     * @return the command value
     */
    protected abstract short getCommandValue();

    /**
     * Gets the command index
     * @see Class-specific requests in http://www.usb.org/developers/docs/devclass_docs/usb_msc_cbi_1.1.pdf
     *
     * @return the command index
     */
    protected abstract short getCommandIndex();

    /**
     * Gets the command data block length
     * @see Class-specific requests in http://www.usb.org/developers/docs/devclass_docs/usb_msc_cbi_1.1.pdf
     *
     * @return the command data block length
     */
    protected abstract short getCommandLength();

    /**
     * Gets the command transport data
     * @see Class-specific requests in http://www.usb.org/developers/docs/devclass_docs/usb_msc_cbi_1.1.pdf
     *
     * @return the command transport data
     */
    protected abstract byte[] getCommandTransportData();

    /**
     * Gets the expected reply size. This is used to read the reply from the camera
     *
     * @return the expected reply size
     */
    public abstract int getExpectedReplySize();

    /**
     * Constructor
     */
    public CameraCommand() {
        this.flags = new HashSet<CommandFlags>();
    }

    /**
     * Turns a flag on. These flags are camera-specific
     *
     * @param flag the flag to be turned on
     */
    public void setFlag(CommandFlags flag) {
        this.flags.add(flag);
    }

    /**
     * Turns a flag off. These flags are camera-specific
     *
     * @param flag the flag to be turned off
     */
    public void resetFlag(CommandFlags flag) {
        this.flags.remove(flag);
    }

    /**
     * Gets the flags as bits in a byte
     *
     * @return the byte with the consolidated flags
     */
    protected byte getFlagsValue() {
        byte value = 0;

        for(Iterator<CommandFlags> it = this.flags.iterator(); it.hasNext(); ) {
            value |= it.next().value;
        }

        return value;
    }

    /**
     * Returns the lower byte of a short
     *
     * @param value the value
     * @return the lower byte
     */
    protected byte getLowValue(short value) {
        return (byte)(value & 0xff);
    }

    /**
     * Returns the higher byte of a short
     *
     * @param value the value
     * @return the higher byte
     */
    protected byte getHighValue(short value) {
        return (byte)((value >> 8) & 0xff);
    }

    /**
     * Builds and return the command data block to be sent as a bulk transfer
     *
     * @return byte array with the raw data to be sent
     */
    public byte[] getCommandDataBlock() {
        short commandLength = this.getCommandLength();
        byte[] block = new byte[8 + commandLength];

        block[0] = this.getCommandType();
        block[1] = this.getCommandCode();
        short commandValue = this.getCommandValue();
        block[2] = this.getLowValue(commandValue);
        block[3] = this.getHighValue(commandValue);
        short commandIndex = this.getCommandIndex();
        block[4] = this.getLowValue(commandIndex);
        block[5] = this.getHighValue(commandIndex);
        block[6] = this.getLowValue(commandLength);
        block[7] = this.getHighValue(commandLength);

        if(commandLength > 0) {
            byte[] commandParameters = this.getCommandTransportData();

            for(short i = 0; i < commandLength; i++) {
                block[i + 8] = commandParameters[i];
            }
        }

        return block;
    }
}
