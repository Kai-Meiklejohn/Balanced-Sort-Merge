// XSort.java
// Combines XSplit and XMerge
// Name: Kai Meiklejohn
// ID: 1632448
// Solo project: 2-way sort merge
// March 2025

import java.io.*;

public class XSort {
    public static void main(String[] args) throws IOException {
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java XSort <run_length: 64-1024> [2]");
            System.exit(1);
        }

        // Execute XSplit
        ProcessBuilder splitPb = new ProcessBuilder("java", "XSplit", args[0]);
        if (args.length == 2)
            splitPb.command().add(args[1]);
        splitPb.inheritIO();
        Process splitProcess = splitPb.start();
        try {
            int exitCode = splitProcess.waitFor();
            if (exitCode != 0) {
                System.err.println("XSplit failed with exit code " + exitCode);
                System.exit(exitCode);
            }
        } catch (InterruptedException e) {
            System.err.println("XSplit interrupted: " + e.getMessage());
            System.exit(1);
        }

        // If we're doing a 2-way merge, execute XMerge
        if (args.length == 2) {
            if (!args[1].equals("2")) {
                System.out.println("Solo project supports only 2-way merge. Use 2 as second argument.");
                System.exit(1);
            }
            ProcessBuilder mergePb = new ProcessBuilder("java", "XMerge", args[0], "2");
            mergePb.inheritIO();
            Process mergeProcess = mergePb.start();
            try {
                int exitCode = mergeProcess.waitFor();
                if (exitCode != 0) {
                    System.err.println("XMerge failed with exit code " + exitCode);
                    System.exit(exitCode);
                }
            } catch (InterruptedException e) {
                System.err.println("XMerge interrupted: " + e.getMessage());
                System.exit(1);
            }
        }
    }
}