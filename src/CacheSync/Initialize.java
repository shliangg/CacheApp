package CacheSync;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author shantanubobhate
 */
public class Initialize {
    
    
    // n = 138,000, p = 0.0001 (1 in 10,000) → m = 2,645,477 (322.93KB), k = 13
    
    // Make sets of size below (avarage from statistics)
    private static final int SIZE = 2645477;
    private static final int k = 13;
    // Decalare a FileReader and initialize it to null
    private static FileReader myFileReader = null;
    // The bloom filter
    public static final BloomFilter filter = new BloomFilter(SIZE, k);
    // Hashmap to store strings and their hash values
    private static HashMap data;
    // Hashmap to store strings and their popularities
    private static HashMap popularities;
    
    private static final ArrayList<String> strings = new ArrayList();
    
    public static TrieST<Integer> st = new TrieST<>();
    
    public static boolean buildStructure(String fileName) {
        // NOTE: Set boolean flag to see data to help debug
        boolean debug = false;
        
        try {
            // Try to read from the file
            myFileReader = new FileReader(fileName);
            BufferedReader myBufferedReader = new BufferedReader(myFileReader);
            String line;
            while ((line = myBufferedReader.readLine()) != null) {
                // Split the line at any whitespace
                String[] split = line.split("\\s+");
                strings.add(split[0]);
                filter.add(split[0]);
                st.put(split[0], Integer.parseInt(split[1]));
            }
        // Catch any exceptions
        } catch (IOException | NumberFormatException e) {
            System.out.println(e);
            return false;
        } finally {
            // Try to close the file
            try {
                if (myFileReader != null) myFileReader.close();
            // Catch any exceptions
            } catch (Exception e) {
                System.out.println(e);
                return false;
            }
        }

        // Print message if sets were built successfully
        System.out.println("Done adding elements.");
        
        return true;
    }
    
    public static ArrayList<String> getStrings(byte[] set, int keySize, int numberOfElements) {
        ArrayList<String> toSendStrings = new ArrayList();
        
        BloomFilter otherFilter = new BloomFilter(set, keySize, numberOfElements);
        
        System.out.println("Processing: Please Wait...");
        for (int jj = 0; jj < strings.size(); jj++) {
            String currentString = (String) strings.get(jj);
            if (!otherFilter.contains(currentString)) {
                toSendStrings.add(currentString);
            }
        }
        System.out.println("Processing done.");
        return toSendStrings;
    }
    
    public static void addStrings(ArrayList<String> toAdd) {
        System.out.println("Refreshing Data Structure...");
        int ecount = strings.size() + toAdd.size();
        for (String s : toAdd) {
            if (!strings.contains(s)) {
                strings.add(s);
                st.put(s, 0);
            }
        }
        int acount = strings.size();
        int dcount = ecount-acount;
        System.out.println("Done Refreshing");
        System.out.println("Total number of false positives: " + dcount);
        System.out.println("Constructing File");
        FileWriter myFileWriter = null;
        try {
            // Try to read from the file
            myFileWriter = new FileWriter("output.txt");
            BufferedWriter myBufferedWriter = new BufferedWriter(myFileWriter);
            
            for (String s : strings) {
                myBufferedWriter.write(s);
                myBufferedWriter.newLine();
            }
            myBufferedWriter.close();
        // Catch any exceptions
        } catch (IOException | NumberFormatException e) {
            System.out.println(e);
        } finally {
            // Try to close the file
            try {
                if (myFileWriter != null) myFileWriter.close();
            // Catch any exceptions
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        System.out.println("File Constructed");
    }
    
    public static void insert(String entry) {
        if (!st.contains(entry)){
           st.put(entry, 0);
           strings.add(entry);
           filter.add(entry);
           System.out.println("Entry added");
        }
        else {
            int cweight = st.get(entry) + 1;
            st.put(entry, null);
            st.put(entry,cweight);
            System.out.println("Updated value to " + st.get(entry));
        }
    }
}