// XSplit.java
// Creates initial sorted runs from standard input
// Name: Kai Meiklejohn
// ID: 1632448
// March 2025

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class XSplit {
    private static int runLength;

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
            outputRunsToFile(runs, new File("runs.txt"));
        }
        // Two arguments: Leave runs1.txt and runs2.txt for XMerge
    }

    private static List<File> createInitialRuns() throws IOException {
        List<String> lines = new ArrayList<>(runLength);
        List<File> runFiles = new ArrayList<>();
        File temp1 = new File("runs1.txt");
        File temp2 = new File("runs2.txt");
        runFiles.add(temp1);
        runFiles.add(temp2);
        boolean writeToFirst = true;
        int totalInputLines = 0;

        try (PrintWriter writer1 = new PrintWriter(new FileWriter(temp1, true));
             PrintWriter writer2 = new PrintWriter(new FileWriter(temp2, true))) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while ((line = reader.readLine()) != null) {
                totalInputLines++;
                if (lines.size() < runLength) {
                    lines.add(line);
                } else {
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

            if (!lines.isEmpty()) {
                heapSort(lines);
                PrintWriter target = writeToFirst ? writer1 : writer2;
                for (String sortedLine : lines) {
                    target.println(sortedLine);
                }
            }

            reader.close();
        }

        System.err.println("Total input lines read: " + totalInputLines);
        System.err.println("Runs1.txt: " + countLines(temp1) + " lines, " + temp1.length() + " bytes");
        System.err.println("Runs2.txt: " + countLines(temp2) + " lines, " + temp2.length() + " bytes");

        return runFiles;
    }

    private static void outputRunsToFile(List<File> runFiles, File outputFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            for (File runFile : runFiles) {
                try (BufferedReader reader = new BufferedReader(new FileReader(runFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.println(line);
                    }
                }
                // Delete temporary run files
                if (runFile.exists()) {
                    if (!runFile.delete()) {
                        System.err.println("Failed to delete " + runFile.getName());
                    }
                }
            }
        }
    }

    private static void heapSort(List<String> list) {
        int n = list.size();
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(list, n, i);
        }
        for (int i = n - 1; i > 0; i--) {
            String temp = list.get(0);
            list.set(0, list.get(i));
            list.set(i, temp);
            heapify(list, i, 0);
        }
    }

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

    private static int countLines(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int lines = 0;
            while (reader.readLine() != null) lines++;
            return lines;
        }
    }
}