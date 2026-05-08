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

import org.jetbrains.kotlinx.lincheck.strategy.managed.LoopDetector
import org.jetbrains.kotlinx.lincheck.strategy.managed.LoopEvalDecisionReason
import org.jetbrains.kotlinx.lincheck.strategy.managed.LoopEvalFailureKind
import org.jetbrains.kotlinx.lincheck.strategy.managed.LoopEvalListener
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

data class EvalStats(
    var exploredSchedules: Long = 0,
    var failureFound: Int = 0,

    var switchThreadCount: Long = 0,
    var stuckCount: Long = 0,
    var idleCount: Long = 0,

    var awaitCount: Long = 0,
    var casCount: Long = 0,
    var zneCount: Long = 0,
    var unknownCount: Long = 0,

    var switchByBoundedPeriod: Long = 0,
    var switchByAwait: Long = 0,
    var switchByCas: Long = 0,
    var switchByZne: Long = 0,
    var switchByUnknown: Long = 0,
    var switchByNoObservations: Long = 0,

    var stuckByLoopBound: Long = 0,
    var stuckByRecursionBound: Long = 0,
    var stuckByAdaptiveIterationBound: Long = 0,
    var stuckByAbstractState: Long = 0,
    var stuckByNoSwitchableThread: Long = 0,

    var loopEnterCount: Long = 0,
    var loopExitCount: Long = 0,
    var loopIterationCount: Long = 0,
    var irreducibleLoopIterationCount: Long = 0,
    var awaitBackEdgeHitCount: Long = 0,
    var maxLoopIterations: Long = 0,

    var methodEnterCount: Long = 0,
    var methodExitCount: Long = 0,
    var maxMethodDepth: Long = 0,
    var recursionBoundHitCount: Long = 0,

    var sharedReadCount: Long = 0,
    var sharedWriteCount: Long = 0,
    var casSuccessCount: Long = 0,
    var casFailureCount: Long = 0,
    var relevantExternalWriteCount: Long = 0,
    var waitSetSizeSum: Long = 0,
    var maxWaitSetSize: Long = 0,
    var abstractStateVisitCount: Long = 0,
    var maxAbstractStateVisits: Long = 0,
    var signatureCycleCount: Long = 0,
    var repeatSignatureCount: Long = 0,

    var switchRequestCount: Long = 0,
    var successfulSwitchCount: Long = 0,
    var failedSwitchCount: Long = 0,
    var enabledThreadsSum: Long = 0,
    var maxEnabledThreads: Long = 0,

    var incorrectResultsFailureCount: Long = 0,
    var livelockFailureCount: Long = 0,
    var deadlockFailureCount: Long = 0,
    var obstructionFreedomFailureCount: Long = 0,
    var runnerTimeoutFailureCount: Long = 0,
    var internalFailureCount: Long = 0,
    var exceptionFailureCount: Long = 0,
    var traceCollectionCount: Long = 0
)

object EvalStatsHolder : LoopEvalListener {
    private val exploredSchedules = AtomicLong(0)
    private val failureFound = AtomicInteger(0)
    private val failureKindRecorded = AtomicBoolean(false)

    private val switchThreadCount = AtomicLong(0)
    private val stuckCount = AtomicLong(0)
    private val idleCount = AtomicLong(0)

    private val awaitCount = AtomicLong(0)
    private val casCount = AtomicLong(0)
    private val zneCount = AtomicLong(0)
    private val unknownCount = AtomicLong(0)

    private val switchByBoundedPeriod = AtomicLong(0)
    private val switchByAwait = AtomicLong(0)
    private val switchByCas = AtomicLong(0)
    private val switchByZne = AtomicLong(0)
    private val switchByUnknown = AtomicLong(0)
    private val switchByNoObservations = AtomicLong(0)

    private val stuckByLoopBound = AtomicLong(0)
    private val stuckByRecursionBound = AtomicLong(0)
    private val stuckByAdaptiveIterationBound = AtomicLong(0)
    private val stuckByAbstractState = AtomicLong(0)
    private val stuckByNoSwitchableThread = AtomicLong(0)

    private val loopEnterCount = AtomicLong(0)
    private val loopExitCount = AtomicLong(0)
    private val loopIterationCount = AtomicLong(0)
    private val irreducibleLoopIterationCount = AtomicLong(0)
    private val awaitBackEdgeHitCount = AtomicLong(0)
    private val maxLoopIterations = AtomicLong(0)

