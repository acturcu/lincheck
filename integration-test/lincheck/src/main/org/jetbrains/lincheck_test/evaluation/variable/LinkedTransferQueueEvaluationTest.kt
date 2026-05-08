package org.jetbrains.lincheck_test.evaluation.variable

import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import java.util.concurrent.LinkedTransferQueue

object LinkedTransferQueueEvaluationTest : BenchmarkCase {
    override val name: String = "VariableLinkedTransferQueue"

    @Param(name = "value", gen = IntGen::class, conf = "1:4")
    class Scenario {
        private val queue = LinkedTransferQueue<Int>()

        @Operation
        fun offer(@Param(name = "value") value: Int): Boolean =
            queue.offer(value)

        @Operation
        fun tryTransfer(@Param(name = "value") value: Int): Boolean =
            queue.tryTransfer(value)

        @Operation
        fun poll(): Int? = queue.poll()

        @Operation
        fun peek(): Int? = queue.peek()

        @Operation
        fun remove(@Param(name = "value") value: Int): Boolean =
            queue.remove(value)
    }

    override fun runModelChecking() {
        VariableEvalOptions.modelChecking(invocationsPerIteration = 5_000, analyzeStdLib = true).check(Scenario::class)
    }
}
