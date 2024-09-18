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
            System.exit(1);
        }

        String filePath = args[0];
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            MEFStreamer streamer = new MEFStreamer(file);
            List<TimeSeriesPage> pages = streamer.getNextBlocks(10); // Read 10 blocks
            // Process pages or output as needed
            System.out.println("Read " + pages.size() + " blocks.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
