/*
 * Lincheck
 *
 * Copyright (C) 2019 - 2025 JetBrains s.r.o.
 *
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.jetbrains.kotlinx.lincheck.strategy.managed

import org.jetbrains.lincheck.datastructures.ManagedCTestConfiguration

/* TODO: Notes from 25th November meeting
  - Heuristic with too many iteration as fallback
  - For certain loops have a smarter way to delect when we are getting stuck - spinloops
  - Loop detector should not depend on Managed Strategy - only some calls need to be kept, for example beforeAtomicMethodCall and afterAtomicMethodCall
  - TODO: important- each method call is added in ManagedStrategy.kt in callStackTrace map; in each stack element have stack of loops in current execution frame. In loop detector make use of it to detect loops

*/

//TODO tests from integration SHOULD not fail





/**
 * LoopDetector2 is a wrapper over the existing [LoopDetector] that:
 *
 *  - Can delegate the old behaviour to the original LoopDetector. (I do this only for compatibility now.)
 *  - Adds per-thread and per-loop iteration tracking based on CFG loop IDs.
 *  - Provides api for the instrumented code: [onLoopIteration] and [afterLoopExit].
 * TODO: fully integrate with ManagedStrategy, such that the original LoopDetector is not needed anymore.
 */
internal class LoopDetector2 (private val hangingDetectionThreshold2: Int) : LoopDetector(hangingDetectionThreshold2) {

//    private var currentThreadId: Int = -1
//    private var totalExecutionsCount: Int = 0
//    private val globalEventsThreshold: Int = ManagedCTestConfiguration.DEFAULT_LIVELOCK_EVENTS_THRESHOLD

    // ==== New loop state support ====
    private data class LoopState(
        var iterations: Int = 0,
        var isInsideLoop: Boolean = false,
        var suspect: Boolean = false,
        var currentSignature: Int = 1,
        val signatures: MutableList<Int> = mutableListOf()

    )

    // needed for model checking strategy
    data class LoopSummary(
        val threadId : Int,
        val loopId: Int,
        val iterations: Int,
        val isInsideLoop: Boolean,
        val isSuspect: Boolean
    )

    // threadId -> (loopId -> LoopState)
    private val threadLoopStates = mutableMapOf<Int, MutableMap<Int, LoopState>>()

    private fun loopStatesForThread(threadId: Int): MutableMap<Int, LoopState> =
        threadLoopStates.getOrPut(threadId) { mutableMapOf() }

    fun currentLoopStatesSummary(): List<LoopSummary> =
        threadLoopStates.flatMap { (threadId, loops) ->
            loops.map { (loopId, state) ->
                LoopSummary(
                    threadId = threadId,
                    loopId = loopId,
                    iterations = state.iterations,
                    isInsideLoop = state.isInsideLoop,
                    isSuspect = state.suspect
                )
            }
        }

    override fun reset() {
        threadLoopStates.clear()
//        currentThreadId = -1
//        totalExecutionsCount = 0
        super.reset()
    }

    // TODO Temporary, to keep the old behaviour. In future I want to not make use of original LoopDetector at all. (changes needed in ManagedStrategy).
    //  Probably needs modifications for the new apis too.
    // I keep it for compatibility now.
    override fun visitCodeLocation(iThread: Int, codeLocation: Int): Decision {
        val loops = loopStatesForThread(iThread)
        loops.values.forEach { state ->
            if (state.isInsideLoop) {
                // javc string hashcode function style
                state.currentSignature = state.currentSignature * 31 + codeLocation
            }
        }
//        or we can call super.visitCodeLocation(iThread, codeLocation)
        return Decision.Idle
    }


    // ==== API called by ManagedStrategy ====

    /**
     * Called during each loop iteration for a given thread.
     *
     *  - Maintain iteration counter per (thread, loopId).
     *  - Contains a cycle detector
     *  - Based on the iteration count and cycle detection, decide whether to:
     *    - Idle
     *    - Mark the loop as suspect and request a thread switch.
     *
     */
    fun onLoopIteration(
        iThread: Int,
        codeLocation: Int,
        loopId: Int
    ): Decision {

        //  New loop heuristic.
        val loops = loopStatesForThread(iThread)
        val state = loops.getOrPut(loopId) { LoopState() }

        var cycleDetected = false
        var cyclePeriod = 0
        if (state.isInsideLoop) {
            val sig = state.currentSignature
            state.signatures.add(sig)

            // check for cycles by looking for previous occurences of the current signature
            val lastIdx = state.signatures.lastIndex
            val firstOccurenceIdx = state.signatures.indexOf(sig)
            if (firstOccurenceIdx >= 0 && lastIdx - firstOccurenceIdx >= 1) {
                // cycle detected
                cycleDetected = true
                cyclePeriod = lastIdx - firstOccurenceIdx
            }
        }

//        if (!state.isInsideLoop) {
//            state.isInsideLoop = true
//            state.iterations = 0
//            state.suspect = false
//        }


        state.isInsideLoop = true
        state.iterations++
        state.currentSignature = 1


        // Some simple rules:
        // - When iterations <= threshold: idle.
        // - When cycle detected and not yet suspected: make suspect and ask for a switch with the cycle period.
        // - When iterations > threshold and not yet suspected: make suspect and ask for a switch with cycle 1.
        // - When already suspected: idle.

        val tooManyOperations = state.iterations > hangingDetectionThreshold2
        return when  {
            cycleDetected && !state.suspect -> {
                state.suspect = true
                Decision.LivelockThreadSwitch(
                    cyclePeriod = cyclePeriod
                )
            }
            tooManyOperations && !cycleDetected && !state.suspect -> {
                state.suspect = true
                Decision.LivelockThreadSwitch(
                    cyclePeriod = 1
                )
            }
            // 2 options
            // Suspect, dont notify the model checker again, it is checking for it already
            // Or iteration <= threshold
            else -> Decision.Idle

//            TODO: might need something similar to original Loop detector of the case
            // live-lock detected - fail
//            totalExecutionsCount > ManagedCTestConfiguration.DEFAULT_LIVELOCK_EVENTS_THRESHOLD -> Decision.EventsThresholdReached

        }
    }

    /**
     * Api for leaving a loop.
     * Drop the loop state for (thread, loopId) and reset values.
     */
    fun afterLoopExit(
        iThread: Int,
        codeLocation: Int,
        loopId: Int,
        exception: Throwable?,
        canEnterFromOutsideLoop: Boolean
    ) {
        val loops = loopStatesForThread(iThread)
        val state = loops[loopId] ?: return

        state.isInsideLoop = false
        state.iterations = 0
        state.suspect = false
        state.signatures.clear()
        state.currentSignature = 1

        // drop state if loop cannot be reentered
        if (!canEnterFromOutsideLoop) {
            loops.remove(loopId)
        }
    }
}