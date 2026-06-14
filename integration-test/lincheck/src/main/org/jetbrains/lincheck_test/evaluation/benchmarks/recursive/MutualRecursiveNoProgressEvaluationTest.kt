package org.jetbrains.lincheck_test.evaluation.benchmarks.recursive

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object MutualRecursiveNoProgressEvaluationTest : BenchmarkCase {
    override val name: String = "MutualRecursiveNoProgress"

    class Scenario {
        @Operation
        fun recurse(): Int = recurseA(0)

        private fun recurseA(depth: Int): Int = recurseB(depth + 1)

        private fun recurseB(depth: Int): Int = recurseA(depth + 1)
    }

    override fun runModelChecking() {
        singleActorOptions(EvalOptions.modelChecking(), Scenario::recurse).check(Scenario::class)
    }
}
