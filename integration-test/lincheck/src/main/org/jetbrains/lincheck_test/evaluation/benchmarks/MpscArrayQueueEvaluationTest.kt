package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jctools.queues.MpscArrayQueue
import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object MpscArrayQueueEvaluationTest : BenchmarkCase {
    override val name: String = "MpscArrayQueue"

    /**
     * MPSC queue: multiple producers, single consumer.
     * The consumer operation is marked as non-parallel to respect this contract.
     */
    @Param(name = "value", gen = IntGen::class, conf = "1:3")
    class Scenario {
        private val q = MpscArrayQueue<Int>(4)

        @Operation
        fun offer(@Param(name = "value") value: Int): Boolean = q.offer(value)

        @Operation(nonParallelGroup = "consumer")
        fun poll(): Int? = q.poll()

        @Operation
        fun isEmpty(): Boolean = q.isEmpty
    }

    private fun modelCheckingOptions() =
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking())
            .threads(3)


    override fun runModelChecking() {
        modelCheckingOptions().check(Scenario::class)
    }
}
