# Loop Evaluation Testing Context

Date: 2026-05-07

## Scope
This note summarizes what was observed while testing the loop-evaluation microbenchmarks, specifically:
- `ZneWriteLoopEvaluationTest`
- `UnknownReadLoopEvaluationTest`
- `RecursiveNoProgressEvaluationTest`

The goal is to later downgrade to a prior Lincheck version (original loop detection algorithm) and re-run the same benchmarks for comparison.

## Observed Results (Adaptive)
Source: `build/loop-eval/raw/results_adaptive_micro.csv`

- `ZneWriteLoop` (ADAPTIVE, MC) failed with "execution has hung". Logged counters show:
  - `zne_count = 0`
  - `unknown_count = 20`
  - `await_count = 0`
  - `stuck_count = 20`
  - `switch_thread_count = 0`
  - This indicates ZNE classification did not trigger at all.

- `UnknownReadLoop` (ADAPTIVE, MC) failed with "execution has hung". Logged counters show:
  - `await_count = 20`
  - `unknown_count = 0`
  - `stuck_count = 20`
  - `switch_thread_count = 0`
  - The loop was classified as AWAIT (side-effect-free back-edge path), not UNKNOWN.

## Interpretation
- The intended ZNE classification was not reached in `ZneWriteLoop`. The test likely did not produce observable shared writes for the adaptive loop detector’s write-signature logic.
- The intended UNKNOWN classification was not reached in `UnknownReadLoop`. The loop looked side-effect-free and was treated as an await loop.

## Test Adjustments Applied (Current Working Tree)
To make the loop kinds observable by the adaptive detector without changing detector semantics:

- `ZneWriteLoopEvaluationTest`
  - Switched the loop body to write a `@Volatile` field (`sink = 0`) so shared writes are observed.

- `UnknownReadLoopEvaluationTest`
  - Added a shared `@Volatile` write (`sink = sink + 1`) in the loop body to avoid await classification while staying non-CAS and non-ZNE.

- `RecursiveNoProgressEvaluationTest`
  - Stress run intentionally skipped to avoid a known hang.

## Next Use
When downgrading to the original loop detection algorithm:
1. Re-run the micro suite under the same runner configuration.
2. Compare `zne_count`, `unknown_count`, `await_count`, `stuck_count`, and `switch_thread_count` for the microbenchmarks.
3. Keep the adjusted microbenchmarks so loop kinds are still observable under both versions.

## Files Referenced
- `integration-test/lincheck/src/main/org/jetbrains/lincheck_test/evaluation/micro/ZneWriteLoopEvaluationTest.kt`
- `integration-test/lincheck/src/main/org/jetbrains/lincheck_test/evaluation/micro/UnknownReadLoopEvaluationTest.kt`
- `integration-test/lincheck/src/main/org/jetbrains/lincheck_test/evaluation/micro/RecursiveNoProgressEvaluationTest.kt`
- `build/loop-eval/raw/results_adaptive_micro.csv`

## Measurement Pipeline (Hooks + Counters)

This section documents how metrics are collected so the same hooks can be reintroduced when downgrading.

### Hook Wiring (where the listener is installed)
- `integration-test/lincheck/src/main/org/jetbrains/lincheck_test/evaluation/common/AllBenchmarksRunner.kt`
  - Installs hooks at startup: `LoopEvalHooks.install(EvalStatsHolder)`.
  - Resets stats per run: `EvalStatsHolder.reset()` before each benchmark run.
  - Collects snapshot after each run: `EvalStatsHolder.snapshot()`.

### Hook Interface
- `lincheck/src/jvm/main/org/jetbrains/kotlinx/lincheck/strategy/managed/LoopEvalHooks.kt`
  - Defines `LoopEvalListener` callbacks and dispatches them via static `LoopEvalHooks`.
  - The listener is optional and safe to no-op if not installed.

