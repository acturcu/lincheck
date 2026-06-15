# Evaluation Benchmarks

Run commands from the repository root.

## Run

Run all benchmarks once in adaptive mode:

```powershell
.\gradlew.bat :integration-test:lincheck:loopEval --args="ADAPTIVE 1"
```

The arguments are:

```text
<mode-label> <repetitions>
```

The mode label is recorded in the results. Supported labels are `BASELINE`,
`BOUNDED`, and `ADAPTIVE`. The default arguments are `ADAPTIVE 1`.

Currently, the mode label is used only for the naming of the output CSV file. The detector must be modified manually in `ManagedStrategy.kt`.

Select a suite with `-Plincheck.loopEval.suite=core|micro|all`:

```powershell
.\gradlew.bat :integration-test:lincheck:loopEval --args="ADAPTIVE 10" -Plincheck.loopEval.suite=core
```

Run specific benchmarks by their exact `BenchmarkCase.name` values:

```powershell
.\gradlew.bat :integration-test:lincheck:loopEval --args="ADAPTIVE 1" `
  -Plincheck.loopEval.suite=all `
  -Plincheck.loopEval.benchmarks=ConcurrentHashMap,FAAQueue
```

Otherwise, the benchmarks can be run directly in `AllBenchmarksRunner.kt`.

## Results

Results are written as CSV files under:

```text
build/loop-eval/raw/
```

The filename format is:

```text
results_<mode>_<suite>_<date>.csv
```

For example:

```text
build/loop-eval/raw/results_adaptive_all_2026-06-15T11-03-03.883356900.csv
```

Running the same mode and suite again overwrites the existing CSV file.
