package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jctools.queues.MpscGrowableArrayQueue
import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase

object MpscGrowableArrayQueueExpansionEvaluationTest : BenchmarkCase {
    override val name: String = "VariableMpscGrowableArrayQueueExpansion"

    @Param(name = "value", gen = IntGen::class, conf = "1:5")
    class Scenario {
        private val queue = MpscGrowableArrayQueue<Int>(2, 8)

        @Operation
        fun offer(@Param(name = "value") value: Int): Boolean =
            queue.offer(value)

        @Operation
        fun relaxedOffer(@Param(name = "value") value: Int): Boolean =
            queue.relaxedOffer(value)

        @Operation(nonParallelGroup = "consumer")
        fun poll(): Int? = queue.poll()

        @Operation(nonParallelGroup = "consumer")
        fun relaxedPoll(): Int? = queue.relaxedPoll()

        @Operation(nonParallelGroup = "consumer")
        fun peek(): Int? = queue.peek()
    }

    override fun runModelChecking() {
        VariableEvalOptions.modelChecking().check(Scenario::class)
    }
}
