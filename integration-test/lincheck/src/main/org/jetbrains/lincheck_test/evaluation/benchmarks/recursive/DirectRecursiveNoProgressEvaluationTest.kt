package org.jetbrains.lincheck_test.evaluation.benchmarks.recursive

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object DirectRecursiveNoProgressEvaluationTest : BenchmarkCase {
    override val name: String = "DirectRecursiveNoProgress"

    class Scenario {
        @Operation
        fun recurse(): Int = recurse0(0)

        private fun recurse0(depth: Int): Int = recurse0(depth + 1)
    }

    override fun runModelChecking() {
        singleActorOptions(EvalOptions.modelChecking(), Scenario::recurse).check(Scenario::class)
    }
}
