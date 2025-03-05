@echo off
REM filepath: C:\Users\kai\Balanced-Sort-Merge-assignment\test.bat
REM Improved test script for XSort: generates large input and tests 2-way merge

echo Compiling XSort...
javac XSort.java
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed.
    exit /b 1
)

echo Creating large test input file with 200 lines...
(
    REM Generate 200 lines with variety: sentences, numbers, special characters
    for /l %%i in (1,1,50) do (
        echo Line %%i: The quick brown fox jumps over the lazy dog.
        echo Line %%i: A journey of %%i miles begins with a single step.
        echo Line %%i: "Quoted text with number %%i" - Anonymous
        echo Line %%i: !@#$%%^&* Special chars test %%i
    )
) > test_input.txt

echo.
echo === Testing with run length 64, 2-way merge ===
type test_input.txt | java XSort 64 2 > xsort_output.txt
if %ERRORLEVEL% NEQ 0 (
    echo XSort execution failed.
    exit /b 1
)
type test_input.txt | sort > expected_output.txt

echo.
echo === Comparing results with expected output ===
fc /n xsort_output.txt expected_output.txt > nul
if %ERRORLEVEL% EQU 0 (
    echo [PASS] Output matches expected sorting
) else (
    echo [FAIL] Output does not match expected sorting
    echo --- Expected output (first 10 lines): ---
    type expected_output.txt | more +0 -10
    echo --- Your output (first 10 lines): ---
    type xsort_output.txt | more +0 -10
)

echo.
echo Cleanup...
del test_input.txt
del xsort_output.txt
del expected_output.txt