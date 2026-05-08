/*
 * Lincheck
 *
 * Copyright (C) 2019 - 2026 JetBrains s.r.o.
 *
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.jetbrains.lincheck_test.evaluation.common

import org.jetbrains.kotlinx.lincheck.strategy.managed.LoopEvalHooks
import org.jetbrains.kotlinx.lincheck.strategy.managed.LoopEvalFailureKind
import java.io.File
import kotlin.system.exitProcess

object AllBenchmarksRunner {
    @JvmStatic
    fun main(args: Array<String>) {
        // BASELINE, BOUNDED, ADAPTIVE
        val modeLabel = args.getOrNull(0) ?: "ADAPTIVE"
        val repetitions = args.getOrNull(1)?.toIntOrNull() ?: 1

        val suiteLabel = System.getProperty("lincheck.loopEval.suite", "all")
        val suite = BenchmarkRegistry.parseSuite(suiteLabel)
        val benchmarkFilter = System.getProperty("lincheck.loopEval.benchmarks", "")
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()

        LoopEvalHooks.install(EvalStatsHolder)

        println("Running benchmarks with config: modeLabel=$modeLabel, repetitions=$repetitions, suite=${suite.name}")
        val config = EvalConfig(
            modeLabel = modeLabel,
            repetitions = repetitions
        )

        val outputName = "results_${modeLabel.lowercase()}_${suite.name.lowercase()}_0806.csv"
        val output = File("build/loop-eval/raw/$outputName")
        output.parentFile.mkdirs()

        val logger = EvalLogger(output)
        println("Logging results to ${output.absolutePath}")
        val benchmarks = BenchmarkRegistry.selectBenchmarks(suite)
            .filter { benchmarkFilter.isEmpty() || it.name in benchmarkFilter }
        check(benchmarks.isNotEmpty()) {
            "No benchmarks selected for suite=${suite.name}, filter=$benchmarkFilter"
        }

        for (benchmark in benchmarks) {
            println(benchmark.toString())
            repeat(config.repetitions) { repetition ->
                logger.append(runOnce(benchmark, config, repetition))
                println(benchmark.toString())
            }
        }

        logger.close()
        exitProcess(0)
    }

    private fun runOnce(
        benchmark: BenchmarkCase,
        config: EvalConfig,
        repetition: Int
    ): EvalResult {
        println("Running ${benchmark.name}")
        EvalStatsHolder.reset()

        var status = "OK"
        var errorType = ""
        var errorMessage = ""

        val startTime = System.nanoTime()
        try {
            benchmark.runModelChecking()
        } catch (t: Throwable) {
            status = "FAIL"
            errorType = t::class.java.simpleName
            errorMessage = t.message ?: ""
            EvalStatsHolder.onFailure(LoopEvalFailureKind.EXCEPTION)
        }
        val runtime = (System.nanoTime() - startTime) / 1_000_000

        val stats = EvalStatsHolder.snapshot()
        println(
            "Completed: benchmark=${benchmark.name}, mode=${config.modeLabel}, runKind=MC, " +
                    "repetition=$repetition, status=$status, runtimeMs=$runtime, errorType=$errorType"
        )
        return EvalResult(
            benchmark = benchmark.name,
            modeLabel = config.modeLabel,
            runKind = "MC",
            repetition = repetition,
            status = status,
            runtimeMs = runtime,
            errorType = errorType,
            errorMessage = errorMessage,

            exploredSchedules = stats.exploredSchedules,
            failureFound = stats.failureFound,

            switchThreadCount = stats.switchThreadCount,
            stuckCount = stats.stuckCount,
            idleCount = stats.idleCount,

            awaitCount = stats.awaitCount,
            casCount = stats.casCount,
            zneCount = stats.zneCount,
            unknownCount = stats.unknownCount,

            switchByBoundedPeriod = stats.switchByBoundedPeriod,
            switchByAwait = stats.switchByAwait,
            switchByCas = stats.switchByCas,
            switchByZne = stats.switchByZne,
            switchByUnknown = stats.switchByUnknown,
            switchByNoObservations = stats.switchByNoObservations,

            stuckByLoopBound = stats.stuckByLoopBound,
            stuckByRecursionBound = stats.stuckByRecursionBound,
            stuckByAdaptiveIterationBound = stats.stuckByAdaptiveIterationBound,
            stuckByAbstractState = stats.stuckByAbstractState,
            stuckByNoSwitchableThread = stats.stuckByNoSwitchableThread,

            loopEnterCount = stats.loopEnterCount,
            loopExitCount = stats.loopExitCount,
            loopIterationCount = stats.loopIterationCount,
            irreducibleLoopIterationCount = stats.irreducibleLoopIterationCount,
            awaitBackEdgeHitCount = stats.awaitBackEdgeHitCount,
            maxLoopIterations = stats.maxLoopIterations,

            methodEnterCount = stats.methodEnterCount,
            methodExitCount = stats.methodExitCount,
            maxMethodDepth = stats.maxMethodDepth,
            recursionBoundHitCount = stats.recursionBoundHitCount,

            sharedReadCount = stats.sharedReadCount,
            sharedWriteCount = stats.sharedWriteCount,
            casSuccessCount = stats.casSuccessCount,
            casFailureCount = stats.casFailureCount,
            relevantExternalWriteCount = stats.relevantExternalWriteCount,
            waitSetSizeSum = stats.waitSetSizeSum,
            maxWaitSetSize = stats.maxWaitSetSize,
            abstractStateVisitCount = stats.abstractStateVisitCount,
            maxAbstractStateVisits = stats.maxAbstractStateVisits,
            signatureCycleCount = stats.signatureCycleCount,
            repeatSignatureCount = stats.repeatSignatureCount,

            switchRequestCount = stats.switchRequestCount,
            successfulSwitchCount = stats.successfulSwitchCount,
            failedSwitchCount = stats.failedSwitchCount,
            enabledThreadsSum = stats.enabledThreadsSum,
            maxEnabledThreads = stats.maxEnabledThreads,

            incorrectResultsFailureCount = stats.incorrectResultsFailureCount,
            livelockFailureCount = stats.livelockFailureCount,
            deadlockFailureCount = stats.deadlockFailureCount,
            obstructionFreedomFailureCount = stats.obstructionFreedomFailureCount,
            runnerTimeoutFailureCount = stats.runnerTimeoutFailureCount,
            internalFailureCount = stats.internalFailureCount,
            exceptionFailureCount = stats.exceptionFailureCount,
            traceCollectionCount = stats.traceCollectionCount
        )
    }
}
