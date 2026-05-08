/*
 * Lincheck
 *
 * Copyright (C) 2019 - 2026 JetBrains s.r.o.
 *
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jctools.queues.atomic.MpscLinkedAtomicQueue
import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object MpscLinkedAtomicQueueEvaluationTest : BenchmarkCase {
    override val name: String = "MpscLinkedAtomicQueue"

    /**
     * MPSC queue: multiple producers, single consumer.
     * Consumer operations are marked as non-parallel to respect the contract.
     */
    @Param(name = "value", gen = IntGen::class, conf = "1:3")
    class Scenario {
        private val q = MpscLinkedAtomicQueue<Int>()

        @Operation
        fun offer(@Param(name = "value") value: Int): Boolean = q.offer(value)

        @Operation(nonParallelGroup = "consumer")
        fun poll(): Int? = q.poll()

        @Operation(nonParallelGroup = "consumer")
        fun peek(): Int? = q.peek()
    }

    private fun modelCheckingOptions() =
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking())
            .threads(3)


    override fun runModelChecking() {
        modelCheckingOptions().check(Scenario::class)
    }
}
