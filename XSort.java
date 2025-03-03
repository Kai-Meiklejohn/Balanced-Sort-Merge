// Balanced Sort Merge Assignment
// Name: Kai Meiklejohn, ID: 1632448
// Solo project: 2-way sort merge
// March 2025

import java.io.IOException;

public class XSort {
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java XSort <run_length: 64-1024> [2]");
            System.exit(1);
        }
        int runLength = Integer.parseInt(args[0]);
        if (runLength < 64 || runLength > 1024) {
            System.out.println("Run length must be between 64 and 1024.");
            System.exit(1);
        }
        if (args.length == 2 && !args[1].equals("2")) {
            System.out.println("Solo project supports only 2-way merge. Use '2' as second argument.");
            System.exit(1);
        }
        System.out.println("Run length: " + runLength + ", Merge type: 2-way");
        // Add heapsort and merge logic here later
    }

    // method for heapsort
    private static void heapSort(String[] array) {
        // Implementation will go here
    }

    // Helper methods for heapSort
    private static void buildHeap(String[] array) {
        // Implementation will go here
    }

    private static void heapify(String[] array, int heapSize, int rootIndex) {
        // Implementation will go here
    }

    // method for creating initial sorted runs
    private static void createInitialRuns(int runLength) throws IOException {
        // Implementation will go here - reads from stdin, uses heapsort, creates temp files
    }

    // method for 2-way merge sort
    private static void mergeRuns() throws IOException {
        // Implementation will go here - performs balanced 2-way merge
    }
    
}