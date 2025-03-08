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

        File tape1 = new File("runs1.txt");
        File tape2 = new File("runs2.txt");
        File temp1 = new File("temp1.txt");
        File temp2 = new File("temp2.txt");

        // Check if input files exist
        if (!tape1.exists() || !tape2.exists()) {
            System.err.println("Input run files (runs1.txt or runs2.txt) are missing.");
            System.exit(1);
        }

        List<File> inputTapes = new ArrayList<>();
        inputTapes.add(tape1);
        inputTapes.add(tape2);
        List<File> outputTapes = new ArrayList<>();
        outputTapes.add(temp1);
        outputTapes.add(temp2);

        int totalLines = countLines(tape1) + countLines(tape2);
        performBalancedMerge(inputTapes, outputTapes, totalLines);

        // Clean up temporary files
        for (File file : inputTapes) {
            if (file.exists() && !file.delete()) {
                System.err.println("Failed to delete " + file.getName());
            }
        }
        for (File file : outputTapes) {
            if (file.exists() && !file.delete()) {
                System.err.println("Failed to delete " + file.getName());
            }
        }
    }

    private static void performBalancedMerge(List<File> inputTapes, List<File> outputTapes, int totalLines) throws IOException {
        int pass = 0;
        int linesProcessed = 0;

        while (linesProcessed < totalLines) {
            // Delete output tapes if they exist to avoid appending
            for (File file : outputTapes) {
                if (file.exists() && !file.delete()) {
                    System.err.println("Failed to delete " + file.getName());
                }
            }

            linesProcessed = mergePass(inputTapes, outputTapes, pass);
            pass++;

            // If all lines are in one tape, output and exit
            if (linesProcessed >= totalLines) {
                File finalTape = outputTapes.get(0);
                if (finalTape.exists() && countLines(finalTape) == totalLines) {
                    outputToStdout(finalTape);
                    break;
                }
            }

            // Swap tapes for next pass
            List<File> temp = inputTapes;
            inputTapes = outputTapes;
            outputTapes = temp;
        }
    }

    private static int mergePass(List<File> inputTapes, List<File> outputTapes, int pass) throws IOException {
        try (BufferedReader reader1 = new BufferedReader(new FileReader(inputTapes.get(0)));
             BufferedReader reader2 = new BufferedReader(new FileReader(inputTapes.get(1)));
             PrintWriter writer1 = new PrintWriter(new FileWriter(outputTapes.get(0)));
             PrintWriter writer2 = new PrintWriter(new FileWriter(outputTapes.get(1)))) {

            String line1 = reader1.readLine();
            String line2 = reader2.readLine();
            int linesWritten = 0;
            int runLines1 = 0;
            int runLines2 = 0;
            boolean writeToFirst = true;
            PrintWriter currentWriter = writer1;

            while (line1 != null || line2 != null) {
                // Start a new run if previous run is complete
                if (runLines1 >= runLength * (1 << pass) || (runLines1 > 0 && line1 == null)) {
                    appendRemaining(reader2, currentWriter, line2);
                    line2 = null;
                    runLines1 = 0;
                    runLines2 = 0;
                    writeToFirst = !writeToFirst;
                    currentWriter = writeToFirst ? writer1 : writer2;
                    continue;
                }
                if (runLines2 >= runLength * (1 << pass) || (runLines2 > 0 && line2 == null)) {
                    appendRemaining(reader1, currentWriter, line1);
                    line1 = null;
                    runLines1 = 0;
                    runLines2 = 0;
                    writeToFirst = !writeToFirst;
                    currentWriter = writeToFirst ? writer1 : writer2;
                    continue;
                }

                // Merge current pair
                if (line1 == null) {
                    currentWriter.println(line2);
                    line2 = reader2.readLine();
                    runLines2++;
                    linesWritten++;
                } else if (line2 == null) {
                    currentWriter.println(line1);
                    line1 = reader1.readLine();
                    runLines1++;
                    linesWritten++;
                } else {
                    if (line1.compareTo(line2) <= 0) {
                        currentWriter.println(line1);
                        line1 = reader1.readLine();
                        runLines1++;
                    } else {
                        currentWriter.println(line2);
                        line2 = reader2.readLine();
                        runLines2++;
                    }
                    linesWritten++;
                }
            }

            System.err.println("Pass " + pass + ": " + linesWritten + " lines merged");
            return linesWritten;
        }
    }

    private static void appendRemaining(BufferedReader reader, PrintWriter writer, String currentLine) throws IOException {
        if (currentLine != null) {
            writer.println(currentLine);
            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }
        }
    }

    private static void outputToStdout(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    private static int countLines(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int lines = 0;
            while (reader.readLine() != null) {
                lines++;
            }
            return lines;
        }
    }
}