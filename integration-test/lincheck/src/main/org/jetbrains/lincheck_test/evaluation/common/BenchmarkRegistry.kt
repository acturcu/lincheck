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

import org.jetbrains.lincheck_test.evaluation.benchmarks.ConcurrentAutoTableEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.BranchingRecursiveNoProgressEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.ConcurrentHashMapCollisionAwaitEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.ConcurrentHashMapEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.ConcurrentLinkedQueueEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.ConcurrentSkipListMapEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.ConcurrentDirectRecursiveNoProgressEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.ConcurrentRecursiveWithSharedReadEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.CounterEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.DirectRecursiveNoProgressEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.FourThreadMutualRecursiveNoProgressEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.FAAQueueEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.LockFreeTaskQueueEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.MpmcUnboundedXaddArrayQueueAwaitEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.MpscLinkedAtomicQueueAwaitEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.MpscUnboundedXaddArrayQueueAwaitEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.MpmcArrayQueueEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.MpscArrayQueueEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.MpscLinkedAtomicQueueEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.MutualRecursiveNoProgressEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.NonBlockingHashMapEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.NonBlockingHashMapLongEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.NonBlockingSetIntEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.SingleWriterHashTableEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.SpmcArrayQueueEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.SpmcArrayQueueAwaitEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.SpscArrayQueueEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.RecursiveThroughObjectsNoProgressEvaluationTest
import org.jetbrains.lincheck_test.evaluation.benchmarks.ThreeThreadRecursiveNoProgressEvaluationTest
import org.jetbrains.lincheck_test.evaluation.micro.AwaitLoopMixedPathsEvaluationTest
import org.jetbrains.lincheck_test.evaluation.micro.AwaitLoopSimpleEvaluationTest
import org.jetbrains.lincheck_test.evaluation.micro.CasAlwaysFailLoopEvaluationTest
import org.jetbrains.lincheck_test.evaluation.micro.CasEventuallySuccessfulLoopEvaluationTest
import org.jetbrains.lincheck_test.evaluation.micro.CasRetryLoopEvaluationTest
import org.jetbrains.lincheck_test.evaluation.micro.RecursiveNoProgressEvaluationTest
import org.jetbrains.lincheck_test.evaluation.micro.UnknownReadLoopEvaluationTest
import org.jetbrains.lincheck_test.evaluation.micro.ZneBooleanToggleLoopEvaluationTest
import org.jetbrains.lincheck_test.evaluation.micro.ZneIntegerRestoreLoopEvaluationTest
import org.jetbrains.lincheck_test.evaluation.micro.ZneWriteLoopEvaluationTest
import org.jetbrains.lincheck_test.evaluation.variable.ConcurrentHashMapCollisionEvaluationTest
import org.jetbrains.lincheck_test.evaluation.variable.ConcurrentLinkedDequeChurnEvaluationTest
import org.jetbrains.lincheck_test.evaluation.variable.LinkedTransferQueueEvaluationTest
import org.jetbrains.lincheck_test.evaluation.variable.LockFreeTaskQueueMultiConsumerEvaluationTest
import org.jetbrains.lincheck_test.evaluation.variable.MpmcUnboundedXaddArrayQueueChunkEvaluationTest
import org.jetbrains.lincheck_test.evaluation.variable.MpscGrowableArrayQueueExpansionEvaluationTest

enum class BenchmarkSuite {
    CORE,
    MICRO,
    VARIABLE,
    ALL
}

object BenchmarkRegistry {
    val coreBenchmarks: List<BenchmarkCase> = listOf(
        ConcurrentHashMapCollisionAwaitEvaluationTest,
        SpmcArrayQueueAwaitEvaluationTest,
        MpmcUnboundedXaddArrayQueueAwaitEvaluationTest,
        MpscUnboundedXaddArrayQueueAwaitEvaluationTest,
        MpscLinkedAtomicQueueAwaitEvaluationTest,
        DirectRecursiveNoProgressEvaluationTest,
        MutualRecursiveNoProgressEvaluationTest,
        ConcurrentDirectRecursiveNoProgressEvaluationTest,
        ConcurrentRecursiveWithSharedReadEvaluationTest,
        ThreeThreadRecursiveNoProgressEvaluationTest,
        FourThreadMutualRecursiveNoProgressEvaluationTest,
        RecursiveThroughObjectsNoProgressEvaluationTest,
        BranchingRecursiveNoProgressEvaluationTest,
        FAAQueueEvaluationTest,
        LockFreeTaskQueueEvaluationTest,
        ConcurrentHashMapEvaluationTest,
        ConcurrentLinkedQueueEvaluationTest,
        ConcurrentSkipListMapEvaluationTest,
        MpmcArrayQueueEvaluationTest,
        MpscArrayQueueEvaluationTest,
        MpscLinkedAtomicQueueEvaluationTest,
        SpmcArrayQueueEvaluationTest,
        SpscArrayQueueEvaluationTest,
        NonBlockingHashMapEvaluationTest,
        NonBlockingHashMapLongEvaluationTest,
        NonBlockingSetIntEvaluationTest,
        SingleWriterHashTableEvaluationTest,
        ConcurrentAutoTableEvaluationTest,
        CounterEvaluationTest
    )

    val microBenchmarks: List<BenchmarkCase> = listOf(
        AwaitLoopSimpleEvaluationTest,
        AwaitLoopMixedPathsEvaluationTest,
        CasRetryLoopEvaluationTest,
        CasAlwaysFailLoopEvaluationTest,
        CasEventuallySuccessfulLoopEvaluationTest,
        ZneWriteLoopEvaluationTest,
        ZneBooleanToggleLoopEvaluationTest,
        ZneIntegerRestoreLoopEvaluationTest,
        UnknownReadLoopEvaluationTest,
        RecursiveNoProgressEvaluationTest,
    )

    val variableBenchmarks: List<BenchmarkCase> = listOf(
        ConcurrentHashMapCollisionEvaluationTest,
        ConcurrentLinkedDequeChurnEvaluationTest,
        LinkedTransferQueueEvaluationTest,
        MpscGrowableArrayQueueExpansionEvaluationTest,
        MpmcUnboundedXaddArrayQueueChunkEvaluationTest,
        LockFreeTaskQueueMultiConsumerEvaluationTest,
    )

    val benchmarks: List<BenchmarkCase> = coreBenchmarks + microBenchmarks + variableBenchmarks

    fun selectBenchmarks(suite: BenchmarkSuite): List<BenchmarkCase> = when (suite) {
        BenchmarkSuite.CORE -> coreBenchmarks
        BenchmarkSuite.MICRO -> microBenchmarks
        BenchmarkSuite.VARIABLE -> variableBenchmarks
        BenchmarkSuite.ALL -> benchmarks
    }

    fun parseSuite(label: String): BenchmarkSuite = when (label.lowercase()) {
        "core" -> BenchmarkSuite.CORE
        "micro" -> BenchmarkSuite.MICRO
        "variable" -> BenchmarkSuite.VARIABLE
        "all" -> BenchmarkSuite.ALL
        else -> error("Unknown suite: $label")
    }
}
