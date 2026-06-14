package org.jetbrains.lincheck_test.evaluation.benchmarks.recursive

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions
import java.util.concurrent.atomic.AtomicInteger

object ThreeThreadRecursiveNoProgressEvaluationTest : BenchmarkCase {
    override val name: String = "ThreeThreadRecursiveNoProgress"

    class Scenario {
        private val counter = AtomicInteger(0)

        @Operation
        fun recurseA(): Int = recurseA0(0)

        @Operation
        fun recurseB(): Int = recurseB0(0)

        @Operation
        fun recurseC(): Int = recurseC0(0)

        private fun recurseA0(depth: Int): Int {
            counter.get()
            return recurseA0(depth + 1)
        }

        private fun recurseB0(depth: Int): Int {
            counter.get()
            return recurseB0(depth + 1)
        }

        private fun recurseC0(depth: Int): Int {
            counter.get()
            return recurseC0(depth + 1)
        }
    }

    override fun runModelChecking() {
        actorOptions(
            EvalOptions.modelChecking(),
            Scenario::recurseA,
            Scenario::recurseB,
            Scenario::recurseC
        ).check(Scenario::class)
    }
}
