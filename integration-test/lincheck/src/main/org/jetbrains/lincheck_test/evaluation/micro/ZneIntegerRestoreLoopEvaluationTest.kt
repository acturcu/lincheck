package org.jetbrains.lincheck_test.evaluation.micro

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object ZneIntegerRestoreLoopEvaluationTest : BenchmarkCase {
    override val name: String = "ZneIntegerRestoreLoop"

    class Scenario {
        private var value = 0

        @Operation
        fun boundedRestore(): Int {
            var iterations = 0
            while (iterations < LOOP_ITERATIONS) {
                value = 1
                value = 0
                iterations++
            }
            return value
        }
    }

    override fun runModelChecking() {
        singleActorOptions(EvalOptions.modelChecking(), Scenario::boundedRestore).check(Scenario::class)
    }
}
