# Balanced-Sort-Merge-assignment
Student ID: 1632448  
Solo project implementing a 2-way balanced sort merge with heapsort.  
Runs on Windows with OpenJDK 21.0.6.

## Overview
This project implements an external sorting algorithm using a 2-way balanced sort merge, as specified for a solo solution. It sorts text files line-by-line (like the Linux sort command) by:

1. Creating initial sorted runs using heapsort.
2. Merging those runs with a 2-way balanced merge process.

The program, XSort, reads from standard input (System.in), writes to standard output (System.out), and uses temporary files for merging. It's designed to handle large datasets efficiently by processing data in chunks.

## Usage Instructions
Compile: `javac XSort.java`

Run:
- Create runs only: `cat input.txt | java XSort <run_length>`
- Full sort (runs + merge): `cat input.txt | java XSort <run_length> 2 > output.txt`

Arguments:
- `<run_length>`: Number of lines per run (64–1024). Example: 64, 512.
- `2`: Optional, triggers 2-way merge (solo mode). Other values cause an error.

Examples:
- `echo "zebra\napple\ncat" | java XSort 64 2 > sorted.txt` (sorts 3 lines).
- `cat MobyDick.txt | java XSort 512 2 > Moby.sorted` (sorts a large file).

## Tools and Resources Used for the Assignment
- [Heap Sort Visualization](https://www.cs.usfca.edu/~galles/visualization/HeapSort.html)
- [Heap Sort Tutorial](https://www.geeksforgeeks.org/heap-sort/)
- [Merge Sort Tutorial](https://www.geeksforgeeks.org/merge-sort/)
- AI Assistance (GitHub Copilot): Helped with pseudocode structure, debugging PATH issues, and clarifying assignment requirements. All code is my own implementation.

## Pseudocode (High-Level Description)
Below is a plain-English breakdown of the XSort algorithm, split into key functions. It processes lines from standard input, sorts them with heapsort, and merges runs using a 2-way balanced approach.

### Main Program
**Start:**
- Check command-line arguments (1 or 2 args allowed).
- If too few/many args, show error: "Usage: run_length 64–1024, optional 2".

**Set Run Length:**
- Take first arg as run length (convert to number).
- If run length < 64 or > 1024, show error and stop.

**Check Merge Mode:**
- If second arg exists and isn't "2", show error: "Solo mode requires 2-way merge" and stop.

**Run Steps:**
- Create initial sorted runs from input.
- If second arg is "2", merge runs into final sorted output.

### Create Initial Runs
**Purpose:** Break input into chunks (runs) of up to run_length lines, sort each with heapsort, and save to temp files.

**Steps:**
- Make an empty list for up to run_length lines.
- For each line from standard input:
  - Add line to list (keep original newline characters).
  - If list reaches run_length:
    - Sort list with heapsort.
    - Write sorted lines to a temp file (e.g., "run0").
    - Clear list, add current line, increment run count.
- If any lines remain in list:
  - Sort with heapsort, write to final run file.
- Return list of temp file names.

### Heapsort
**Purpose:** Sort a list of lines in-place (ascending order) using a max-heap structure.

**Steps:**
- Build a max-heap:
  - Start from last parent node to root.
  - For each node, ensure it's larger than its children (heapify down).
- Extract sorted order:
  - Swap root (largest) with last element.
  - Reduce heap size by 1, heapify root down.
  - Repeat until all elements are sorted.
- Compare lines as strings (e.g., "apple" < "zebra").

### 2-Way Balanced Merge
**Purpose:** Combine all sorted runs into one final sorted output using two temp files.

**Steps:**
- Create two temp files: temp1 and temp2.
- Distribute Runs:
  - Put odd-numbered runs into temp1, even into temp2.
- Merge Passes:
  - While more than one run exists:
    - Make two new output files: out1, out2.
    - Merge pairs of runs from temp1 and temp2:
      - Read one line from each input file.
      - Compare lines, write smaller to current output (alternate out1, out2 per run).
      - Advance the file with the used line.
    - Continue until both runs are merged.
    - Replace temp1, temp2 with out1, out2 as new inputs.
    - Update run list (fewer runs after each pass).
- Final Output:
  - Write the last temp file's contents to standard output.
  - Delete all temp files.

### Merge Pass Helper
**Purpose:** Merge pairs of runs from two input files into two output files.

**Steps:**
- Open readers for temp1, temp2 and writers for out1, out2.
- For each pair of runs (up to total run count):
  - Alternate output between out1 and out2.
  - While either input has lines:
    - If one input is empty, write from the other.
    - Otherwise, compare lines, write smaller, advance that input.
- Close all files.