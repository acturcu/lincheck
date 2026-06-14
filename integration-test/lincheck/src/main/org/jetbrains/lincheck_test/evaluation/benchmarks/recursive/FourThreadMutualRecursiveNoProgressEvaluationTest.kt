package org.jetbrains.lincheck_test.evaluation.benchmarks.recursive

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions
import java.util.concurrent.atomic.AtomicInteger

object FourThreadMutualRecursiveNoProgressEvaluationTest : BenchmarkCase {
    override val name: String = "FourThreadMutualRecursiveNoProgress"

    class Scenario {
        private val state = AtomicInteger(0)

        @Operation
        fun startA(): Int = recurseA(0)

        @Operation
        fun startB(): Int = recurseB(0)

        @Operation
        fun startC(): Int = recurseC(0)

        @Operation
        fun update(): Int = state.incrementAndGet()

        private fun recurseA(depth: Int): Int {
            state.get()
            return recurseB(depth + 1)
        }

        private fun recurseB(depth: Int): Int {
            state.get()
            return recurseC(depth + 1)
        }

        private fun recurseC(depth: Int): Int {
            state.get()
            return recurseA(depth + 1)
        }
    }

    override fun runModelChecking() {
        actorOptions(
            EvalOptions.modelChecking(),
            Scenario::startA,
            Scenario::startB,
            Scenario::startC,
            Scenario::update
        ).check(Scenario::class)
    }
}
