# Build from source

## Prerequisites

JDK 8 or later installed
Maven installed

## Build

on windows, run build.bat
"mvn clean install -DskipTests" will bexecuted

# Binaries

(prebuild and clean build) binaries can be found in the folder /target

on windows
run startBenchmark.bat for JMH benchmark
run startInteractive.bat for running costum comparisons

# How to use the interactive mode

It is a simple console application which runs a configurable benchmark comparison and outputs the result in a plot and some metric number in a .txt file.

The benchmark's name is derived from the configuration containing Problemset, Iterations and Runtime. This name will be used for the filenames as well.

The Input can be an option or a variable free to choose. Here is an example:

```
Run Main...
--- TH OWL ATA EXAM PROGRAM ---
choose problem set:
(0) uf100_430 with 1000 problems
(1) uf150_645 with 100 problems
(2) uf20_91 with 1000 problems
(3) uf200_860 with 100 problems
(4) uf50_218 with 1000 problems
(5) uuf100_430 with 1000 problems
(6) uuf150_645 with 100 problems
(7) uuf200_860 with 100 problems
(8) uf250_1065 with 100 problems
(9) uuf250_1065 with 100 problems
(10) uuf50_218 with 1000 problems

Input -> 5

uuf100_430
choose problem size, 1 to 1000:

Input -> 1000

choose time in milliseconds for each algorithm and problem:

Input -> 10000

add algorithms to compare:
(1) Add Simulated Annealing
(2) Add Artificial Bee Colony
(3) Add Standard Set
(4) Next
Added: []

Input -> 1

enter initial temperature:

Input -> 100

(1) Add Simulated Annealing
(2) Add Artificial Bee Colony
(3) Add Standard Set
(4) Next
Added: [sa_t100]

Input -> 2

enter colony size:

Input -> 100

enter abandonment number:

Input -> 5

(1) Add Simulated Annealing
(2) Add Artificial Bee Colony
(3) Add Standard Set
(4) Next
Added: [abc_c100_a5, sa_t100]

Input -> 4

uuf100_430_10_10000ms with 2 algoritms and 1000 problems could take up to 333 minutes
Starting abc_c100_a5 ...
Run 1 of 10 completed.
Run 2 of 10 completed.
Run 3 of 10 completed.
Run 4 of 10 completed.
...

```


After this, uuf100_430_10_10000ms.png plots the result and uuf100_430_10_10000ms.txt contains final statistics after the last execution step