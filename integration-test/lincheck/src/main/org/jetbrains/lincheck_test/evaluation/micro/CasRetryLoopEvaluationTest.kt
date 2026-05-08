package org.jetbrains.lincheck_test.evaluation.micro

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions
import java.util.concurrent.atomic.AtomicInteger

object CasRetryLoopEvaluationTest : BenchmarkCase {
    override val name: String = "CasRetryLoop"

    class Scenario {
        private val x = AtomicInteger(0)

        @Operation
        fun increment() {
            while (true) {
                val old = x.get()
                if (x.compareAndSet(old, old + 1)) return
            }
        }

        @Operation
        fun get(): Int = x.get()
    }

    override fun runModelChecking() {
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking()).check(Scenario::class)
    }
}
