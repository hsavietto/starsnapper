package starsnapper;

import org.apache.commons.cli.*;
import starsnapper.camera.Camera;
import starsnapper.commands.*;
import starsnapper.usb.IUsbController;
import starsnapper.usb.UsbController;

import java.io.*;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 03/09/2015
 */
public class main {

    private static void printUsage(
            final String applicationName,
            final Options options,
            final OutputStream out)
    {
        final PrintWriter writer = new PrintWriter(out);
        final HelpFormatter usageFormatter = new HelpFormatter();
        usageFormatter.printUsage(writer, 80, applicationName, options);
        writer.close();
    }

    private static String getArgumentValueOrDefault(CommandLine commandLine, String argumentName, String defaultValue) {
        if(commandLine.hasOption(argumentName)) {
            return commandLine.getOptionValue(argumentName);
        }

        return defaultValue;
    }

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
        Options options = new Options();
        options.addOption("h", "help", false, "Program usage");
        options.addOption("f", "format", true, "Output format <fits (default)/png/both>");
        options.addOption("n", "number", true, "Number of images (0 for infinity, default)");
        options.addOption("o", "output", true, "Output path (current path default)");
        options.addOption("p", "prefix", true, "File name prefix (\"snap\" default)");
        options.addOption("e", "exposition", true, "Exposition in milliseconds (500 default)");
        options.addOption("c", "closed", true, "Closed shutter time in milliseconds (1500 default)");
        options.addOption("m", "mode", true, "Program mode <snap (default)/focus>");

        CommandLineParser parser = new PosixParser();
        CommandLine commandLine = parser.parse(options, args);

        if(commandLine.hasOption("h")) {
            printUsage("starsnapper", options, System.out);
            return;
        }

        long start = System.currentTimeMillis();

        String formats = getArgumentValueOrDefault(commandLine, "f", "fits");
        boolean fits = "fits".equalsIgnoreCase(formats) || "both".equalsIgnoreCase(formats);
        boolean png = "png".equalsIgnoreCase(formats) || "both".equalsIgnoreCase(formats);

        int numberOfImages = Integer.parseInt(getArgumentValueOrDefault(commandLine, "n", "0"));

        if(numberOfImages == 0) {
            numberOfImages = -1;
        }

        String outputPathValue = getArgumentValueOrDefault(commandLine, "o", ".");
        final File outputPath = new File(outputPathValue);
        final String fileNamePrefix = getArgumentValueOrDefault(commandLine, "p", "snap");
        int expositionTime = Integer.parseInt(getArgumentValueOrDefault(commandLine, "e", "500"));
        int closedTime = Integer.parseInt(getArgumentValueOrDefault(commandLine, "c", "1500"));

        IUsbController controller = new UsbController();
        Camera camera = new Camera(controller);
        camera.initCommunications();
        camera.sendCommand(new Reset());

        SnapperDriver driver = new SnapperDriver(
                camera, fits, png, numberOfImages, outputPath,
                fileNamePrefix, expositionTime, closedTime);

        driver.captureFrames(System.out);

        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("Elapsed time: " + elapsed + " ms");
    }
}
