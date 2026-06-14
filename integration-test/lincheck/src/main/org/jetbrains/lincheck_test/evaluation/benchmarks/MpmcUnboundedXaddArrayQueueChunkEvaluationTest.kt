package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jctools.queues.MpmcUnboundedXaddArrayQueue
import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase

object MpmcUnboundedXaddArrayQueueChunkEvaluationTest : BenchmarkCase {
    override val name: String = "VariableMpmcUnboundedXaddArrayQueueChunk"

    @Param(name = "value", gen = IntGen::class, conf = "1:5")
    class Scenario {
        private val queue = MpmcUnboundedXaddArrayQueue<Int>(2)

        @Operation
        fun offer(@Param(name = "value") value: Int): Boolean =
            queue.offer(value)

        @Operation
        fun poll(): Int? = queue.poll()

        @Operation
        fun relaxedPoll(): Int? = queue.relaxedPoll()

        @Operation
        fun peek(): Int? = queue.peek()

        @Operation
        fun relaxedPeek(): Int? = queue.relaxedPeek()
    }

    override fun runModelChecking() {
        VariableEvalOptions.modelChecking().check(Scenario::class)
    }
}
