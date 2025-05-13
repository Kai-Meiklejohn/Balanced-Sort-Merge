// XSort.java
// Combines XSplit and XMerge
// Name: Kai Meiklejohn
// Solo project: 2-way sort merge

import java.io.*;

public class XSort {
    public static void main(String[] args) throws IOException {
        // make sure we got enough args but not too many
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java XSort <run_length: 64-1024> [2]");
            System.exit(1);
        }

        // execute first program that makes runs
        ProcessBuilder xSplitProcessBuilder = new ProcessBuilder("java", "XSplit", args[0]);
        if (args.length == 2)
            xSplitProcessBuilder.command().add(args[1]);  // add the 2nd arg if we got one
            
        // redirect stuff from parent process cause we need the input
        xSplitProcessBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
        xSplitProcessBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        
        Process splitProcess = xSplitProcessBuilder.start();
        try {
            // wait for XSplit to finish before continuing
            int exitCode = splitProcess.waitFor();
            if (exitCode != 0) {
                System.err.println("XSplit failed with exit code " + exitCode);
                System.exit(exitCode);
            }
        } catch (InterruptedException e) {
            // something went wrong with the process
            System.err.println("XSplit interrupted: " + e.getMessage());
            System.exit(1);
        }

        // if theres a second arg, do the merge part
        if (args.length == 2) {
            // sanity check - make sure they asked for 2-way merge
            if (!args[1].equals("2")) {
                System.out.println("Only supports 2-way merge. Use 2 as second argument.");
                System.exit(1);
            }
            
            // setup the merge program with same params
            ProcessBuilder xMergeProcessBuilder = new ProcessBuilder("java", "XMerge", args[0], "2");
            
            // need to get output back to parent proccess
            xMergeProcessBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            xMergeProcessBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
            
            // run XMerge and wait for it to finish
            Process mergeProcess = xMergeProcessBuilder.start();
            try {
                int exitCode = mergeProcess.waitFor();
                if (exitCode != 0) {
                    // trouble in paradise
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