    private val methodEnterCount = AtomicLong(0)
    private val methodExitCount = AtomicLong(0)
    private val maxMethodDepth = AtomicLong(0)
    private val recursionBoundHitCount = AtomicLong(0)

    private val sharedReadCount = AtomicLong(0)
    private val sharedWriteCount = AtomicLong(0)
    private val casSuccessCount = AtomicLong(0)
    private val casFailureCount = AtomicLong(0)
    private val relevantExternalWriteCount = AtomicLong(0)
    private val waitSetSizeSum = AtomicLong(0)
    private val maxWaitSetSize = AtomicLong(0)
    private val abstractStateVisitCount = AtomicLong(0)
    private val maxAbstractStateVisits = AtomicLong(0)
    private val signatureCycleCount = AtomicLong(0)
    private val repeatSignatureCount = AtomicLong(0)

    private val switchRequestCount = AtomicLong(0)
    private val successfulSwitchCount = AtomicLong(0)
    private val failedSwitchCount = AtomicLong(0)
    private val enabledThreadsSum = AtomicLong(0)
    private val maxEnabledThreads = AtomicLong(0)

    private val incorrectResultsFailureCount = AtomicLong(0)
    private val livelockFailureCount = AtomicLong(0)
    private val deadlockFailureCount = AtomicLong(0)
    private val obstructionFreedomFailureCount = AtomicLong(0)
    private val runnerTimeoutFailureCount = AtomicLong(0)
    private val internalFailureCount = AtomicLong(0)
    private val exceptionFailureCount = AtomicLong(0)
    private val traceCollectionCount = AtomicLong(0)

    fun reset() {
        exploredSchedules.set(0)
        failureFound.set(0)
        failureKindRecorded.set(false)
        switchThreadCount.set(0)
        stuckCount.set(0)
        idleCount.set(0)
        awaitCount.set(0)
        casCount.set(0)
        zneCount.set(0)
        unknownCount.set(0)
        switchByBoundedPeriod.set(0)
        switchByAwait.set(0)
        switchByCas.set(0)
        switchByZne.set(0)
        switchByUnknown.set(0)
        switchByNoObservations.set(0)
        stuckByLoopBound.set(0)
        stuckByRecursionBound.set(0)
        stuckByAdaptiveIterationBound.set(0)
        stuckByAbstractState.set(0)
        stuckByNoSwitchableThread.set(0)
        loopEnterCount.set(0)
        loopExitCount.set(0)
        loopIterationCount.set(0)
        irreducibleLoopIterationCount.set(0)
        awaitBackEdgeHitCount.set(0)
        maxLoopIterations.set(0)
        methodEnterCount.set(0)
        methodExitCount.set(0)
        maxMethodDepth.set(0)
        recursionBoundHitCount.set(0)
        sharedReadCount.set(0)
        sharedWriteCount.set(0)
        casSuccessCount.set(0)
        casFailureCount.set(0)
        relevantExternalWriteCount.set(0)
        waitSetSizeSum.set(0)
        maxWaitSetSize.set(0)
        abstractStateVisitCount.set(0)
        maxAbstractStateVisits.set(0)
        signatureCycleCount.set(0)
        repeatSignatureCount.set(0)
        switchRequestCount.set(0)
        successfulSwitchCount.set(0)
        failedSwitchCount.set(0)
        enabledThreadsSum.set(0)
        maxEnabledThreads.set(0)
        incorrectResultsFailureCount.set(0)
        livelockFailureCount.set(0)
        deadlockFailureCount.set(0)
        obstructionFreedomFailureCount.set(0)
        runnerTimeoutFailureCount.set(0)
        internalFailureCount.set(0)
        exceptionFailureCount.set(0)
        traceCollectionCount.set(0)
    }

