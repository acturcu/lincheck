/*
 * Lincheck
 *
 * Copyright (C) 2019 - 2026 JetBrains s.r.o.
 *
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.jetbrains.kotlinx.lincheck.strategy.managed

/**
 * Hook for loop-evaluation instrumentation.
 */
interface LoopEvalListener {
    fun onExploredSchedule()
    fun onFailureFound()
    fun onFailure(kind: LoopEvalFailureKind)
    fun onDecision(decision: LoopDetector.Decision)
    fun onDecisionReason(reason: LoopEvalDecisionReason)
    fun onTraceCollectionStarted()
    fun onLoopEntered()
    fun onLoopExited()
    fun onLoopIteration(iteration: Int)
    fun onIrreducibleLoopIteration()
    fun onAwaitBackEdgeHit()
    fun onMethodEntered(depth: Int)
    fun onMethodExited()
    fun onRecursionBoundHit()
    fun onSharedReadInLoop()
    fun onSharedWriteInLoop()
    fun onCasResultInLoop(success: Boolean)
    fun onRelevantExternalWrite()
    fun onWaitSetSize(size: Int)
    fun onAbstractStateVisit(visits: Int)
    fun onSignatureCycle()
    fun onRepeatSignature()
    fun onSwitchAttempt(enabledThreads: Int, switched: Boolean)
    fun onAwaitClassified()
    fun onCasClassified()
    fun onZneClassified()
    fun onUnknownDecision()
}

enum class LoopEvalDecisionReason {
    BOUNDED_PERIODIC_SWITCH,
    BOUNDED_LOOP_BOUND,
    RECURSION_BOUND,
    ADAPTIVE_ITERATION_BOUND,
    ADAPTIVE_ABSTRACT_STATE,
    ADAPTIVE_AWAIT,
    ADAPTIVE_CAS,
    ADAPTIVE_ZNE,
    ADAPTIVE_UNKNOWN,
    ADAPTIVE_NO_OBSERVATIONS,
    NO_SWITCHABLE_THREAD,
}

enum class LoopEvalFailureKind {
    INCORRECT_RESULTS,
    LIVELOCK,
    DEADLOCK,
    OBSTRUCTION_FREEDOM,
    RUNNER_TIMEOUT,
    INTERNAL_ERROR,
    EXCEPTION,
}

object LoopEvalHooks {
    @Volatile
    private var listener: LoopEvalListener? = null

    fun install(listener: LoopEvalListener?) {
        this.listener = listener
    }

    fun onExploredSchedule() {
        listener?.onExploredSchedule()
    }

    fun onFailureFound() {
        listener?.onFailureFound()
    }

    fun onFailure(kind: LoopEvalFailureKind) {
        listener?.onFailure(kind)
    }

    fun onDecision(decision: LoopDetector.Decision) {
        listener?.onDecision(decision)
    }

    fun onDecisionReason(reason: LoopEvalDecisionReason) {
        listener?.onDecisionReason(reason)
    }

    fun onTraceCollectionStarted() {
        listener?.onTraceCollectionStarted()
    }

    fun onLoopEntered() {
        listener?.onLoopEntered()
    }

    fun onLoopExited() {
        listener?.onLoopExited()
    }

    fun onLoopIteration(iteration: Int) {
        listener?.onLoopIteration(iteration)
    }

    fun onIrreducibleLoopIteration() {
        listener?.onIrreducibleLoopIteration()
    }

    fun onAwaitBackEdgeHit() {
        listener?.onAwaitBackEdgeHit()
    }

    fun onMethodEntered(depth: Int) {
        listener?.onMethodEntered(depth)
    }

    fun onMethodExited() {
        listener?.onMethodExited()
    }

    fun onRecursionBoundHit() {
        listener?.onRecursionBoundHit()
    }

    fun onSharedReadInLoop() {
        listener?.onSharedReadInLoop()
    }

    fun onSharedWriteInLoop() {
        listener?.onSharedWriteInLoop()
    }

    fun onCasResultInLoop(success: Boolean) {
        listener?.onCasResultInLoop(success)
    }

    fun onRelevantExternalWrite() {
        listener?.onRelevantExternalWrite()
    }

    fun onWaitSetSize(size: Int) {
        listener?.onWaitSetSize(size)
    }

    fun onAbstractStateVisit(visits: Int) {
        listener?.onAbstractStateVisit(visits)
    }

    fun onSignatureCycle() {
        listener?.onSignatureCycle()
    }

    fun onRepeatSignature() {
        listener?.onRepeatSignature()
    }

    fun onSwitchAttempt(enabledThreads: Int, switched: Boolean) {
        listener?.onSwitchAttempt(enabledThreads, switched)
    }

    fun onAwaitClassified() {
        listener?.onAwaitClassified()
    }

    fun onCasClassified() {
        listener?.onCasClassified()
    }

    fun onZneClassified() {
        listener?.onZneClassified()
    }

    fun onUnknownDecision() {
        listener?.onUnknownDecision()
    }
}
