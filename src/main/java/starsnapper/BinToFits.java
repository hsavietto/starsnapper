package starsnapper;

import nom.tam.fits.*;
import nom.tam.util.BufferedFile;
import starsnapper.treatment.RawToShorts;

import java.io.*;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 01/02/2016.
 */
public class BinToFits {

    public static void main(String args[]) throws IOException, FitsException {
        BufferedReader br = new BufferedReader(new FileReader("c:\\temp\\telescope\\catalog.txt"));
        String line;

        while((line = br.readLine()) != null) {
            String[] tokens = line.split(";");
            String fileName = tokens[0];
            float exposureTime = Float.parseFloat(tokens[1]);
            String dateEnd = tokens[2];
            int height = Integer.parseInt(tokens[3]);
            int width = Integer.parseInt(tokens[4]);
            System.out.println("File = " + fileName + ", exposure = " + exposureTime + "end timestamp = " + dateEnd + ", height = " + height + ", width = " + width);

            File file = new File(fileName);
            byte[] fileData = new byte[(int) file.length()];
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            dis.readFully(fileData);
            dis.close();

            byte[][] rawData = new byte[2][width * height];

            for(int row = 0; row < height; row++) {
                int field = row % 2;

                for(int col = 0; col < width; col++) {
                    rawData[field][(row / 2) * width + col] = fileData[row * width + col];
                }
            }

            String fitsFileName = fileName.replace("complexo_20160116", "normalized").replace(".bin", ".fits");
            System.out.println("New file name = " + fitsFileName);

            double evenExposure = (2.0 * exposureTime) - 0.5;

            double[] normalization = { 1.0, 1.0 / evenExposure };

            RawToShorts floatsGenerator = new RawToShorts(width, height / 2, 2);
            short[][] data = floatsGenerator.convertRawInterlacedToShorts(rawData, normalization);
            Fits fitsFile = new Fits();
            BasicHDU<?> dataHDU = FitsFactory.hduFactory(data);
            Header header = dataHDU.getHeader();
            header.addValue("EXPOSURE", exposureTime, "Exposure time (s)");
            header.addValue("DATE-END", dateEnd, "Observation timestamp");
            fitsFile.addHDU(dataHDU);
            File fitsFileDesc = new File(fitsFileName);
            BufferedFile bufferedFile = new BufferedFile(fitsFileDesc, "rw");
            fitsFile.write(bufferedFile);
            bufferedFile.close();
        }
    }
}