    fun snapshot(): EvalStats = EvalStats(
        exploredSchedules = exploredSchedules.get(),
        failureFound = failureFound.get(),
        switchThreadCount = switchThreadCount.get(),
        stuckCount = stuckCount.get(),
        idleCount = idleCount.get(),
        awaitCount = awaitCount.get(),
        casCount = casCount.get(),
        zneCount = zneCount.get(),
        unknownCount = unknownCount.get(),
        switchByBoundedPeriod = switchByBoundedPeriod.get(),
        switchByAwait = switchByAwait.get(),
        switchByCas = switchByCas.get(),
        switchByZne = switchByZne.get(),
        switchByUnknown = switchByUnknown.get(),
        switchByNoObservations = switchByNoObservations.get(),
        stuckByLoopBound = stuckByLoopBound.get(),
        stuckByRecursionBound = stuckByRecursionBound.get(),
        stuckByAdaptiveIterationBound = stuckByAdaptiveIterationBound.get(),
        stuckByAbstractState = stuckByAbstractState.get(),
        stuckByNoSwitchableThread = stuckByNoSwitchableThread.get(),
        loopEnterCount = loopEnterCount.get(),
        loopExitCount = loopExitCount.get(),
        loopIterationCount = loopIterationCount.get(),
        irreducibleLoopIterationCount = irreducibleLoopIterationCount.get(),
        awaitBackEdgeHitCount = awaitBackEdgeHitCount.get(),
        maxLoopIterations = maxLoopIterations.get(),
        methodEnterCount = methodEnterCount.get(),
        methodExitCount = methodExitCount.get(),
        maxMethodDepth = maxMethodDepth.get(),
        recursionBoundHitCount = recursionBoundHitCount.get(),
        sharedReadCount = sharedReadCount.get(),
        sharedWriteCount = sharedWriteCount.get(),
        casSuccessCount = casSuccessCount.get(),
        casFailureCount = casFailureCount.get(),
        relevantExternalWriteCount = relevantExternalWriteCount.get(),
        waitSetSizeSum = waitSetSizeSum.get(),
        maxWaitSetSize = maxWaitSetSize.get(),
        abstractStateVisitCount = abstractStateVisitCount.get(),
        maxAbstractStateVisits = maxAbstractStateVisits.get(),
        signatureCycleCount = signatureCycleCount.get(),
        repeatSignatureCount = repeatSignatureCount.get(),
        switchRequestCount = switchRequestCount.get(),
        successfulSwitchCount = successfulSwitchCount.get(),
        failedSwitchCount = failedSwitchCount.get(),
        enabledThreadsSum = enabledThreadsSum.get(),
        maxEnabledThreads = maxEnabledThreads.get(),
        incorrectResultsFailureCount = incorrectResultsFailureCount.get(),
        livelockFailureCount = livelockFailureCount.get(),
        deadlockFailureCount = deadlockFailureCount.get(),
        obstructionFreedomFailureCount = obstructionFreedomFailureCount.get(),
        runnerTimeoutFailureCount = runnerTimeoutFailureCount.get(),
        internalFailureCount = internalFailureCount.get(),
        exceptionFailureCount = exceptionFailureCount.get(),
        traceCollectionCount = traceCollectionCount.get()
    )

    override fun onExploredSchedule() {
        exploredSchedules.incrementAndGet()
    }

    override fun onFailureFound() {
        failureFound.set(1)
    }

    override fun onFailure(kind: LoopEvalFailureKind) {
        failureFound.set(1)
        if (!failureKindRecorded.compareAndSet(false, true)) return
        when (kind) {
            LoopEvalFailureKind.INCORRECT_RESULTS -> incorrectResultsFailureCount.incrementAndGet()
            LoopEvalFailureKind.LIVELOCK -> livelockFailureCount.incrementAndGet()
            LoopEvalFailureKind.DEADLOCK -> deadlockFailureCount.incrementAndGet()
            LoopEvalFailureKind.OBSTRUCTION_FREEDOM -> obstructionFreedomFailureCount.incrementAndGet()
            LoopEvalFailureKind.RUNNER_TIMEOUT -> runnerTimeoutFailureCount.incrementAndGet()
            LoopEvalFailureKind.INTERNAL_ERROR -> internalFailureCount.incrementAndGet()
            LoopEvalFailureKind.EXCEPTION -> exceptionFailureCount.incrementAndGet()
        }
    }

    override fun onDecision(decision: LoopDetector.Decision) {
        when (decision) {
            LoopDetector.Decision.IDLE -> idleCount.incrementAndGet()
            LoopDetector.Decision.SWITCH_THREAD -> switchThreadCount.incrementAndGet()
            LoopDetector.Decision.STUCK -> stuckCount.incrementAndGet()
//            LoopEvalDecision.REPLAY -> {}
        }
    }

