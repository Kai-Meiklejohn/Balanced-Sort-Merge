// Balanced Sort Merge Assignment
// Student: [Your Name], ID: 1632448
// Solo project: 2-way sort merge
// Created for COMP SCI course, March 2025

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
}