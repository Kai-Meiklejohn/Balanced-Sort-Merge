@echo off
REM filepath: /c:/Users/kai/Balanced-Sort-Merge-assignment/test.bat
echo Compiling XSort...
javac XSort.java
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed.
    exit /b 1
)

echo Creating test input file...
(
    echo The quick brown fox jumps over the lazy dog.
    echo A journey of a thousand miles begins with a single step.
    echo All that glitters is not gold.
    echo Don't count your chickens before they hatch.
    echo Every cloud has a silver lining.
    echo Fortune favors the bold.
    echo Actions speak louder than words.
    echo Better late than never.
    echo Curiosity killed the cat.
    echo Early bird catches the worm.
    echo A penny saved is a penny earned.
    echo When in Rome, do as the Romans do.
) > test_input.txt

echo.
echo === Testing with run length 64, 2-way merge ===
type test_input.txt | java XSort 64 2 > xsort_output.txt
type test_input.txt | sort > expected_output.txt

echo.
echo === Comparing results with expected output ===
fc /n xsort_output.txt expected_output.txt > nul
if %ERRORLEVEL% EQU 0 (
    echo [PASS] Output matches expected sorting
) else (
    echo [FAIL] Output does not match expected sorting
    echo --- Expected output: ---
    type expected_output.txt
    echo --- Your output: ---
    type xsort_output.txt
)

echo.
echo === Hard test case with special characters and mixed content ===
(
    echo Z is the last letter of the English alphabet.
    echo 123 is smaller than 456 numerically.
    echo "Quoted text should sort correctly" - Anonymous
    echo 10 apples cost more than 5 apples.
    echo A sentence that starts with A.
    echo Another sentence that starts with A.
    echo !Special characters may affect sorting.
    echo 99 bottles of beer on the wall...
    echo @ symbols can be troublesome.
) > hard_test.txt

echo.
echo === Running hard test case ===
type hard_test.txt | java XSort 64 2 > xsort_hard_output.txt
type hard_test.txt | sort > expected_hard_output.txt

echo === Comparing hard test results ===
fc /n xsort_hard_output.txt expected_hard_output.txt > nul
if %ERRORLEVEL% EQU 0 (
    echo [PASS] Hard test output matches expected sorting
) else (
    echo [FAIL] Hard test output does not match expected sorting
    echo --- Expected output: ---
    type expected_hard_output.txt
    echo --- Your output: ---
    type xsort_hard_output.txt
)

echo.
echo Cleanup...
del test_input.txt
del hard_test.txt
del xsort_output.txt
del expected_output.txt
del xsort_hard_output.txt
del expected_hard_output.txt