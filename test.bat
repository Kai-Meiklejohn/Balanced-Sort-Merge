@echo off
REM filepath: C:\Users\kai\Balanced-Sort-Merge-assignment\test.bat
REM Improved test script for XSort: Tests MobyDick.txt, BrownCorpus.txt, and a generated input

echo Compiling XSort...
javac XSort.java
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed.
    exit /b 1
)

echo.
echo === Testing MobyDick.txt with run length 64, 2-way merge ===
type MobyDick.txt | java XSort 64 2 > moby_xsort.txt
if %ERRORLEVEL% NEQ 0 (
    echo XSort execution failed for MobyDick.txt.
    exit /b 1
)
type MobyDick.txt | sort > moby_expected.txt
fc /n moby_xsort.txt moby_expected.txt > nul
if %ERRORLEVEL% EQU 0 (
    echo [PASS] MobyDick.txt matches expected sorting
    sort /c moby_xsort.txt
    if %ERRORLEVEL% EQU 0 (
        echo [PASS] MobyDick.txt is sorted correctly
    ) else (
        echo [FAIL] MobyDick.txt is not sorted
    )
) else (
    echo [FAIL] MobyDick.txt does not match expected sorting
    echo --- Expected (first 10 lines): ---
    type moby_expected.txt | more +0 -10
    echo --- Your output (first 10 lines): ---
    type moby_xsort.txt | more +0 -10
)

echo.
echo === Testing BrownCorpus.txt with run length 64, 2-way merge ===
type BrownCorpus.txt | java XSort 64 2 > brown_xsort.txt
if %ERRORLEVEL% NEQ 0 (
    echo XSort execution failed for BrownCorpus.txt.
    exit /b 1
)
type BrownCorpus.txt | sort > brown_expected.txt
fc /n brown_xsort.txt brown_expected.txt > nul
if %ERRORLEVEL% EQU 0 (
    echo [PASS] BrownCorpus.txt matches expected sorting
    sort /c brown_xsort.txt
    if %ERRORLEVEL% EQU 0 (
        echo [PASS] BrownCorpus.txt is sorted correctly
    ) else (
        echo [FAIL] BrownCorpus.txt is not sorted
    )
) else (
    echo [FAIL] BrownCorpus.txt does not match expected sorting
    echo --- Expected (first 10 lines): ---
    type brown_expected.txt | more +0 -10
    echo --- Your output (first 10 lines): ---
    type brown_xsort.txt | more +0 -10
)

echo.
echo === Testing generated input (200 lines) with run length 64, 2-way merge ===
(
    for /l %%i in (1,1,50) do (
        echo Line %%i: The quick brown fox jumps over the lazy dog.
        echo Line %%i: A journey of %%i miles begins with a single step.
        echo Line %%i: "Quoted text with number %%i" - Anonymous
        echo Line %%i: !@#$%%^&* Special chars test %%i
    )
) > test_input.txt
type test_input.txt | java XSort 64 2 > test_xsort.txt
if %ERRORLEVEL% NEQ 0 (
    echo XSort execution failed for generated input.
    exit /b 1
)
type test_input.txt | sort > test_expected.txt
fc /n test_xsort.txt test_expected.txt > nul
if %ERRORLEVEL% EQU 0 (
    echo [PASS] Generated input matches expected sorting
    sort /c test_xsort.txt
    if %ERRORLEVEL% EQU 0 (
        echo [PASS] Generated input is sorted correctly
    ) else (
        echo [FAIL] Generated input is not sorted
    )
) else (
    echo [FAIL] Generated input does not match expected sorting
    echo --- Expected (first 10 lines): ---
    type test_expected.txt | more +0 -10
    echo --- Your output (first 10 lines): ---
    type test_xsort.txt | more +0 -10
)

echo.
echo Cleanup...
del moby_xsort.txt moby_expected.txt brown_xsort.txt brown_expected.txt test_input.txt test_xsort.txt test_expected.txt