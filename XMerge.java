// XMerge.java
// Performs 2-way merge on pre-created runs
// Name: Kai Meiklejohn
// ID: 1632448
// March 2025

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class XMerge {
    private static int runLength;

    public static void main(String[] args) throws IOException {
        if (args.length != 2 || !args[1].equals("2")) {
            System.out.println("Usage: java XMerge <run_length: 64-1024> 2");
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

        List<File> runFiles = new ArrayList<>();
        runFiles.add(new File("runs1.txt"));
        runFiles.add(new File("runs2.txt"));
        mergeRuns2Way(runFiles);
    }

    private static void mergeRuns2Way(List<File> runFiles) throws IOException {
        if (runFiles.size() != 2 || !runFiles.get(0).exists() || !runFiles.get(1).exists()) {
            System.err.println("Expected exactly 2 run files (runs1.txt, runs2.txt) to exist.");
            System.exit(1);
        }

        System.err.println("Merging runs...");

        File tape1 = runFiles.get(0);
        File tape2 = runFiles.get(1);
        File tape3 = new File("temp1.txt");
        File tape4 = new File("temp2.txt");

        int totalLines1 = countLines(tape1);
        int totalLines2 = countLines(tape2);
        int runsPerFile = Math.max((totalLines1 + runLength - 1) / runLength,
                                   (totalLines2 + runLength - 1) / runLength);

        System.err.println("Initial runs per file: " + runsPerFile);

        // Multi-pass merge until one run remains
        while (runsPerFile > 1) {
            mergePass(tape1, tape2, tape3, tape4);
            runsPerFile = (runsPerFile + 1) / 2; // Ceiling division
            if (tape1.exists()) tape1.delete();
            if (tape2.exists()) tape2.delete();
            if (tape3.exists()) tape3.renameTo(new File("runs1.txt"));
            if (tape4.exists()) tape4.renameTo(new File("runs2.txt"));
            tape1 = new File("runs1.txt");
            tape2 = new File("runs2.txt");
            tape3 = new File("temp1.txt");
            tape4 = new File("temp2.txt");
            System.err.println("Runs per file after pass: " + runsPerFile);
        }

        // Final merge to stdout when one run per file remains
        mergePass(tape1, tape2, null, null);
        if (tape1.exists()) tape1.delete();
        if (tape2.exists()) tape2.delete();
    }

    private static void mergePass(File in1, File in2, File out1, File out2) throws IOException {
        try (BufferedReader reader1 = new BufferedReader(new FileReader(in1));
             BufferedReader reader2 = new BufferedReader(new FileReader(in2));
             PrintWriter writer1 = out1 != null ? new PrintWriter(new FileWriter(out1)) : new PrintWriter(System.out, true);
             PrintWriter writer2 = out2 != null ? new PrintWriter(new FileWriter(out2)) : null) {

            String line1 = reader1.readLine();
            String line2 = reader2.readLine();
            boolean useFirstOutput = true;

            while (line1 != null || line2 != null) {
                PrintWriter currentWriter = useFirstOutput || writer2 == null ? writer1 : writer2;
                int linesWritten = 0;

                // Merge one pair of runs (up to runLength each) into a single sorted run
                while (linesWritten < runLength * 2 && (line1 != null || line2 != null)) {
                    if (line1 == null) {
                        currentWriter.println(line2);
                        line2 = reader2.readLine();
                        linesWritten++;
                    } else if (line2 == null) {
                        currentWriter.println(line1);
                        line1 = reader1.readLine();
                        linesWritten++;
                    } else if (line1.compareTo(line2) <= 0) {
                        currentWriter.println(line1);
                        line1 = reader1.readLine();
                        linesWritten++;
                    } else {
                        currentWriter.println(line2);
                        line2 = reader2.readLine();
                        linesWritten++;
                    }
                }

                // Switch output for the next merged run (if not final pass)
                if (writer2 != null && (line1 != null || line2 != null)) {
                    useFirstOutput = !useFirstOutput;
                }
            }
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