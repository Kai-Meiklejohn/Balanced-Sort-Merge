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
        if (runFiles.size() != 2) {
            System.err.println("Expected exactly 2 run files for merging.");
            System.exit(1);
        }

        File in1 = runFiles.get(0);
        File in2 = runFiles.get(1);
        File temp1 = new File("temp1.txt");
        File temp2 = new File("temp2.txt");

        int totalLines1 = countLines(in1);
        int totalLines2 = countLines(in2);
        int runsPerFile = Math.max((totalLines1 + runLength - 1) / runLength, 
                                   (totalLines2 + runLength - 1) / runLength);

        if (runsPerFile > 1) {
            while (runsPerFile > 1) {
                mergePass(in1, in2, temp1, temp2, runsPerFile);
                runsPerFile = (runsPerFile + 1) / 2;
                if (in1.exists()) in1.delete();
                if (in2.exists()) in2.delete();
                if (temp1.exists()) temp1.renameTo(new File("runs1.txt"));
                if (temp2.exists()) temp2.renameTo(new File("runs2.txt"));
                in1 = new File("runs1.txt");
                in2 = new File("runs2.txt");
                temp1 = new File("temp1.txt");
                temp2 = new File("temp2.txt");
            }
            mergePass(in1, in2, null, null, 1);
            if (in1.exists()) in1.delete();
            if (in2.exists()) in2.delete();
        } else if (totalLines1 + totalLines2 > 0) {
            outputRunToStdout(in1);
            if (in1.exists()) in1.delete();
            if (in2.exists()) in2.delete();
        }
    }

    private static void mergePass(File in1, File in2, File out1, File out2, int runsPerFile) throws IOException {
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
                useFirstOutput = !useFirstOutput;
            }

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

    private static void outputRunToStdout(File runFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(runFile));
             PrintWriter stdout = new PrintWriter(System.out, true)) {
            String line;
            while ((line = reader.readLine()) != null) {
                stdout.println(line);
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