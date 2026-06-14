package org.jetbrains.lincheck_test.evaluation.benchmarks.recursive

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions
import java.util.concurrent.atomic.AtomicInteger

object ConcurrentRecursiveWithSharedReadEvaluationTest : BenchmarkCase {
    override val name: String = "ConcurrentRecursiveWithSharedRead"

    class Scenario {
        private val value = AtomicInteger(0)

        @Operation
        fun recurseReader(): Int = recurseReader0(0)

        @Operation
        fun update(): Int = value.incrementAndGet()

        private fun recurseReader0(depth: Int): Int {
            value.get()
            return recurseReader0(depth + 1)
        }
    }

    override fun runModelChecking() {
        actorOptions(
            EvalOptions.modelChecking(),
            Scenario::recurseReader,
            Scenario::update
        ).check(Scenario::class)
    }
}
