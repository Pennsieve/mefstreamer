package edu.upenn.cis.eeg.mef.mefstreamer;

import java.io.BufferedWriter;
import java.io.File;
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
            System.out.println("Usage: java -jar <jarfile> <Pathdir>");
            return;
        }
        
        String directoryPath = args[0];
        File directory = new File(directoryPath);

        // Check if the provided path is a directory
        if (!directory.isDirectory()) {
            System.out.println("The provided path is not a valid directory.");
            return;
        }

        // List all .mef files in the directory
        File[] mefFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".mef"));
        
        if (mefFiles == null || mefFiles.length == 0) {
            System.out.println("No .mef files found in the specified directory.");
            return;
        }

        // Process each .mef file
        for (File inputFile : mefFiles) {
            String filePath = inputFile.getAbsolutePath();
            String fileName = inputFile.getName();
            String baseName;

            // Check for file extension
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                baseName = fileName.substring(0, dotIndex);
            } else {
                baseName = fileName; // Use the whole filename if no extension
            }

            // Construct output file names including the directory
            String headerFileName = directoryPath + File.separator + baseName + "_header_info.csv";
            String valuesFileName = directoryPath + File.separator + baseName + "_values_data.csv";

            try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
                MEFStreamer streamer = new MEFStreamer(file);
                MefHeader2 header = streamer.getMEFHeader();

         // Write header information to CSV 53
                try (BufferedWriter headerWriter = new BufferedWriter(new FileWriter(headerFileName))) {
                	headerWriter.write("Institution,Unencrypted Text Field,Encryption Algorithm,Subject Encryption Used,Session Encryption Used,Data Encryption Used,Byte Order Code,Header Version Major,Header Version Minor,Header Length,Session Unique ID,Subject First Name,Subject Middle Name,Subject Last Name,Subject ID,Session Password,Number of Index Entries,Channel Name,Recording State Time,Recording End Time,Sampling Frequency,Low Frequency Filter Setting,High Frequency Filter Setting,Notch Filter Frequency,Voltage Conversion Factor,Acquisition System,Channel Comments,Study Comments,Physical Channel Number,Compression Algorithm,Maximum Compressed Block Size,Maximum Block Length,Block Interval,Maximum Data Value,Minimum Data Value,Block Header Length,GMT Offset,Discontinuity Data Offset,Number of Discontinuity Entries,File Unique ID,Anonymized Subject Name,Header CRC,8 Random Bytes,Bytes Read,Class,File Name,Index Data Offset,Index Data Offset End,Session Validation Field,Subject Validation Field,Voltage Offset,CRC Tab 32\n");
                	headerWriter.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
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
                			// missing subject password validation field
                			// missing protected region
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
                			String.valueOf(header.getHeaderCrc()),
                			String.valueOf(header.get8RandomBytes()),
                			String.valueOf(header.getBytesRead()),
                			String.valueOf(header.getClass()),
                			header.getFilename(),
                			String.valueOf(header.getIndexDataOffset()),
                			String.valueOf(header.getIndexDataOffsetEnd()),
                			String.valueOf(header.getNumberOfSamples()),
                			header.getSessionValidationField(),
                			header.getSubjectValidationField(),
                			String.valueOf(header.getVoltageOffset()),
                			String.valueOf(header.getCrcTab32()))); //byte
                        	// missing EEG Data Start
                }


                // Get number of blocks from MEF file
                long numBlocks = streamer.getNumberOfBlocks();
                System.out.println("Num Blocks: " + numBlocks + " blocks.");

                // Write values to CSV
                try (BufferedWriter valuesWriter = new BufferedWriter(new FileWriter(valuesFileName))) {
                	valuesWriter.write("Value\n");

                	// Loop over each page within the blocks
                	for (TimeSeriesPage page : streamer.getNextBlocks((int) numBlocks - 1)) {

                		// Write the page data to the CSV
                		for (int value : page.values) {
                			valuesWriter.write(String.format("%d\n", value));
                		}
                	}
                }

                System.out.println("Header file created: " + headerFileName);
                System.out.println("Values file created: " + valuesFileName);
            } catch (IOException e) {
                System.err.println("Error processing file: " + fileName);
                e.printStackTrace();
            }
        }

        System.out.println("All files processed!");
    }
}