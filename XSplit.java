// XSplit.java
// Creates initial sorted runs from standard input
// Name: Kai Meiklejohn
// ID: 1632448

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class XSplit {
    private static int runLength;

    // main method - checks args and calls run creation func
    // also handles the file writing if we only want the runs
    public static void main(String[] args) throws IOException {
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java XSplit <run_length: 64-1024> [2]");
            System.exit(1);
        }

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

        List<File> runs = createInitialRuns();

        // If only one argument, write runs to runs.txt and clean up
        if (args.length == 1) {
            System.err.println("Outputting runs to runs.txt");
            outputRunsToFile(runs, new File("runs.txt"));
        }
    }

    // reads from stidn and creates sorted runs of run_length
    // distributes the runs between 2 diff "tape" files in alternating pattern
    private static List<File> createInitialRuns() throws IOException {
        List<String> lines = new ArrayList<>(runLength);
        List<File> runFiles = new ArrayList<>();
        File temp1 = new File("runs1.txt");
        File temp2 = new File("runs2.txt");
        runFiles.add(temp1);
        runFiles.add(temp2);
        boolean writeToFirst = true;
        int totalInputLines = 0;

        // Delete existing files to avoid appending
        if (temp1.exists() && !temp1.delete()) {
            System.err.println("Failed to delete " + temp1.getName());
        }
        if (temp2.exists() && !temp2.delete()) {
            System.err.println("Failed to delete " + temp2.getName());
        }

        try (PrintWriter writer1 = new PrintWriter(new FileWriter(temp1));
            PrintWriter writer2 = new PrintWriter(new FileWriter(temp2));
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                totalInputLines++;
                if (lines.size() < runLength) {
                    lines.add(line);
                } else {
                    // got enough lines for a run, now sort and write to appropriate file
                    heapSort(lines);
                    PrintWriter target = writeToFirst ? writer1 : writer2;
                    for (String sortedLine : lines) {
                        target.println(sortedLine);
                    }
                    writeToFirst = !writeToFirst;
                    lines.clear();
                    lines.add(line);
                }
            }

            // gotta handle leftover lines at the end
            if (!lines.isEmpty()) {
                heapSort(lines);
                PrintWriter target = writeToFirst ? writer1 : writer2;
                for (String sortedLine : lines) {
                    target.println(sortedLine);
                }
            }
        }

        // System.err.println("Total input lines read: " + totalInputLines);
        // System.err.println("Runs1.txt: " + countLines(temp1) + " lines, " + temp1.length() + " bytes");
        // System.err.println("Runs2.txt: " + countLines(temp2) + " lines, " + temp2.length() + " bytes");

        return runFiles;
    }

    // takes all the runs from seprate files and combines them into 1 file
    // this is for when we only wanna output sorted runs and skip the merge
    private static void outputRunsToFile(List<File> runFiles, File outputFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            for (File runFile : runFiles) {
                try (BufferedReader reader = new BufferedReader(new FileReader(runFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.println(line);
                    }
                }
                if (runFile.exists() && !runFile.delete()) {
                    System.err.println("Failed to delete " + runFile.getName());
                }
            }
        }
    }

    // implements heap sort to sort the runs in memory
    // sorts the given list in place - no new lists are created
    private static void heapSort(List<String> list) {
        int n = list.size();
        // build the heap (rearrange array)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(list, n, i);
        }
        // extract elements one by one from heap
        for (int i = n - 1; i > 0; i--) {
            String temp = list.get(0);
            list.set(0, list.get(i));
            list.set(i, temp);
            heapify(list, i, 0);
        }
    }

    // heapify a subtree with root at given index
    // maintains the max heap property recursivly
    private static void heapify(List<String> list, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && list.get(left).compareTo(list.get(largest)) > 0) {
            largest = left;
        }
        if (right < n && list.get(right).compareTo(list.get(largest)) > 0) {
            largest = right;
        }
        if (largest != i) {
            String temp = list.get(i);
            list.set(i, list.get(largest));
            list.set(largest, temp);
            heapify(list, n, largest);
        }
    }
}