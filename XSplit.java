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
            System.err.println("Outputting runs to runs.txt");
            outputRunsToFile(runs, new File("runs.txt"));
        }
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
        }

        return runFiles;
    }

    // Modified to output one run at a time from each file alternately
    private static void outputRunsToFile(List<File> runFiles, File outputFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
             BufferedReader reader1 = new BufferedReader(new FileReader(runFiles.get(0)));
             BufferedReader reader2 = new BufferedReader(new FileReader(runFiles.get(1)))) {
            
            String line1 = reader1.readLine();
            String line2 = reader2.readLine();
            boolean fromFirst = true;  // Start with runs1.txt

            while (line1 != null || line2 != null) {
                int linesWritten = 0;
                if (fromFirst && line1 != null) {
                    // Write one run from runs1.txt
                    while (linesWritten < runLength && line1 != null) {
                        writer.println(line1);
                        line1 = reader1.readLine();
                        linesWritten++;
                    }
                    fromFirst = false;  // Switch to runs2.txt next
                } else if (!fromFirst && line2 != null) {
                    // Write one run from runs2.txt
                    while (linesWritten < runLength && line2 != null) {
                        writer.println(line2);
                        line2 = reader2.readLine();
                        linesWritten++;
                    }
                    fromFirst = true;  // Switch back to runs1.txt next
                } else {
                    // If one file is exhausted, finish the other
                    if (line1 != null) {
                        while (line1 != null) {
                            writer.println(line1);
                            line1 = reader1.readLine();
                        }
                    } else if (line2 != null) {
                        while (line2 != null) {
                            writer.println(line2);
                            line2 = reader2.readLine();
                        }
                    }
                    break;
                }
            }
        }

        // Clean up temporary files
        for (File runFile : runFiles) {
            if (runFile.exists() && !runFile.delete()) {
                System.err.println("Failed to delete " + runFile.getName());
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
}