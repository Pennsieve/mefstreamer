package edu.upenn.cis.eeg.mef.mefstreamer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import edu.upenn.cis.db.mefview.services.TimeSeriesPage;

/**
 * Main class to run the MEFStreamer application.
 */
public class MEFStreamerMain {
    public static void main(String[] args) {
        // Example usage of MEFStreamer
        if (args.length < 1) {
            System.out.println("Usage: java -jar <jarfile> <filePath>");
        }

        String filePath = args[0];
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            MEFStreamer streamer = new MEFStreamer(file);
            MefHeader2 header = streamer.getMEFHeader();
            
            // TODO: Write out header file
            System.out.println("HEADER INFO");
            System.out.println("Channel Name: " + header.getChannelName());
            System.out.println("GMT OFFSET: " + header.getGmtOffset());
            System.out.println("Sampling Frequency: " + header.getSamplingFrequency());
            System.out.println("Recording start time: " + header.getRecordingStartTime());
            System.out.println("Recording end time: " + header.getRecordingEndTime());
            System.out.println("MinValue: " + header.getMinimumDataValue());
            System.out.println("MaxValue: " + header.getMaximumDataValue());
            
//            WHILE STREAMER hasBlocks
//            	GET X pages of blocks
//            		For each block, get the values
//            			READ each value
            
            // Get number of blocks from MEF file
            long numBlocks = streamer.getNumberOfBlocks();
            System.out.println("Num Blocks: " +  numBlocks + " blocks.");
            
            // Loop over each page within the blocks
            for(TimeSeriesPage page: streamer.getNextBlocks((int)numBlocks -1)) {
            	
            	System.out.println("TIME START: " + page.timeStart);
            	System.out.println("TIME END: " + page.timeEnd);
            	
            	// Printing out page itself isn't super useful
//            	System.out.println("Block: " + page.toString());
            	int page_len = page.values.length;
            	
            	// What is useful: loop through the pages to get data values
            	for(int value: page.values) {
            		System.out.println("Value: " + value);
            	}
            }
            
            List<TimeSeriesPage> pages = streamer.getNextBlocks(10); // Read 10 blocks
            // Process pages or output as needed
            System.out.println("Read " + pages.size() + " blocks.");
            System.out.println(pages.get(0).values[8]);
            System.out.println("DONE!");
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
