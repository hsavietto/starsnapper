package starsnapper.camera;

import starsnapper.commands.CameraCommand;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 19/01/2016.
 */
public interface ICamera {
    void initCommunications() throws RuntimeException;

    byte[] sendCommand(CameraCommand command);
}
