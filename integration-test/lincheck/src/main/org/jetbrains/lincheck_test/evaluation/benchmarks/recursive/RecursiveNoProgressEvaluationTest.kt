package org.jetbrains.lincheck_test.evaluation.benchmarks.recursive

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object RecursiveNoProgressEvaluationTest : BenchmarkCase {
    override val name: String = "RecursiveNoProgress"

    class Scenario {
        @Operation
        fun recurse(): Int = recurse0(0)

        private fun recurse0(i: Int): Int = recurse0(i + 1)
    }

    override fun runModelChecking() {
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking()).check(Scenario::class)
    }
}
