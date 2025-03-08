// XMerge.java
// Performs 2-way merge on pre-created runs
// Name: Kai Meiklejohn
// ID: 1632448

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class XMerge {
    private static int runLength;

    // main method - sets up everything and does argument checking
    // also handles all the file setup stuff before we can begin merging
    public static void main(String[] args) throws IOException {
        // check if we got the right args
        if (args.length != 2 || !args[1].equals("2")) {
            System.out.println("Usage: java XMerge <run_length: 64-1024> 2");
            System.exit(1);
        }

        // pass the run length and make sure its valid
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

        // setup all our tapes (files) that we need for the merge algorithm
        File tape1 = new File("runs1.txt");
        File tape2 = new File("runs2.txt");
        File temp1 = new File("temp1.txt");
        File temp2 = new File("temp2.txt");

        // make sure our input files actally exist before we try to merge
        if (!tape1.exists() || !tape2.exists()) {
            System.err.println("Input run files (runs1.txt or runs2.txt) are missing.");
            System.exit(1);
        }

        // setup our tapes for the 2 way merging
        List<File> inputTapes = new ArrayList<>();
        inputTapes.add(tape1);
        inputTapes.add(tape2);
        List<File> outputTapes = new ArrayList<>();
        outputTapes.add(temp1);
        outputTapes.add(temp2);

        int totalLines = countLines(tape1) + countLines(tape2);
        performBalancedMerge(inputTapes, outputTapes, totalLines);

        // cleanup all the files after we are done
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

    // does the actual balanced merge algorithm by running multiple passes
    // keeps swapping input/output tapes until everything is merged
    private static void performBalancedMerge(List<File> inputTapes, List<File> outputTapes, int totalLines) throws IOException {
        int pass = 0;
        int numRuns = (int) Math.ceil((double) totalLines / runLength); // Initial number of runs

        while (numRuns > 1) {
            // make shure output tapes r clean before writing
            for (File file : outputTapes) {
                if (file.exists() && !file.delete()) {
                    System.err.println("Failed to delete " + file.getName());
                }
            }

            int linesMerged = mergePass(inputTapes, outputTapes, pass);
            // calc how many runs we have aftr this pass - run size doubles each time
            numRuns = (int) Math.ceil((double) linesMerged / (runLength * (1 << pass))); // Runs after this pass

            // if were done merging jus print to stdout and finish
            if (numRuns <= 1 && countLines(outputTapes.get(0)) == totalLines) {
                outputToStdout(outputTapes.get(0));
                break;
            }

            // swap input and output tapes for next pass
            List<File> temp = inputTapes;
            inputTapes = outputTapes;
            outputTapes = temp;
            pass++;
        }
    }

    // does a single merge pass, taking 2 input tapes and creating 2 output tapes
    // alternates which output tape it writes to for each pair of runs
    private static int mergePass(List<File> inputTapes, List<File> outputTapes, int pass) throws IOException {
        try (BufferedReader reader1 = new BufferedReader(new FileReader(inputTapes.get(0)));
             BufferedReader reader2 = new BufferedReader(new FileReader(inputTapes.get(1)));
             PrintWriter writer1 = new PrintWriter(new FileWriter(outputTapes.get(0)));
             PrintWriter writer2 = new PrintWriter(new FileWriter(outputTapes.get(1)))) {

            String line1 = reader1.readLine();
            String line2 = reader2.readLine();
            int linesWritten = 0;
            // run size gets bigger each pass (doubles)
            int currentRunSize = runLength * (1 << pass);
            int runLines1 = 0;
            int runLines2 = 0;
            boolean writeToFirst = true;
            PrintWriter currentWriter = writer1;

            while (line1 != null || line2 != null) {
                // merge one pair of runs at a time
                while (runLines1 < currentRunSize && runLines2 < currentRunSize && line1 != null && line2 != null) {
                    // take the smaller element first
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

                // gotta finish the current run if one inptu is done
                if (runLines1 >= currentRunSize || line1 == null) {
                    // finish off tape2 run
                    while (runLines2 < currentRunSize && line2 != null) {
                        currentWriter.println(line2);
                        line2 = reader2.readLine();
                        runLines2++;
                        linesWritten++;
                    }
                    runLines1 = 0;
                    runLines2 = 0;
                    // switch to next output tape if theres more data
                    if (line1 != null || line2 != null) {
                        writeToFirst = !writeToFirst;
                        currentWriter = writeToFirst ? writer1 : writer2;
                    }
                } else if (runLines2 >= currentRunSize || line2 == null) {
                    // finish off tape1 run
                    while (runLines1 < currentRunSize && line1 != null) {
                        currentWriter.println(line1);
                        line1 = reader1.readLine();
                        runLines1++;
                        linesWritten++;
                    }
                    runLines1 = 0;
                    runLines2 = 0;
                    // switch to next output tape if theres more data
                    if (line1 != null || line2 != null) {
                        writeToFirst = !writeToFirst;
                        currentWriter = writeToFirst ? writer1 : writer2;
                    }
                }
            }

            return linesWritten;
        }
    }

    // dump the final sorted data to stdout
    // once we got 1 big sorted file we just print it all out
    private static void outputToStdout(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    // count how many lines in a file, usefull for various calc
    // helps us know how big our files are and when we are done
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