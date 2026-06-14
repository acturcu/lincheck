package org.jetbrains.lincheck_test.evaluation.micro

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object ZneBooleanToggleLoopEvaluationTest : BenchmarkCase {
    override val name: String = "ZneBooleanToggleLoop"

    class Scenario {
        private var flag = false

        @Operation
        fun boundedToggle(): Boolean {
            var iterations = 0
            while (iterations < LOOP_ITERATIONS) {
                flag = true
                flag = false
                iterations++
            }
            return flag
        }
    }

    override fun runModelChecking() {
        singleActorOptions(EvalOptions.modelChecking(), Scenario::boundedToggle).check(Scenario::class)
    }
}
