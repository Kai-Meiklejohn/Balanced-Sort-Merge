# Balanced-Sort-Merge-assignment
Name: Kai Meiklejohn
Student ID: 1632448  
Solo project implementing a 2-way balanced sort merge with heapsort.

## Overview
This project implements an external sorting algorithm using a 2-way balanced sort merge, as specified for a solo solution. It sorts text files line-by-line (like the Linux sort command) by:

1. Creating initial sorted runs using heapsort.
2. Merging those runs with a 2-way balanced merge process.

The program consists of three components:
- XSort.java - Main wrapper that coordinates the process
- XSplit.java - Creates initial sorted runs using heapsort
- XMerge.java - Performs the balanced 2-way merge

The program reads from standard input (System.in), writes to standard output (System.out), and uses temporary files for merging. It's designed to handle large datasets efficiently by processing data in chunks.

## Usage Instructions
Compile all components: `javac XSort.java XSplit.java XMerge.java`

Run:
- Create runs only: `cat input.txt | java XSort <run_length>`
- Full sort (runs + merge): `cat input.txt | java XSort <run_length> 2 > output.txt`

Arguments:
- `<run_length>`: Number of lines per run (64-1024). Example: 64, 512.
- `2`: Optional, triggers 2-way merge. Must be "2" for solo project.

Examples:
- `echo "zebra\napple\ncat" | java XSort 64 2 > sorted.txt` (sorts 3 lines).
- `cat MobyDick.txt | java XSort 512 2 > Moby.sorted` (sorts a large file).

## Implementation Details

### XSort
The main controller program that:
1. Processes command-line arguments
2. Launches XSplit to create initial runs
3. If requested, launches XMerge to perform balanced merge
4. Handles process coordination and error management

### XSplit
Creates initial sorted runs from standard input:
1. Reads lines into memory up to the run length limit
2. Sorts each batch using heapsort
3. Writes sorted runs to two alternating files: runs1.txt and runs2.txt
4. If run without merge step, combines all runs into a single output file

### XMerge
Performs balanced 2-way merge of sorted runs:
1. Takes the initial runs from runs1.txt and runs2.txt
2. Executes multiple merge passes, swapping input/output tapes
3. During each pass, merges pairs of runs while preserving sort order
4. Outputs final sorted result to standard output
5. Cleans up temporary files

## Tools and Resources Used
- [Heap Sort Visualisation](https://www.cs.usfca.edu/~galles/visualization/HeapSort.html)
- [Heap Sort Tutorial](https://www.geeksforgeeks.org/heap-sort/)
- [Merge Sort Tutorial](https://www.geeksforgeeks.org/merge-sort/)

## Pseudocode (High-Level Description)
Below is a plain-English breakdown of the program's algorithm, split into three components that work together to implement a balanced 2-way sort merge.

### XSort (Main Wrapper)
**Purpose:** Coordinate the execution of XSplit and XMerge programs.

**Algorithm:**
- Check command-line arguments (1 or 2 args allowed)
- Parse run length from first arg
- Launch XSplit process, passing run length and optional second arg
- If second arg is "2":
  - Launch XMerge process, passing run length and "2"
  - Redirect XMerge output to standard output
- Handle any process errors or interruptions

### XSplit (Run Creation)
**Purpose:** Create initial sorted runs and distribute them between two files.

**Algorithm:**
- Initialise empty list for collecting lines
- Create two output files: runs1.txt and runs2.txt
- For each line from standard input:
  - Add line to list
  - If list reaches run_length:
    - Sort list using heapsort
    - Write sorted lines to alternating output file (runs1.txt or runs2.txt)
    - Clear list and start collecting for next run
- Sort and write any remaining lines in final partial run
- If only run creation was requested (no merge):
  - Combine all runs into a single output file (runs.txt)
  - Clean up temporary files

### XMerge (Balanced Merge)
**Purpose:** Perform balanced 2-way merge on pre-created runs.

**Algorithm:**
- Check if input run files exist (runs1.txt and runs2.txt)
- Count total lines across run files
- Create two temporary files for merging (temp1.txt and temp2.txt)
- While more than one run exists:
  - Clear output files
  - Read from current input files, write to output files
  - For each run pair:
    - Compare heads of runs and write smaller value
    - Keep track of run boundaries
    - Alternate output files for consecutive merged runs
  - Swap input and output files for next pass
  - Double effective run length for next pass
- When only one run remains:
  - Output final sorted data to standard output
  - Clean up all temporary files

### HeapSort Implementation
**Purpose:** Sort a list of lines in-place using heap structure.

**Algorithm:**
- Build max heap (largest element at root):
  - Start from last non-leaf node down to root
  - For each node, ensure it's larger than its children
- Extract sorted elements:
  - Swap root (largest) with last unsorted element
  - Reduce heap size by 1
  - Fix heap property by moving new root down to correct position
  - Repeat until all elements are sorted