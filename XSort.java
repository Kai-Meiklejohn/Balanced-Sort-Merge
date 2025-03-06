// Balanced Sort Merge Assignment
// Name: Kai Meiklejohn
// ID: 1632448
// Solo project: 2-way sort merge
// March 2025

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class XSort {
    // class level variable to store run length
    private static int runLength;
    
    /**
     * main entry point wich validates args and orchestrates run creation and merging
     * @param args [0]: run length (64–1024), [1]: optional "2" for merge mode
     */
    public static void main(String[] args) throws IOException {
        // check number of arguments
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java XSort <run_length: 64-1024> [2]");
            System.exit(1);
        }

        // initialize the class-level runLength
        runLength = 0;
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

        // check merge mode 
        if (args.length == 2 && !args[1].equals("2")) {
            System.out.println("Solo project supports only 2-way merge. Use 2 as second argument.");
            System.exit(1);
        }

        // create initial sorted runs
        List<File> runs = createInitialRuns();

        // if merge mode is specified, do a 2-way merge
        if (args.length == 2) {
            mergeRuns2Way(runs);
        }
    }

    /**
     * creates initial sorted runs from standard input using heapsort
     * @param none, uses class-level runLength
     * @return list of temporary files containing sorted runs
     */
    private static List<File> createInitialRuns() throws IOException {
        // initialise list to hold up to runLength lines
        List<String> lines = new ArrayList<>(runLength);
        List<File> runFiles = new ArrayList<>();
        File temp1 = new File("runs1.txt");
        File temp2 = new File("runs2.txt");
        runFiles.add(temp1);
        runFiles.add(temp2);
        boolean writeToFirst = true; // alternate between temp1 and temp2

        // open writers for both files (append mode to avoid overwriting)
        try (PrintWriter writer1 = new PrintWriter(new FileWriter(temp1, true));
             PrintWriter writer2 = new PrintWriter(new FileWriter(temp2, true))) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while ((line = reader.readLine()) != null) {
                // if list isn’t full, add the line
                if (lines.size() < runLength) {
                    lines.add(line);
                } else {
                    // list is full: sort with heapsort and write to temp file
                    heapSort(lines);
                    PrintWriter target = writeToFirst ? writer1 : writer2;
                    for (String sortedLine : lines) {
                        target.println(sortedLine);
                    }
                    writeToFirst = !writeToFirst; // alternate files
                    // clear list and add current line to start new run
                    lines.clear();
                    lines.add(line);
                }
            }

            // handle any remaining lines (less then runLength)
            if (!lines.isEmpty()) {
                heapSort(lines);
                PrintWriter target = writeToFirst ? writer1 : writer2;
                for (String sortedLine : lines) {
                    target.println(sortedLine);
                }
            }

            reader.close();
        }

        // print debug count lines in each file
        System.err.println("Runs1.txt: " + countLines(temp1) + " lines");
        System.err.println("Runs2.txt: " + countLines(temp2) + " lines");

        return runFiles; // returns [runs1.txt, runs2.txt]
    }

    /**
     * writes a sorted run to a temporary file
     * @param lines sorted list of lines
     * @param prefix file prefix (e.g. "run0")
     * @return temporary file object
     */
    private static File writeRunToTempFile(List<String> lines, String prefix) throws IOException {
        // write lines to a temp file, preserving contents - unused, kept from earlier design
        File tempFile = new File(prefix + ".txt");
        
        // write lines to the temp file, keeping original content
        try (PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {
            for (String line : lines) {
                writer.println(line); // println keeps the line as-is, adds platform-specific newline
            }
        }
        
        return tempFile;
    }

    /**
     * sorts an ArrayList of strings in ascending order using heapsort
     * @param list list of strings to sort
     */
    private static void heapSort(List<String> list) {
        // build max-heap, then extract elements to sort
        int n = list.size();

        // build max-heap (start from last non-leaf node)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(list, n, i);
        }

        // extract elements from heap one by one
        for (int i = n - 1; i > 0; i--) {
            // move current root (max) to end
            String temp = list.get(0);
            list.set(0, list.get(i));
            list.set(i, temp);

            // heapify the reduced heap
            heapify(list, i, 0);
        }
    }

    /**
     * heapifies a subtree rooted at index i
     * @param list list to heapify
     * @param n size of the heap
     * @param i root index of subtree
     */
    private static void heapify(List<String> list, int n, int i) {
        // ensure max heap property by comparing and swapping down
        int largest = i; // initialize largest as root
        int left = 2 * i + 1; // left child
        int right = 2 * i + 2; // right child

        // compare with left child
        if (left < n && list.get(left).compareTo(list.get(largest)) > 0) {
            largest = left;
        }

        // compare with right child
        if (right < n && list.get(right).compareTo(list.get(largest)) > 0) {
            largest = right;
        }

        // if largest is not root, swap and keep heapifying
        if (largest != i) {
            String temp = list.get(i);
            list.set(i, list.get(largest));
            list.set(largest, temp);

            // recursively heapify the affected subtree
            heapify(list, n, largest);
        }
    }

    /**
     * does a 2-way balanced merge on the initial runs
     * @param runFiles list of temp files with sorted runs
     */
    private static void mergeRuns2Way(List<File> runFiles) throws IOException {
        // distribute runs, perform merge passes, output final run
        if (runFiles.size() != 2) {
            System.err.println("Expected exactly 2 run files for merging.");
            System.exit(1);
        }

        File in1 = runFiles.get(0); // runs1.txt
        File in2 = runFiles.get(1); // runs2.txt
        File temp1 = new File("temp1.txt");
        File temp2 = new File("temp2.txt");

        // estimate runs per file (could be uneven due to input size)
        int totalLines1 = countLines(in1);
        int totalLines2 = countLines(in2);
        int runsPerFile = Math.max((totalLines1 + runLength - 1) / runLength, 
                                   (totalLines2 + runLength - 1) / runLength);

        // Only do merge passes if there's more than one run
        if (runsPerFile > 1) {
            while (runsPerFile > 1) {
                mergePass(in1, in2, temp1, temp2, runsPerFile);
                runsPerFile = (runsPerFile + 1) / 2;
                in1.delete();
                in2.delete();
                in1 = temp1;
                in2 = temp2;
                temp1 = new File("temp1.txt");
                temp2 = new File("temp2.txt");
            }
            // final merge to stdout
            mergePass(in1, in2, null, null, 1);
        } else if (totalLines1 + totalLines2 > 0) {
            // if only one run, just output it
            outputRunToStdout(in1);
        }

        in1.delete();
        in2.delete();
    }

    /**
     * distributes initial runs across two temp files
     * @param runFiles list of run files
     * @param temp1 first temp file
     * @param temp2 second temp file
     */
    private static void distributeRuns(List<File> runFiles, File temp1, File temp2) throws IOException {
        // split runs into temp1 (odd) and temp2 (even) - unused, kept for potential reuse
        try (PrintWriter writer1 = new PrintWriter(new FileWriter(temp1));
             PrintWriter writer2 = new PrintWriter(new FileWriter(temp2))) {
            for (int i = 0; i < runFiles.size(); i++) {
                PrintWriter target = (i % 2 == 0) ? writer1 : writer2;
                try (BufferedReader reader = new BufferedReader(new FileReader(runFiles.get(i)))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        target.println(line);
                    }
                }
                runFiles.get(i).delete();
            }
        }
    }

    /**
     * merges runs from two input files into two output files
     * @param in1 first input file
     * @param in2 second input file
     * @param out1 first output file (null for stdout)
     * @param out2 second output file (null if stdout)
     * @param runsPerFile number of runs per input file
     */
    private static void mergePass(File in1, File in2, File out1, File out2, int runsPerFile) throws IOException {
        // merge pairs of runs, alternating outputs
        try (BufferedReader reader1 = new BufferedReader(new FileReader(in1));
             BufferedReader reader2 = new BufferedReader(new FileReader(in2));
             PrintWriter writer1 = out1 != null ? new PrintWriter(new FileWriter(out1)) : new PrintWriter(System.out, true);
             PrintWriter writer2 = out2 != null ? new PrintWriter(new FileWriter(out2)) : null) {
            String line1 = reader1.readLine();
            String line2 = reader2.readLine();
            boolean useFirstOutput = true;
            int runsMerged = 0;

            while (runsMerged < runsPerFile && (line1 != null || line2 != null)) {
                PrintWriter currentWriter = useFirstOutput ? writer1 : writer2;
                int linesWritten = 0;

                // merge one pair of runs (up to runLength lines or end of file)
                while (linesWritten < runLength && (line1 != null || line2 != null)) {
                    if (line1 == null) {
                        currentWriter.println(line2);
                        line2 = reader2.readLine();
                    } else if (line2 == null) {
                        currentWriter.println(line1);
                        line1 = reader1.readLine();
                    } else if (line1.compareTo(line2) <= 0) {
                        currentWriter.println(line1);
                        line1 = reader1.readLine();
                    } else {
                        currentWriter.println(line2);
                        line2 = reader2.readLine();
                    }
                    linesWritten++;
                }

                runsMerged++;
                useFirstOutput = !useFirstOutput; // alternate outputs
            }

            // flush remaining lines using last currentWriter
            PrintWriter currentWriter = useFirstOutput ? writer1 : writer2;
            while (line1 != null) {
                currentWriter.println(line1);
                line1 = reader1.readLine();
            }
            while (line2 != null) {
                currentWriter.println(line2);
                line2 = reader2.readLine();
            }
        }
    }

    /**
     * outputs a run file to standard output
     * @param runFile file to output
     */
    private static void outputRunToStdout(File runFile) throws IOException {
        // write run contents to System.out
        try (BufferedReader reader = new BufferedReader(new FileReader(runFile))) {
            PrintWriter stdout = new PrintWriter(System.out, true);
            String line;
            while ((line = reader.readLine()) != null) {
                stdout.println(line);
            }
        }
    }

    // helper method to count lines wich is used in mergeRuns2Way
    private static int countLines(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int lines = 0;
            while (reader.readLine() != null) lines++;
            return lines;
        }
    }
}