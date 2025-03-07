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

        String[] splitArgs = args.length == 1 ? new String[]{"XSplit", args[0]} : new String[]{"XSplit", args[0], "2"};
        ProcessBuilder splitPb = new ProcessBuilder("java", splitArgs[0], splitArgs[1]);
        if (args.length == 2) splitPb.command().add("2");
        splitPb.inheritIO();
        Process splitProcess = splitPb.start();
        try {
            splitProcess.waitFor();
        } catch (InterruptedException e) {
            System.err.println("XSplit interrupted: " + e.getMessage());
            System.exit(1);
        }

        if (args.length == 2) {
            if (!args[1].equals("2")) {
                System.out.println("Solo project supports only 2-way merge. Use 2 as second argument.");
                System.exit(1);
            }
            ProcessBuilder mergePb = new ProcessBuilder("java", "XMerge", args[0], "2");
            mergePb.inheritIO();
            Process mergeProcess = mergePb.start();
            try {
                mergeProcess.waitFor();
            } catch (InterruptedException e) {
                System.err.println("XMerge interrupted: " + e.getMessage());
                System.exit(1);
            }
        }
    }
}