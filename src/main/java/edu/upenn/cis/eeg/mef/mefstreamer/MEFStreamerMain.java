package edu.upenn.cis.eeg.mef.mefstreamer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import edu.upenn.cis.db.mefview.services.TimeSeriesPage;

/**
 * Main class to run the MEFStreamer application.
 */
public class MEFStreamerMain {
    public static void main(String[] args) {
        // Check for file path argument
        if (args.length < 1) {
            System.out.println("Usage: java -jar <jarfile> <filePath>");
            return;
        }

        String filePath = args[0];
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            MEFStreamer streamer = new MEFStreamer(file);
            MefHeader2 header = streamer.getMEFHeader();

         // Write header information to CSV
            try (BufferedWriter headerWriter = new BufferedWriter(new FileWriter("/Users/juliadengler/Documents/mefstreamer/header_info.csv"))) {
                headerWriter.write("Channel Name,GMT OFFSET,Sampling Frequency,Recording Start Time,Recording End Time,Min Value,Max Value\n");
                headerWriter.write(String.format("%s,%s,%s,%s,%s,%s,%s\n",
                        header.get
                		header.getInstitution(), // string
                        header.getUnencryptedTextField(), //string
                        header.getEncryptionAlgorithm(), //string
                        String.valueOf(header.getSubjectEncryptionUsed()), //int
                        String.valueOf(header.getSessionEncryptionUsed()), //int
                        String.valueOf(header.getDataEncryptionUsed()), //int
                        String.valueOf(header.getByteOrderCode()), //int
                        String.valueOf(header.getHeaderVersionMajor()), //int
                        String.valueOf(header.getHeaderVersionMinor()), //int
                        String.valueOf(header.getHeaderLength()), //short
                        String.valueOf(header.getSessionUniqueID()), //byte
                        header.getSubjectFirstName(), //string
                        header.getSubjectSecondName(), //string 
                        header.getSubjectThirdName(), //string
                        header.getSubjectId(), //string
                        header.getSessionPassword(), //string
                        // missing subject passowrd validation field
                        // missing protected region
                        header.getSessionPassword(), //string
                        String.valueOf(header.getNumberOfIndexEntries()), //long
                		header.getChannelName(), //string
                        header.getRecordingStartTime(), //string
                        header.getRecordingEndTime(), //string
                        String.valueOf(header.getSamplingFrequency()),
                        String.valueOf(header.getLowFrequencyFilterSetting()), // double
                        String.valueOf(header.getHighFrequencyFilterSetting()), //double
                        String.valueOf(header.getNotchFilterFrequency()), //double
                        String.valueOf(header.getVoltageConversionFactor()), //double
                        header.getAcquisitionSystem(), //string
                        header.getChannelComments(), //string
                        header.getStudyComments(), //string
                        String.valueOf(header.getPhysicalChannelNumber()), //int
                        header.getCompressionAlgorithm(), //string
                        String.valueOf(header.getMaximumCompressedBlockSize()), //long
                        String.valueOf(header.getMaximumBlockLength()), //long
                        String.valueOf(header.getBlockInterval()), //long
                        String.valueOf(header.getMaximumDataValue()), //long
                        String.valueOf(header.getMinimumDataValue()), //long
                        // missing Offset to Block Indices Data
                        // missing Number of Block Index Entries
                        String.valueOf(header.getBlockHeaderLength()), //short
                        // missing unused
                        String.valueOf(header.getGmtOffset()), //float
                        String.valueOf(header.getDiscontinuityDataOffset()), //long
                        String.valueOf(header.getNumberOfDiscontinuityEntries()), //long
                        // missing unused
                        String.valueOf(header.getFileUniqueID()), //byte
                        header.getAnonymizedSubjectName(), //string
                        String.valueOf(header.getHeaderCrc()))); //byte
                        // missing EEG Data Start
            }


            // Get number of blocks from MEF file
            long numBlocks = streamer.getNumberOfBlocks();
            System.out.println("Num Blocks: " + numBlocks + " blocks.");

            // Write values to CSV
            try (BufferedWriter valuesWriter = new BufferedWriter(new FileWriter("/Users/juliadengler/Documents/mefstreamer/values_data.csv"))) {
                valuesWriter.write("Time Start,Time End,Value\n");

                // Loop over each page within the blocks
                for (TimeSeriesPage page : streamer.getNextBlocks((int) numBlocks - 1)) {
                    System.out.println("TIME START: " + page.timeStart);
                    System.out.println("TIME END: " + page.timeEnd);

                    // Write the page data to the CSV
                    for (int value : page.values) {
                        valuesWriter.write(String.format("%d,%d,%d\n", page.timeStart, page.timeEnd, value));
                    }
                }
            }

            System.out.println("DONE!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
