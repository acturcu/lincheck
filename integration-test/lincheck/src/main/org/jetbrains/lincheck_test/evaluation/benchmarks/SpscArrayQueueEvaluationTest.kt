package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jctools.queues.SpscArrayQueue
import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object SpscArrayQueueEvaluationTest : BenchmarkCase {
    override val name: String = "SpscArrayQueue"

    @Param(name = "value", gen = IntGen::class, conf = "1:3")
    class Scenario {
        private val q = SpscArrayQueue<Int>(4)

        @Operation(nonParallelGroup = "producer")
        fun offer(@Param(name = "value") value: Int): Boolean = q.offer(value)

        @Operation(nonParallelGroup = "consumer")
        fun poll(): Int? = q.poll()

        @Operation(nonParallelGroup = "consumer")
        fun peek(): Int? = q.peek()
    }

    private fun modelCheckingOptions() =
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking())
            .threads(2)


    override fun runModelChecking() {
        modelCheckingOptions().check(Scenario::class)
    }
}
