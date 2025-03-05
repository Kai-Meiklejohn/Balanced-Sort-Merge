// Balanced Sort Merge Assignment
// Name: Kai Meiklejohn, ID: 1632448
// Solo project: 2-way sort merge
// March 2025

import java.io.IOException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class XSort {
   /**
     * main entry point which alidates args and orchestrates run creation and merging
     * @param args [0]: run length (64–1024), [1]: optional "2" for merge mode
     */
    public static void main(String[] args) throws IOException {
        // check args, validate run length, check merge mode, call createInitialRuns and mergeRuns2Way


    }

    /**
     * creates initial sorted runs from standard input using heapsort
     * @param runLength number of lines per run (64–1024)
     * @return list of temporary files containing sorted runs
     */
    private static List<File> createInitialRuns(int runLength) throws IOException {
        // read from System.in, build runs, sort with heapSort, save to temp files


        return new ArrayList<>();
    }

    /**
     * writes a sorted run to a temporary file
     * @param lines sorted list of lines
     * @param prefix file prefix (e.g. "run0")
     * @return temporary file object
     */
    private static File writeRunToTempFile(List<String> lines, String prefix) throws IOException {
        // write lines to a temp file, preserving contents



        return null;
    }

    /**
     * sorts an ArrayList of strings in ascending order using heapsort
     * @param list list of strings to sort
     */
    private static void heapSort(List<String> list) {
        // Build max-heap, then extract elements to sort


    }

    /**
     * heapifies a subtree rooted at index i
     * @param list list to heapify
     * @param n Size of the heap
     * @param i root index of subtree
     */
    private static void heapify(List<String> list, int n, int i) {
        // ensure max heap property by comparing and swapping down



    }

    /**
     * does a 2-way balanced merge on the initial runs
     * @param runFiles list of temp files with sorted runs
     */
    private static void mergeRuns2Way(List<File> runFiles) throws IOException {
        // distribute runs, perform merge passes, output final run



    }

    /**
     * Distributes initial runs across two temp files
     * @param runFiles List of run files
     * @param temp1 first temp file
     * @param temp2 second temp file
     */
    private static void distributeRuns(List<File> runFiles, File temp1, File temp2) throws IOException {
        // split runs into temp1 (odd) and temp2 (even)
    }

    /**
     * Merges runs from two input files into two output files
     * @param in1 first input file
     * @param in2 second input file
     * @param out1 first output file
     * @param out2 second output file
     * @param runsPerFile Number of runs per input file
     */
    private static void mergePass(File in1, File in2, File out1, File out2, int runsPerFile) throws IOException {
        // Merge pairs of runs, alternating outputs



    }

    /**
     * outputs a run file to standard output
     * @param runFile File to output
     */
    private static void outputRunToStdout(File runFile) throws IOException {
        // Write run contents to System.out


    }
}