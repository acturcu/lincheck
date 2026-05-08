package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck_test.datastructures.FAAQueue
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object FAAQueueEvaluationTest : BenchmarkCase {
    override val name: String = "FAAQueue"

    @Param(name = "value", gen = IntGen::class, conf = "1:3")
    class Scenario {
        private val q = FAAQueue<Int>()

        @Operation
        fun enqueue(@Param(name = "value") x: Int) {
            q.enqueue(x)
        }

        @Operation
        fun dequeue(): Int? {
            return q.dequeue()
        }

        @Operation
        fun isEmpty(): Boolean {
            return q.isEmpty
        }
    }

    override fun runModelChecking() {
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking()).check(Scenario::class)
    }
}
