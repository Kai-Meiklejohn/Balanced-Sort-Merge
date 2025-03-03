@echo off
echo Compiling XSort...
javac XSort.java
if %ERRORLEVEL% EQU 0 (
    echo Running test with sample input...
    echo apple&echo zebra&echo cat | java XSort 64 2
) else (
    echo Compilation failed.
)