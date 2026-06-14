package org.jetbrains.lincheck_test.evaluation.micro

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions
import java.util.concurrent.atomic.AtomicInteger

object CasAlwaysFailLoopEvaluationTest : BenchmarkCase {
    override val name: String = "CasAlwaysFailLoop"

    class Scenario {
        private val value = AtomicInteger(0)

        @Operation
        fun boundedFailedCas(): Int {
            var failures = 0
            while (failures < LOOP_ITERATIONS) {
                if (!value.compareAndSet(1, 2)) failures++
            }
            return failures
        }
    }

    override fun runModelChecking() {
        singleActorOptions(EvalOptions.modelChecking(), Scenario::boundedFailedCas).check(Scenario::class)
    }
}
