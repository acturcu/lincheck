package org.jetbrains.lincheck_test.evaluation.micro

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

object AwaitLoopMixedPathsEvaluationTest : BenchmarkCase {
    override val name: String = "AwaitLoopMixedPaths"

    class Scenario {
        private val flag = AtomicBoolean(false)
        private val metric = AtomicInteger(0)

        @Operation
        fun waitLoop(): Int {
            while (true) {
                val v = flag.get()
                if (!v) continue
                metric.set(1)
                return 1
            }
        }

        @Operation
        fun release() {
            flag.set(true)
        }
    }

    override fun runModelChecking() {
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking()).check(Scenario::class)
    }
}
