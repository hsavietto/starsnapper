package starsnapper.commands;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 05/09/2015
 */
public enum CommandFlags {
    CCD_FLAGS_FIELD_ODD((byte)1),         //Specify odd field for MX cameras
    CCD_FLAGS_FIELD_EVEN((byte)2),        //Specify even field for MX cameras
    CCD_FLAGS_NOBIN_ACCUM((byte)4),       //Don't accumulate charge if binning
    CCD_FLAGS_NOWIPE_FRAME((byte)8),      //Don't apply WIPE when clearing frame
    CCD_FLAGS_TDI((byte)32),              //Implement TDI (drift scan) operation
    CCD_FLAGS_NOCLEAR_FRAME((byte)64);    //Don't clear frame, even when asked

    public final byte value;

    CommandFlags(byte value) {
        this.value= value;
    }
}
