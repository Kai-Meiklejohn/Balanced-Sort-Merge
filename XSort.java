// Balanced Sort Merge Assignment
// Name: Kai Meiklejohn, ID: 1632448
// Solo project: 2-way sort merge
// March 2025

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class XSort {
   /**
     * main entry point which alidates args and orchestrates run creation and merging
     * @param args [0]: run length (64–1024), [1]: optional "2" for merge mode
     */
    public static void main(String[] args) throws IOException {
        // check number of arguments
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java XSort <run_length: 64-1024> [2]");
            System.exit(1);
        }

        // Declare and initialize runLength with a default value
        int runLength = 0;
        try {
            runLength = Integer.parseInt(args[0]);
            if (runLength < 64 || runLength > 1024) {
                System.out.println("Run length must be between 64 and 1024.");
                System.exit(1);
            }
        } catch (NumberFormatException e) {
            System.out.println("Run length must be an integer between 64 and 1024.");
            System.exit(1);
        }

        // Check merge mode 
        if (args.length == 2 && !args[1].equals("2")) {
            System.out.println("Solo project supports only 2-way merge. Use 2 as second argument.");
            System.exit(1);
        }

        // create initial sorted runs
        List<File> runs = createInitialRuns(runLength);

        // if merge mode is specified, perform 2-way merge
        if (args.length == 2) {
            mergeRuns2Way(runs);
        }
    }

    /**
     * creates initial sorted runs from standard input using heapsort
     * @param runLength number of lines per run (64–1024)
     * @return list of temporary files containing sorted runs
     */
    private static List<File> createInitialRuns(int runLength) throws IOException {
        // initialise list to hold up to runLength lines
        List<String> lines = new ArrayList<>(runLength);
        List<File> runFiles = new ArrayList<>();
        int runCount = 0;

        // read from standard input using BufferedReader
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = reader.readLine()) != null) {
            // If list isn’t full, add the line
            if (lines.size() < runLength) {
                lines.add(line);
            } else {
                // list is full: sort with heapsort and write to temp file
                heapSort(lines);
                File tempFile = writeRunToTempFile(lines, "run" + runCount);
                runFiles.add(tempFile);
                runCount++;
                // clear list and add current line to start new run
                lines.clear();
                lines.add(line);
            }
        }

        // handle any remaining lines (less than runLength)
        if (!lines.isEmpty()) {
            heapSort(lines);
            File tempFile = writeRunToTempFile(lines, "run" + runCount);
            runFiles.add(tempFile);
        }

        // close reader and return list of run files
        reader.close();
        return runFiles;
    }

    /**
     * writes a sorted run to a temporary file
     * @param lines sorted list of lines
     * @param prefix file prefix (e.g. "run0")
     * @return temporary file object
     */
    private static File writeRunToTempFile(List<String> lines, String prefix) throws IOException {
        // write lines to a temp file, preserving contents



        return null;
    }

    /**
     * sorts an ArrayList of strings in ascending order using heapsort
     * @param list list of strings to sort
     */
    private static void heapSort(List<String> list) {
        // Build max-heap, then extract elements to sort


    }

    /**
     * heapifies a subtree rooted at index i
     * @param list list to heapify
     * @param n Size of the heap
     * @param i root index of subtree
     */
    private static void heapify(List<String> list, int n, int i) {
        // ensure max heap property by comparing and swapping down



    }

    /**
     * does a 2-way balanced merge on the initial runs
     * @param runFiles list of temp files with sorted runs
     */
    private static void mergeRuns2Way(List<File> runFiles) throws IOException {
        // distribute runs, perform merge passes, output final run



    }

    /**
     * Distributes initial runs across two temp files
     * @param runFiles List of run files
     * @param temp1 first temp file
     * @param temp2 second temp file
     */
    private static void distributeRuns(List<File> runFiles, File temp1, File temp2) throws IOException {
        // split runs into temp1 (odd) and temp2 (even)
    }

    /**
     * Merges runs from two input files into two output files
     * @param in1 first input file
     * @param in2 second input file
     * @param out1 first output file
     * @param out2 second output file
     * @param runsPerFile Number of runs per input file
     */
    private static void mergePass(File in1, File in2, File out1, File out2, int runsPerFile) throws IOException {
        // Merge pairs of runs, alternating outputs



    }

    /**
     * outputs a run file to standard output
     * @param runFile File to output
     */
    private static void outputRunToStdout(File runFile) throws IOException {
        // Write run contents to System.out


    }
}