package starsnapper.commands;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 05/09/2015
 */
public class CommandTest {

    protected class MockCameraCommand extends CameraCommand {

        @Override
        protected byte getCommandType() {
            return 0;
        }

        @Override
        protected byte getCommandCode() {
            return 1;
        }

        @Override
        protected short getCommandValue() {
            return 0x4321;
        }

        @Override
        protected short getCommandIndex() {
            return 0x1234;
        }

        @Override
        protected short getCommandLength() {
            return 4;
        }

        @Override
        protected byte[] getCommandTransportData() {
            byte[] parameters = new byte[this.getCommandLength()];

            for(int i = 0; i < parameters.length; i++) {
                parameters[i] = (byte)(5 + i);
            }

            return parameters;
        }

        @Override
        public int getExpectedReplySize() {
            return 4;
        }
    }

    @Test
    public void testCameraCommand() {
        CameraCommand command = new MockCameraCommand();
        command.setFlag(CommandFlags.CCD_FLAGS_FIELD_EVEN);
        command.setFlag(CommandFlags.CCD_FLAGS_TDI);

        assertEquals(
                CommandFlags.CCD_FLAGS_FIELD_EVEN.value + CommandFlags.CCD_FLAGS_TDI.value,
                command.getFlagsValue()
        );

        byte[] dataBlock = command.getCommandDataBlock();

        assertEquals(12, dataBlock.length);
        assertEquals(0, dataBlock[0]);
        assertEquals(1, dataBlock[1]);
        assertEquals(0x21, dataBlock[2]);
        assertEquals(0x43, dataBlock[3]);
        assertEquals(0x34, dataBlock[4]);
        assertEquals(0x12, dataBlock[5]);
        assertEquals(0x04, dataBlock[6]);
        assertEquals(0x00, dataBlock[7]);
        assertEquals(5, dataBlock[8]);
        assertEquals(6, dataBlock[9]);
        assertEquals(7, dataBlock[10]);
        assertEquals(8, dataBlock[11]);
    }

    @Test
    public void testReset() {
        CameraCommand command = new Reset();
        byte[] dataBlock = command.getCommandDataBlock();

        assertEquals(8, dataBlock.length);
        assertEquals(0x40, dataBlock[0]);
        assertEquals(6, dataBlock[1]);
        assertEquals(0, dataBlock[2]);
        assertEquals(0, dataBlock[3]);
        assertEquals(0, dataBlock[4]);
        assertEquals(0, dataBlock[5]);
        assertEquals(0, dataBlock[6]);
        assertEquals(0, dataBlock[7]);
    }
}