    override fun onDecisionReason(reason: LoopEvalDecisionReason) {
        when (reason) {
            LoopEvalDecisionReason.BOUNDED_PERIODIC_SWITCH -> switchByBoundedPeriod.incrementAndGet()
            LoopEvalDecisionReason.BOUNDED_LOOP_BOUND -> stuckByLoopBound.incrementAndGet()
            LoopEvalDecisionReason.RECURSION_BOUND -> stuckByRecursionBound.incrementAndGet()
            LoopEvalDecisionReason.ADAPTIVE_ITERATION_BOUND -> stuckByAdaptiveIterationBound.incrementAndGet()
            LoopEvalDecisionReason.ADAPTIVE_ABSTRACT_STATE -> stuckByAbstractState.incrementAndGet()
            LoopEvalDecisionReason.ADAPTIVE_AWAIT -> switchByAwait.incrementAndGet()
            LoopEvalDecisionReason.ADAPTIVE_CAS -> switchByCas.incrementAndGet()
            LoopEvalDecisionReason.ADAPTIVE_ZNE -> switchByZne.incrementAndGet()
            LoopEvalDecisionReason.ADAPTIVE_UNKNOWN -> switchByUnknown.incrementAndGet()
            LoopEvalDecisionReason.ADAPTIVE_NO_OBSERVATIONS -> switchByNoObservations.incrementAndGet()
            LoopEvalDecisionReason.NO_SWITCHABLE_THREAD -> stuckByNoSwitchableThread.incrementAndGet()
        }
    }

    override fun onTraceCollectionStarted() {
        traceCollectionCount.incrementAndGet()
    }

    override fun onLoopEntered() {
        loopEnterCount.incrementAndGet()
    }

    override fun onLoopExited() {
        loopExitCount.incrementAndGet()
    }

    override fun onLoopIteration(iteration: Int) {
        loopIterationCount.incrementAndGet()
        updateMax(maxLoopIterations, iteration.toLong())
    }

    override fun onIrreducibleLoopIteration() {
        irreducibleLoopIterationCount.incrementAndGet()
    }

    override fun onAwaitBackEdgeHit() {
        awaitBackEdgeHitCount.incrementAndGet()
    }

    override fun onMethodEntered(depth: Int) {
        methodEnterCount.incrementAndGet()
        updateMax(maxMethodDepth, depth.toLong())
    }

    override fun onMethodExited() {
        methodExitCount.incrementAndGet()
    }

    override fun onRecursionBoundHit() {
        recursionBoundHitCount.incrementAndGet()
    }

    override fun onSharedReadInLoop() {
        sharedReadCount.incrementAndGet()
    }

    override fun onSharedWriteInLoop() {
        sharedWriteCount.incrementAndGet()
    }

    override fun onCasResultInLoop(success: Boolean) {
        if (success) casSuccessCount.incrementAndGet() else casFailureCount.incrementAndGet()
    }

    override fun onRelevantExternalWrite() {
        relevantExternalWriteCount.incrementAndGet()
    }

    override fun onWaitSetSize(size: Int) {
        waitSetSizeSum.addAndGet(size.toLong())
        updateMax(maxWaitSetSize, size.toLong())
    }

    override fun onAbstractStateVisit(visits: Int) {
        abstractStateVisitCount.incrementAndGet()
        updateMax(maxAbstractStateVisits, visits.toLong())
    }

    override fun onSignatureCycle() {
        signatureCycleCount.incrementAndGet()
    }

    override fun onRepeatSignature() {
        repeatSignatureCount.incrementAndGet()
    }

    override fun onSwitchAttempt(enabledThreads: Int, switched: Boolean) {
        switchRequestCount.incrementAndGet()
        enabledThreadsSum.addAndGet(enabledThreads.toLong())
        updateMax(maxEnabledThreads, enabledThreads.toLong())
        if (switched) successfulSwitchCount.incrementAndGet() else failedSwitchCount.incrementAndGet()
    }

    override fun onAwaitClassified() {
        awaitCount.incrementAndGet()
    }

    override fun onCasClassified() {
        casCount.incrementAndGet()
    }

    override fun onZneClassified() {
        zneCount.incrementAndGet()
    }

    override fun onUnknownDecision() {
        unknownCount.incrementAndGet()
    }

    private fun updateMax(target: AtomicLong, value: Long) {
        while (true) {
            val current = target.get()
            if (value <= current || target.compareAndSet(current, value)) return
        }
    }
}
