package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jctools.queues.SpmcArrayQueue
import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object SpmcArrayQueueEvaluationTest : BenchmarkCase {
    override val name: String = "SpmcArrayQueue"

    @Param(name = "value", gen = IntGen::class, conf = "1:3")
    class Scenario {
        private val q = SpmcArrayQueue<Int>(4)

        @Operation(nonParallelGroup = "producer")
        fun offer(@Param(name = "value") value: Int): Boolean = q.offer(value)

        @Operation
        fun poll(): Int? = q.poll()

        @Operation
        fun peek(): Int? = q.peek()
    }

    private fun modelCheckingOptions() =
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking())
            .threads(3)


    override fun runModelChecking() {
        modelCheckingOptions().check(Scenario::class)
    }
}
