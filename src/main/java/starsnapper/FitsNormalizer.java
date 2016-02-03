package starsnapper;

import nom.tam.fits.*;
import nom.tam.util.BufferedFile;

import java.io.*;

/**
 * @author Helder Savietto (helder.savietto@gmail.com)
 * @date 01/02/2016.
 */
public class FitsNormalizer {

//    private static byte[] floatMatrixToByte(float[][] floatMatrix) {
//        int height = floatMatrix.length;
//        int width = floatMatrix[0].length;
//        int dataLength = width * height * 2;
//        byte[] data = new byte[dataLength];
//
//        for(int row = 0; row < height; row++) {
//            for(int col = 0; col < width; col++) {
//                short converted = (short)floatMatrix[row][col];
//                data[row * width * 2 + col * 2] = (byte)(converted & 0xff);
//                data[row * width * 2 + col * 2 + 1] = (byte)((converted >> 8) & 0xff);
//            }
//        }
//
//        return data;
//    }

    public static void main(String args[]) throws FitsException, IOException {
        File fitsFolder = new File("c:\\temp\\telescope");
        File[] listOfFiles = fitsFolder.listFiles();

        for(File fitsFile: listOfFiles) {
            if(fitsFile.getName().endsWith(".fits") && fitsFile.getName().contains("complexo_20160116")) {
                Fits f = new Fits(fitsFile);
                BasicHDU hdu = f.getHDU(0);
                Header header = hdu.getHeader();
                System.out.println(fitsFile.getAbsolutePath());
                float exposureTime = header.getFloatValue("EXPOSURE");
                String dateEnd = header.getStringValue("DATE-END");
                Data data = hdu.getData();
                float oddExposure = (2.0f * exposureTime) - 0.5f;
                float[] normalization = { 1.0f, 0.5f / oddExposure };
                float[][] floatData = (float[][])data.getData();
                int height = floatData.length;
                int width = floatData[0].length;
                short shortData[][] = new short[height][width];

                for(int row = 0; row < height; row++) {
                    int field = row % 2;

                    for(int col = 0; col < width; col++) {
                        float normalized = floatData[row][col] * normalization[field];
                        shortData[row][col] = (short)((int)normalized & 0xffff);
                    }
                }

                Fits normalizedFits = new Fits();
                BasicHDU<?> dataHDU = FitsFactory.hduFactory(shortData);
                Header normalizedHeader = dataHDU.getHeader();
                normalizedHeader.addValue("EXPOSURE", 0.5, "Exposure time (s)");
                header.addValue("DATE-END", dateEnd, "Observation timestamp");
                normalizedFits.addHDU(dataHDU);
                String fitsFileName = fitsFile.getAbsolutePath().replace("complexo_20160116", "normalized");
                File fitsFileDesc = new File(fitsFileName);
                BufferedFile bufferedFile = new BufferedFile(fitsFileDesc, "rw");
                normalizedFits.write(bufferedFile);
                bufferedFile.close();
            }
        }
    }
}
