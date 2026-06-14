package org.jetbrains.lincheck_test.evaluation.benchmarks.recursive

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions
import java.util.concurrent.atomic.AtomicInteger

object BranchingRecursiveNoProgressEvaluationTest : BenchmarkCase {
    override val name: String = "BranchingRecursiveNoProgress"

    class Scenario {
        private val selector = AtomicInteger(0)

        @Operation
        fun recurseBranching(): Int = root(0)

        @Operation
        fun flipSelector(): Int = selector.incrementAndGet()

        private fun root(depth: Int): Int {
            return if ((selector.get() and 1) == 0) {
                left(depth + 1)
            } else {
                right(depth + 1)
            }
        }

        private fun left(depth: Int): Int = root(depth + 1)

        private fun right(depth: Int): Int = root(depth + 1)
    }

    override fun runModelChecking() {
        actorOptions(
            EvalOptions.modelChecking(),
            Scenario::recurseBranching,
            Scenario::flipSelector
        ).check(Scenario::class)
    }
}
