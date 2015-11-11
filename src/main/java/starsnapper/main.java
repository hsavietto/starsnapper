package starsnapper;

import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.FitsFactory;
import nom.tam.util.BufferedFile;
import org.apache.commons.cli.*;
import starsnapper.camera.Camera;
import starsnapper.commands.*;
import starsnapper.treatment.GrayscalePngGenerator;
import starsnapper.treatment.RawToFloats;
import starsnapper.treatment.RawToGrayscalePixels;
import starsnapper.usb.IUsbController;
import starsnapper.usb.UsbController;

import java.io.*;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 03/09/2015
 */
public class main {

    public static void printUsage(
            final String applicationName,
            final Options options,
            final OutputStream out)
    {
        final PrintWriter writer = new PrintWriter(out);
        final HelpFormatter usageFormatter = new HelpFormatter();
        usageFormatter.printUsage(writer, 80, applicationName, options);
        writer.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
        Options options = new Options();
        options.addOption("f", "format", true, "Output format <fits/png/both>");
        options.addOption("n", "number", true, "Number of images (0 for infinity)");
        options.addOption("o", "output", true, "Output path");
        options.addOption("p", "prefix", true, "File name prefix");
        options.addOption("e", "exposition", true, "Exposition in milliseconds");

        CommandLineParser parser = new PosixParser();
        CommandLine commandLine = parser.parse(options, args);

        if(!commandLine.hasOption("f") || !commandLine.hasOption("n") ||
                !commandLine.hasOption("o") || !commandLine.hasOption("p") ||
                !commandLine.hasOption("e")) {
            printUsage("starsnapper", options, System.out);
            return;
        }

        IUsbController controller = new UsbController();
        Camera camera = new Camera(controller);
        camera.initCommunications();
        camera.sendCommand(new Reset());

        GetCCDParametersReply ccdParameters = new GetCCDParametersReply();
        ccdParameters.setData(camera.sendCommand(new GetCCDParameters()));
        final short width = ccdParameters.getWidth();
        final short height = ccdParameters.getHeight();

        long start = System.currentTimeMillis();

        boolean fits = false;
        boolean png = false;
        String formats = commandLine.getOptionValue("f");

        if("fits".equalsIgnoreCase(formats) || "both".equalsIgnoreCase(formats)) {
            fits = true;
        }

        if("png".equalsIgnoreCase(formats) || "both".equalsIgnoreCase(formats)) {
            png = true;
        }

        int numberOfImages = Integer.parseInt(commandLine.getOptionValue("n"));
        int imageIndex = 0;

        String outputPathValue = commandLine.getOptionValue("o");
        final File outputPath = new File(outputPathValue);
        final String fileNamePrefix = commandLine.getOptionValue("p");

        int expositionTime = Integer.parseInt(commandLine.getOptionValue("e"));

        while(imageIndex != numberOfImages) {
            // clear the pixels and start the acquiring
            camera.sendCommand(new ClearPixels());
            Thread.sleep(expositionTime);

            // obtain even field
            ReadPixels readEvenFieldCommand = new ReadPixels(width, height);
            readEvenFieldCommand.setFlag(CommandFlags.CCD_FLAGS_FIELD_EVEN);
            ReadPixelsReply readEvenReply = new ReadPixelsReply();
            readEvenReply.setData(camera.sendCommand(readEvenFieldCommand));

            // obtain odd field
            ReadPixels readOddFieldCommand = new ReadPixels(width, height);
            readOddFieldCommand.setFlag(CommandFlags.CCD_FLAGS_FIELD_ODD);
            ReadPixelsReply readOddReply = new ReadPixelsReply();
            readOddReply.setData(camera.sendCommand(readOddFieldCommand));

            final byte[][] rawData = new byte[][] { readEvenReply.getRawImage(), readOddReply.getRawImage() };
            final int counter = imageIndex;

            if(png) {
                final String fileName = fileNamePrefix + "_" + counter + ".png";
                System.out.println("Saving " + fileName + "...");

                Thread pngSavingThread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            RawToGrayscalePixels pixelsGenerator = new RawToGrayscalePixels(width, height, 2);
                            int[] pixels = pixelsGenerator.convertRawInterlacedToGrayscalePixels(rawData);
                            GrayscalePngGenerator pngGenerator = new GrayscalePngGenerator(width, height * 2, 16);

                            File file = new File(outputPath, fileName);
                            OutputStream out = new FileOutputStream(file);
                            pngGenerator.writePixelsToStream(pixels, out);
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                pngSavingThread.start();
            }

            if(fits) {
                final String fileName = fileNamePrefix + "_" + counter + ".fits";
                System.out.println("Saving " + fileName + "...");

                Thread fitsSavingThread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            RawToFloats floatsGenerator = new RawToFloats(width, height, 2);
                            float[][] data = floatsGenerator.convertRawInterlacedToFloats(rawData);
                            Fits fitsFile = new Fits();
                            fitsFile.addHDU(FitsFactory.hduFactory(data));
                            File file = new File(outputPath, fileName);
                            BufferedFile bufferedFile = new BufferedFile(file, "rw");
                            fitsFile.write(bufferedFile);
                            bufferedFile.close();
                        } catch (FitsException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                fitsSavingThread.start();
            }

            imageIndex++;
        }

        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("Elapsed time: " + elapsed + " ms");
    }
}
