// XMerge.java
// Performs 2-way merge on pre-created runs
// Name: Kai Meiklejohn
// ID: 1632448

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// this class does balanced sort-merge to combine sorted runs
public class XMerge {
    private static int runLength;

    public static void main(String[] args) throws IOException {
        // check arguments is correct format
        if (args.length != 2 || !args[1].equals("2")) {
            System.out.println("Usage: java XMerge <run_length: 64-1024> 2");
            System.exit(1);
        }

        try {
            // make sure run length is valid number
            runLength = Integer.parseInt(args[0]);
            if (runLength < 64 || runLength > 1024) {
                System.out.println("Run length must be between 64 and 1024.");
                System.exit(1);
            }
        } catch (NumberFormatException e) {
            System.out.println("Run length must be an integer between 64 and 1024.");
            System.exit(1);
        }

        // setup input and temporary output files
        File tape1 = new File("runs1.txt");
        File tape2 = new File("runs2.txt");
        File temp1 = new File("temp1.txt");
        File temp2 = new File("temp2.txt");

        // cant proceed if input files dont exist
        if (!tape1.exists() || !tape2.exists()) {
            System.err.println("Input run files (runs1.txt or runs2.txt) are missing.");
            System.exit(1);
        }

        // store files in lists so we can swap them between passes
        List<File> inputTapes = new ArrayList<>();
        inputTapes.add(tape1);
        inputTapes.add(tape2);
        List<File> outputTapes = new ArrayList<>();
        outputTapes.add(temp1);
        outputTapes.add(temp2);

        // count how many lines we are working with
        int totalLines = countLines(tape1) + countLines(tape2);
        performBalancedMerge(inputTapes, outputTapes, totalLines);

        // clean up after we are done with merge
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

    // does the actual balanced merge across multiple passes until all items merged
    private static void performBalancedMerge(List<File> inputTapes, List<File> outputTapes, int totalLines) throws IOException {
        int numRuns = (int) Math.ceil((double) totalLines / runLength);

        // special case when everything fits in 1 run already
        if (numRuns <= 1) {
            File nonEmptyTape = countLines(inputTapes.get(0)) > 0 ? inputTapes.get(0) : inputTapes.get(1);
            if (totalLines > 0) {
                outputToStdout(nonEmptyTape);
            }
            return;
        }

        int pass = 0;
        while (numRuns > 1) {
            // delete old output tapes before each new pass
            for (File file : outputTapes) {
                if (file.exists() && !file.delete()) {
                    System.err.println("Failed to delete " + file.getName());
                }
            }

            // do a merge pass and calculate new run count
            int linesMerged = mergePass(inputTapes, outputTapes, pass);
            numRuns = (int) Math.ceil((double) linesMerged / (runLength * (1 << pass)));

            // if we merged everything into one tape, output it and finish
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

    // does a single pass of merging runs from input tapes to output tapes
    private static int mergePass(List<File> inputTapes, List<File> outputTapes, int pass) throws IOException {
        try (BufferedReader reader1 = new BufferedReader(new FileReader(inputTapes.get(0)));
             BufferedReader reader2 = new BufferedReader(new FileReader(inputTapes.get(1)));
             PrintWriter writer1 = new PrintWriter(new FileWriter(outputTapes.get(0)));
             PrintWriter writer2 = new PrintWriter(new FileWriter(outputTapes.get(1)))) {

            // setup for merging process
            String line1 = reader1.readLine();
            String line2 = reader2.readLine();
            int linesWritten = 0;
            int currentRunSize = runLength * (1 << pass);
            int runLines1 = 0;
            int runLines2 = 0;
            boolean writeToFirst = true;
            PrintWriter currentWriter = writer1;

            // keep going untill both input tapes are empty
            while (line1 != null || line2 != null) {
                // merge lines from both tapes in sorted order
                while (runLines1 < currentRunSize && runLines2 < currentRunSize && line1 != null && line2 != null) {
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

                // handle case when run1 is exhausted or tape1 is empty
                if (runLines1 >= currentRunSize || line1 == null) {
                    while (runLines2 < currentRunSize && line2 != null) {
                        currentWriter.println(line2);
                        line2 = reader2.readLine();
                        runLines2++;
                        linesWritten++;
                    }
                    runLines1 = 0;
                    runLines2 = 0;
                    if (line1 != null || line2 != null) {
                        writeToFirst = !writeToFirst;
                        currentWriter = writeToFirst ? writer1 : writer2;
                    }
                } else if (runLines2 >= currentRunSize || line2 == null) {
                    // handle case when run2 is exhausted or tape2 is empty
                    while (runLines1 < currentRunSize && line1 != null) {
                        currentWriter.println(line1);
                        line1 = reader1.readLine();
                        runLines1++;
                        linesWritten++;
                    }
                    runLines1 = 0;
                    runLines2 = 0;
                    if (line1 != null || line2 != null) {
                        writeToFirst = !writeToFirst;
                        currentWriter = writeToFirst ? writer1 : writer2;
                    }
                }
            }

            return linesWritten;
        }
    }

    // prints contents of a file to standard output
    private static void outputToStdout(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    // counts number of lines in file, used to determine total data size
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