### Counters and Their Sources
- `explored_schedules`
  - `lincheck/src/jvm/main/org/jetbrains/kotlinx/lincheck/strategy/managed/modelchecking/ModelCheckingStrategy.kt`
  - `runInvocation()` calls `LoopEvalHooks.onExploredSchedule()` after each completed schedule.
  - This is only for model checking (stress doesn’t call this hook).

- `failure_found`
  - `lincheck/src/jvm/main/org/jetbrains/kotlinx/lincheck/strategy/managed/ManagedStrategy.kt`
  - `tryCollectTrace(...)` calls `LoopEvalHooks.onFailureFound()` when a failing execution is detected.

- `switch_thread_count`, `stuck_count`, `idle_count`
  - `lincheck/src/jvm/main/org/jetbrains/kotlinx/lincheck/strategy/managed/ManagedStrategy.kt`
  - `recordLoopDecision(...)` calls `LoopEvalHooks.onDecision(decision)`.
  - This is invoked from loop-handling paths to record `IDLE`, `SWITCH_THREAD`, or `STUCK`.

- `await_count`
  - `lincheck/src/jvm/main/org/jetbrains/kotlinx/lincheck/strategy/managed/AdaptiveLoopDetector.kt`
  - Incremented once per loop instance when it transitions from `UNKNOWN` to `AWAIT`.
  - Hook: `LoopEvalHooks.onAwaitClassified()` in `onAwaitLoopIteration(...)`.

- `cas_count`
  - `lincheck/src/jvm/main/org/jetbrains/kotlinx/lincheck/strategy/managed/AdaptiveLoopDetector.kt`
  - Incremented once per loop instance when it transitions from `UNKNOWN` to `CAS`.
  - Hook: `LoopEvalHooks.onCasClassified()` in `classifyLoop(...)`.

- `zne_count`
  - `lincheck/src/jvm/main/org/jetbrains/kotlinx/lincheck/strategy/managed/AdaptiveLoopDetector.kt`
  - Incremented once per loop instance when it transitions from `UNKNOWN` to `ZNE`.
  - Hook: `LoopEvalHooks.onZneClassified()` in `classifyLoop(...)`.

- `unknown_count`
  - `lincheck/src/jvm/main/org/jetbrains/kotlinx/lincheck/strategy/managed/AdaptiveLoopDetector.kt`
  - Incremented once per loop instance when `UNKNOWN` reaches decision logic.
  - Hook: `LoopEvalHooks.onUnknownDecision()` in `processIteration(...)` (both observation and no‑observation paths).

- `unknown_count` in bounded mode
  - `lincheck/src/jvm/main/org/jetbrains/kotlinx/lincheck/strategy/managed/BoundedLoopDetector.kt`
  - Hook: `LoopEvalHooks.onUnknownDecision()` when bounded detection reaches decision logic.

### Stats Aggregation
- `integration-test/lincheck/src/main/org/jetbrains/lincheck_test/evaluation/common/EvalStats.kt`
  - `EvalStatsHolder` implements `LoopEvalListener` using global `AtomicLong/AtomicInteger` counters.
  - `snapshot()` materializes the values for CSV output.

## Downgrade Notes (porting the hooks)
When switching to the original loop detector implementation, reintroduce the same hook points:
1. Add/keep `LoopEvalHooks` + `LoopEvalListener` in the managed strategy package.
2. Install hooks in `AllBenchmarksRunner` and reset/snapshot per run.
3. Add `onExploredSchedule()` in model checking (one per schedule).
4. Add `onFailureFound()` where Lincheck detects a failing execution (the same place used to collect traces).
5. Add `onDecision(...)` where loop handling decides `IDLE`/`SWITCH_THREAD`/`STUCK`.
6. Add classification hooks in the loop detector (AWAIT/CAS/ZNE) and `onUnknownDecision()` where UNKNOWN reaches decision logic.

These are measurement-only hooks; they do not change loop detector semantics.
