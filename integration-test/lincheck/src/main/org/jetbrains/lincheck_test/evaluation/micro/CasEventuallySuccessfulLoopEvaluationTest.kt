package org.jetbrains.lincheck_test.evaluation.micro

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions
import java.util.concurrent.atomic.AtomicInteger

object CasEventuallySuccessfulLoopEvaluationTest : BenchmarkCase {
    override val name: String = "CasEventuallySuccessfulLoop"

    class Scenario {
        private val value = AtomicInteger(0)

        @Operation
        fun boundedCasThenSuccess(): Int {
            var attempts = 0
            while (attempts < LOOP_ITERATIONS) {
                attempts++
                if (value.compareAndSet(1, 2)) return attempts
            }
            value.set(1)
            while (true) {
                attempts++
                if (value.compareAndSet(1, 2)) return attempts
            }
        }
    }

    override fun runModelChecking() {
        singleActorOptions(EvalOptions.modelChecking(), Scenario::boundedCasThenSuccess).check(Scenario::class)
    }
}
