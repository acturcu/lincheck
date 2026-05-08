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

data class EvalResult(
    val benchmark: String,
    val modeLabel: String,
    val runKind: String, // Always "MC"
    val repetition: Int,
    val status: String,  // "OK" or "FAIL"
    val runtimeMs: Long,
    val errorType: String = "",
    val errorMessage: String = "",

    val exploredSchedules: Long = 0,
    val failureFound: Int = 0,

    val switchThreadCount: Long = 0,
    val stuckCount: Long = 0,
    val idleCount: Long = 0,

    val awaitCount: Long = 0,
    val casCount: Long = 0,
    val zneCount: Long = 0,
    val unknownCount: Long = 0,

    val switchByBoundedPeriod: Long = 0,
    val switchByAwait: Long = 0,
    val switchByCas: Long = 0,
    val switchByZne: Long = 0,
    val switchByUnknown: Long = 0,
    val switchByNoObservations: Long = 0,

    val stuckByLoopBound: Long = 0,
    val stuckByRecursionBound: Long = 0,
    val stuckByAdaptiveIterationBound: Long = 0,
    val stuckByAbstractState: Long = 0,
    val stuckByNoSwitchableThread: Long = 0,

    val loopEnterCount: Long = 0,
    val loopExitCount: Long = 0,
    val loopIterationCount: Long = 0,
    val irreducibleLoopIterationCount: Long = 0,
    val awaitBackEdgeHitCount: Long = 0,
    val maxLoopIterations: Long = 0,

    val methodEnterCount: Long = 0,
    val methodExitCount: Long = 0,
    val maxMethodDepth: Long = 0,
    val recursionBoundHitCount: Long = 0,

    val sharedReadCount: Long = 0,
    val sharedWriteCount: Long = 0,
    val casSuccessCount: Long = 0,
    val casFailureCount: Long = 0,
    val relevantExternalWriteCount: Long = 0,
    val waitSetSizeSum: Long = 0,
    val maxWaitSetSize: Long = 0,
    val abstractStateVisitCount: Long = 0,
    val maxAbstractStateVisits: Long = 0,
    val signatureCycleCount: Long = 0,
    val repeatSignatureCount: Long = 0,

    val switchRequestCount: Long = 0,
    val successfulSwitchCount: Long = 0,
    val failedSwitchCount: Long = 0,
    val enabledThreadsSum: Long = 0,
    val maxEnabledThreads: Long = 0,

    val incorrectResultsFailureCount: Long = 0,
    val livelockFailureCount: Long = 0,
    val deadlockFailureCount: Long = 0,
    val obstructionFreedomFailureCount: Long = 0,
    val runnerTimeoutFailureCount: Long = 0,
    val internalFailureCount: Long = 0,
    val exceptionFailureCount: Long = 0,
    val traceCollectionCount: Long = 0
)
