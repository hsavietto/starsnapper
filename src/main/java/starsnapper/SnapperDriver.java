package starsnapper;

import nom.tam.fits.*;
import nom.tam.util.BufferedFile;
import starsnapper.camera.ICamera;
import starsnapper.commands.*;
import starsnapper.treatment.PngSaver;
import starsnapper.treatment.RawToFloats;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 19/01/2016
 */
public class SnapperDriver {

    private final ICamera camera;
    private final boolean fits;
    private final boolean png;
    private final int numberOfImages;
    private final File outputPath;
    private final String fileNamePrefix;
    private final int expositionTime;
    private final int closedShutterTime;

    public SnapperDriver(
            ICamera camera, boolean fits, boolean png, int numberOfImages, File outputPath,
            String fileNamePrefix, int expositionTime, int closedShutterTime) {
        this.camera = camera;
        this.fits = fits;
        this.png = png;
        this.numberOfImages = numberOfImages;
        this.outputPath = outputPath;
        this.fileNamePrefix = fileNamePrefix;
        this.expositionTime = expositionTime;
        this.closedShutterTime = closedShutterTime;
    }

    public void captureFrames(final PrintStream printOut) throws IOException, InterruptedException {
        camera.initCommunications();
        camera.sendCommand(new Reset());

        GetCCDParametersReply ccdParameters = new GetCCDParametersReply();
        ccdParameters.setData(camera.sendCommand(new GetCCDParameters()));
        final short width = ccdParameters.getWidth();
        final short height = ccdParameters.getHeight();
        int imageIndex = 1;

        while(imageIndex != numberOfImages) {
            // clear the pixels and start the acquiring
            camera.sendCommand(new ClearPixels());
            long startCapture = System.currentTimeMillis();
            Thread.sleep(expositionTime);
            long endCapture = System.currentTimeMillis();

            // obtain even field
            ReadPixels readEvenFieldCommand = new ReadPixels(width, height);
            readEvenFieldCommand.setFlag(CommandFlags.CCD_FLAGS_FIELD_EVEN);
            ReadPixelsReply readEvenReply = new ReadPixelsReply();
            readEvenReply.setData(camera.sendCommand(readEvenFieldCommand));
            long elapsedEvenField = System.currentTimeMillis() - startCapture;

            // obtain odd field
            ReadPixels readOddFieldCommand = new ReadPixels(width, height);
            readOddFieldCommand.setFlag(CommandFlags.CCD_FLAGS_FIELD_ODD);
            ReadPixelsReply readOddReply = new ReadPixelsReply();
            readOddReply.setData(camera.sendCommand(readOddFieldCommand));
            long elapsedOddField = System.currentTimeMillis() - startCapture;
            final double exposureTime = ((double)elapsedOddField + (double)elapsedEvenField) / 2000.0;

            final byte[][] rawData = new byte[][] { readEvenReply.getRawImage(), readOddReply.getRawImage() };
            final int counter = imageIndex;

            if(png) {
                final String fileName = fileNamePrefix + "_" + counter + ".png";
                printOut.println("Saving " + fileName + "...");
                File file = new File(outputPath, fileName);
                OutputStream out = new FileOutputStream(file);
                Thread pngSavingThread = new Thread(new PngSaver(width, height, rawData, out));
                pngSavingThread.start();
            }

            if(fits) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
                Date now = new Date();
                final String dateEnd = dateFormat.format(now) + "T" + timeFormat.format(now);
                final String fileName = fileNamePrefix + "_" + counter + ".fits";
                printOut.println("Saving " + fileName + "...");

                Thread fitsSavingThread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            RawToFloats floatsGenerator = new RawToFloats(width, height, 2);
                            float[][] data = floatsGenerator.convertRawInterlacedToFloats(rawData);
                            Fits fitsFile = new Fits();
                            BasicHDU<?> dataHUD = FitsFactory.hduFactory(data);
                            Header header = dataHUD.getHeader();
                            header.addValue("EXPOSURE", exposureTime, "Exposure time (s)");
                            header.addValue("DATE-END", dateEnd, "Observation timestamp");
                            fitsFile.addHDU(dataHUD);
                            File file = new File(outputPath, fileName);
                            BufferedFile bufferedFile = new BufferedFile(file, "rw");
                            fitsFile.write(bufferedFile);
                            bufferedFile.close();
                        } catch (FitsException | IOException e) {
                            e.printStackTrace(printOut);
                        }
                    }
                });

                fitsSavingThread.start();
            }

            imageIndex++;
            Thread.sleep(closedShutterTime - (System.currentTimeMillis() - endCapture));
        }
    }
}
