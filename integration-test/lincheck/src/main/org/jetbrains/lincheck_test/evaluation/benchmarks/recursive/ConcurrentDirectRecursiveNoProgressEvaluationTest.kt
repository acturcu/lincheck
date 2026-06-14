package org.jetbrains.lincheck_test.evaluation.benchmarks.recursive

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object ConcurrentDirectRecursiveNoProgressEvaluationTest : BenchmarkCase {
    override val name: String = "ConcurrentDirectRecursiveNoProgress"

    class Scenario {
        @Operation
        fun recurseLeft(): Int = recurseLeft0(0)

        @Operation
        fun recurseRight(): Int = recurseRight0(0)

        private fun recurseLeft0(depth: Int): Int = recurseLeft0(depth + 1)

        private fun recurseRight0(depth: Int): Int = recurseRight0(depth + 1)
    }

    override fun runModelChecking() {
        actorOptions(
            EvalOptions.modelChecking(),
            Scenario::recurseLeft,
            Scenario::recurseRight
        ).check(Scenario::class)
    }
}
